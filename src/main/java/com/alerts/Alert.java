package com.alerts;

import java.time.Instant;

/**
 * Represents an alert triggered for a patient based on their health metrics.
 */
public class Alert {
    private final int patientId;
    private final AlertType type;
    private String message;
    private long timestamp;
    private AlertSeverity severity;
    
    /**
     * Constructs a new Alert.
     * 
     * @param patientId ID of the patient
     * @param type type of the alert
     * @param message description of the alert
     * @param timestamp time when the alert was triggered
     * @param severity severity level of the alert
     */
    public Alert(int patientId, AlertType type, String message, long timestamp, AlertSeverity severity) {
        this.patientId = patientId;
        this.type = type;
        this.message = message;
        this.timestamp = timestamp;
        this.severity = severity;
    }
    
    /**
     * Updates an existing alert with a new message and timestamp.
     * 
     * @param message new alert message
     * @param timestamp new alert timestamp
     */
    public void updateAlert(String message, long timestamp) {
        this.message = message;
        this.timestamp = timestamp;
    }
    
    /**
     * Gets the patient ID.
     * 
     * @return patient ID
     */
    public int getPatientId() {
        return patientId;
    }
    
    /**
     * Gets the alert type.
     * 
     * @return alert type
     */
    public AlertType getType() {
        return type;
    }
    
    /**
     * Gets the alert message.
     * 
     * @return alert message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Gets the alert timestamp.
     * 
     * @return alert timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Gets the alert severity.
     * 
     * @return alert severity
     */
    public AlertSeverity getSeverity() {
        return severity;
    }
    
    /**
     * Sets the alert severity.
     * 
     * @param severity the new severity level
     */
    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }
    
    @Override
    public String toString() {
        return "Alert{patientId=" + patientId + 
               ", type=" + type +
               ", message='" + message + "'" +
               ", timestamp=" + Instant.ofEpochMilli(timestamp) +
               ", severity=" + severity + "}";
    }
}
