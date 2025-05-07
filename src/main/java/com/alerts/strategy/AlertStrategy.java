package com.alerts.strategy;

import java.util.List;
import com.alerts.Alert;
import com.data_management.PatientRecord;

/**
 * Strategy interface for alert checking algorithms.
 * Defines the contract for different strategies to determine if an alert
 * should be triggered based on patient data.
 */
public interface AlertStrategy {
    
    /**
     * Checks if an alert should be triggered based on the patient's records.
     *
     * @param patientId the ID of the patient
     * @param records the patient records to analyze
     * @return an Alert object if an alert should be triggered, or null if no alert is needed
     */
    Alert checkAlert(int patientId, List<PatientRecord> records);
}