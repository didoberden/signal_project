package com.data_management;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.CountDownLatch;

import static org.junit.jupiter.api.Assertions.*;

public class PatientDataWebSocketClientTest {
    
    private static TestWebSocketServer server;
    private static final int PORT = 8090;
    private static final String SERVER_URI = "ws://localhost:" + PORT;
    private PatientDataWebSocketClient client;
    private DataStorage dataStorage;
    
    @BeforeAll
    public static void startServer() throws InterruptedException {
        // Start the test server
        server = new TestWebSocketServer(PORT);
        server.start();
        
        // Allow time for the server to start
        Thread.sleep(1000);
    }
    
    @AfterAll
    public static void stopServer() throws InterruptedException {
        // Stop the test server
        server.stop(1000);
    }
    
    @BeforeEach
    public void setUp() {
        dataStorage = DataStorage.getInstance();
        dataStorage.reset();
        client = new PatientDataWebSocketClient(SERVER_URI);
    }
    
    @AfterEach
    public void tearDown() {
        client.close();
    }
    
    @Test
    public void testConnection() throws IOException {
        // Test connection to the server
        client.readData(dataStorage);
        assertTrue(client.isConnected(), "Client should be connected to the WebSocket server");
    }
    
    @Test
    public void testDataProcessing() throws IOException, InterruptedException {
        // Establish connection
        client.readData(dataStorage);
          // Send test data from the server
        final String testMessage = "1234,1621234567890,HeartRate,85.5";
        
        // Send a message from the server to the client
        server.broadcast(testMessage);
        
        // Wait for data processing
        Thread.sleep(500);
        
        // Verify data was properly stored
        Patient patient = getPatient(1234);
        assertNotNull(patient, "Patient should be created and stored in DataStorage");
        
        // Verify record was added with correct values
        assertEquals(1, patient.getRecords(0, Long.MAX_VALUE).size(), "Patient should have one record");
        PatientRecord record = patient.getRecords(0, Long.MAX_VALUE).get(0);
        assertEquals(85.5, record.getMeasurementValue(), 0.001, "Measurement value should match");
        assertEquals("HeartRate", record.getRecordType(), "Record type should match");
        assertEquals(1621234567890L, record.getTimestamp(), "Timestamp should match");
    }
    
    @Test
    public void testMultipleMessages() throws IOException, InterruptedException {
        // Establish connection
        client.readData(dataStorage);
        
        // Send multiple test messages from the server
        server.broadcast("1234,1621234567890,HeartRate,85.5");
        server.broadcast("1234,1621234567891,Temperature,37.2");
        server.broadcast("5678,1621234567892,BloodPressure,120.0");
        
        // Wait for data processing
        Thread.sleep(1000);
        
        // Verify first patient data
        Patient patient1 = getPatient(1234);
        assertNotNull(patient1, "Patient 1 should be created");
        assertEquals(2, patient1.getRecords(0, Long.MAX_VALUE).size(), "Patient 1 should have two records");
        
        // Verify second patient data
        Patient patient2 = getPatient(5678);
        assertNotNull(patient2, "Patient 2 should be created");
        assertEquals(1, patient2.getRecords(0, Long.MAX_VALUE).size(), "Patient 2 should have one record");
    }
    
    @Test
    public void testMalformedMessage() throws IOException, InterruptedException {
        // Establish connection
        client.readData(dataStorage);
        
        // Send valid message followed by malformed message
        server.broadcast("1234,1621234567890,HeartRate,85.5");
        server.broadcast("malformed_data");
        
        // Wait for data processing
        Thread.sleep(500);
        
        // The valid data should be processed, and the malformed data should be handled gracefully
        Patient patient = getPatient(1234);
        assertNotNull(patient, "Patient should be created despite malformed message");
        assertEquals(1, patient.getRecords(0, Long.MAX_VALUE).size(), "Only the valid record should be stored");
    }
    
    @Test
    public void testConnectionFailure() {
        // Try to connect to a non-existent server
        PatientDataWebSocketClient invalidClient = new PatientDataWebSocketClient("ws://localhost:9999", 2);
        
        // Connection should throw an IOException
        IOException exception = assertThrows(IOException.class, () -> {
            invalidClient.readData(dataStorage);
        });
        
        // Verify the exception message
        assertTrue(exception.getMessage().contains("Failed to connect"), "Exception should indicate connection failure");
    }
    
    /**
     * Helper method to get a patient by ID from the DataStorage.
     */
    private Patient getPatient(int patientId) {
        for (Patient patient : dataStorage.getAllPatients()) {
            if (patient.getPatientId() == patientId) {
                return patient;
            }
        }
        return null;
    }
    
    /**
     * Simple WebSocket server for testing.
     */
    private static class TestWebSocketServer extends WebSocketServer {
        
        public TestWebSocketServer(int port) {
            super(new InetSocketAddress(port));
        }
        
        @Override
        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            System.out.println("Test server: New connection from " + conn.getRemoteSocketAddress());
        }
        
        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            System.out.println("Test server: Connection closed");
        }
        
        @Override
        public void onMessage(WebSocket conn, String message) {
            // Echo messages back in tests if needed
            System.out.println("Test server: Received message: " + message);
        }
        
        @Override
        public void onError(WebSocket conn, Exception ex) {
            System.err.println("Test server error: " + ex.getMessage());
        }
        
        @Override
        public void onStart() {
            System.out.println("Test WebSocket server started on port: " + getPort());
        }
    }
}
