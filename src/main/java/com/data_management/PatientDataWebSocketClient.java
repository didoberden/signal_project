package com.data_management;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of DataReader that reads data from a WebSocket server in real-time.
 * Connects to a WebSocketOutputStrategy server to process patient data continuously.
 */
public class PatientDataWebSocketClient implements DataReader {
    
    private final String serverUri;
    private final int connectionTimeoutSeconds;
    private WebSocketClient client;
    private boolean isConnected = false;
    private DataStorage dataStorage;
    
    /**
     * Constructs a PatientDataWebSocketClient with the specified server URI.
     *
     * @param serverUri the URI of the WebSocket server, e.g., "ws://localhost:8080"
     */
    public PatientDataWebSocketClient(String serverUri) {
        this(serverUri, 10); // Default 10 second timeout
    }
    
    /**
     * Constructs a PatientDataWebSocketClient with the specified server URI and connection timeout.
     *
     * @param serverUri the URI of the WebSocket server, e.g., "ws://localhost:8080"
     * @param connectionTimeoutSeconds timeout in seconds for connection attempts
     */
    public PatientDataWebSocketClient(String serverUri, int connectionTimeoutSeconds) {
        this.serverUri = serverUri;
        this.connectionTimeoutSeconds = connectionTimeoutSeconds;
    }
    
    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        this.dataStorage = dataStorage;
        try {
            // Create connection latch to wait for connection
            final CountDownLatch connectLatch = new CountDownLatch(1);
            
            // Create and configure the WebSocket client
            client = createWebSocketClient(new URI(serverUri), connectLatch);
            client.connect();
            
            // Wait for connection or timeout
            boolean connected = connectLatch.await(connectionTimeoutSeconds, TimeUnit.SECONDS);
            if (!connected) {
                throw new IOException("Failed to connect to WebSocket server at " + serverUri + " within " + connectionTimeoutSeconds + " seconds");
            }
            
            // The client is now connected and will process messages asynchronously
            isConnected = true;
            
        } catch (URISyntaxException e) {
            throw new IOException("Invalid WebSocket URI: " + serverUri, e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Connection interrupted while waiting for WebSocket server", e);
        }
    }
    
    /**
     * Stops the WebSocket client connection.
     */
    public void close() {
        if (client != null && isConnected) {
            client.close();
            isConnected = false;
        }
    }
    
    /**
     * Creates a WebSocket client with configured handlers.
     *
     * @param uri the URI of the WebSocket server
     * @param connectLatch latch to signal successful connection
     * @return configured WebSocket client
     */
    private WebSocketClient createWebSocketClient(URI uri, CountDownLatch connectLatch) {
        return new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                System.out.println("WebSocket connection established with: " + uri.toString());
                connectLatch.countDown();
            }

            @Override
            public void onMessage(String message) {
                // Process received message
                try {
                    processMessage(message);
                } catch (Exception e) {
                    System.err.println("Error processing WebSocket message: " + e.getMessage());
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                System.out.println("WebSocket connection closed: " + reason + " (code: " + code + ")");
                isConnected = false;
                
                // Attempt to reconnect if closed unexpectedly by the server
                if (remote && dataStorage != null) {
                    System.out.println("Attempting to reconnect...");
                    try {
                        reconnect();
                    } catch (Exception e) {
                        System.err.println("Failed to reconnect: " + e.getMessage());
                    }
                }
            }

            @Override
            public void onError(Exception ex) {
                System.err.println("WebSocket error: " + ex.getMessage());
                ex.printStackTrace();
            }
        };
    }
    
    /**
     * Processes a message received from the WebSocket server.
     * Expected format: patientId,timestamp,recordType,value
     *
     * @param message the message received from the server
     */
    private void processMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return;
        }
        
        // Parse the message and add the data to storage
        // Format: patientId,timestamp,recordType,value
        String[] parts = message.split(",");
        if (parts.length >= 4) {
            try {                int patientId = Integer.parseInt(parts[0].trim());
                long timestamp = Long.parseLong(parts[1].trim());
                String recordType = parts[2].trim();
                double measurementValue = Double.parseDouble(parts[3].trim());
                
                // Add the data to storage
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing WebSocket message: " + message + " - " + e.getMessage());
            }
        } else {
            System.err.println("Received malformed message: " + message);
        }
    }
    
    /**
     * Checks if the client is currently connected to the WebSocket server.
     *
     * @return true if connected, false otherwise
     */
    public boolean isConnected() {
        return isConnected;
    }
}
