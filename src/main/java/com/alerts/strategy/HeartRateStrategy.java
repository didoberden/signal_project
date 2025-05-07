package com.alerts.strategy;

import java.util.List;
import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;
import com.data_management.PatientRecord;

/**
 * Strategy for monitoring heart rate.
 * Monitors for abnormal heart rates.
 */
public class HeartRateStrategy implements AlertStrategy {

    // Alert thresholds for ECG
    private static final int ECG_WINDOW_SIZE = 20; // Size of sliding window for ECG analysis
    private static final double ECG_ABNORMAL_THRESHOLD = 2.0; // Multiple of standard deviation
    
    @Override
    public Alert checkAlert(int patientId, List<PatientRecord> records) {
        if (records == null || records.size() < ECG_WINDOW_SIZE) {
            return null;
        }
        
        // Get the most recent window of ECG data
        List<PatientRecord> window = records.subList(records.size() - ECG_WINDOW_SIZE, records.size());
        
        // Calculate mean and standard deviation
        double sum = 0, sumOfSquares = 0;
        
        for (PatientRecord record : window) {
            double value = record.getMeasurementValue();
            sum += value;
            sumOfSquares += value * value;
        }
        
        double mean = sum / ECG_WINDOW_SIZE;
        double variance = (sumOfSquares / ECG_WINDOW_SIZE) - (mean * mean);
        double stdDev = Math.sqrt(variance);
        
        // Check the most recent value against the mean + threshold * stdDev
        PatientRecord latest = window.get(window.size() - 1);
        double latestValue = latest.getMeasurementValue();
        
        if (Math.abs(latestValue - mean) > ECG_ABNORMAL_THRESHOLD * stdDev) {
            return new Alert(
                patientId,
                AlertType.ECG_ABNORMAL_PEAK,
                "Abnormal ECG peak detected: " + latestValue + " (exceeds threshold)",
                latest.getTimestamp(),
                AlertSeverity.HIGH
            );
        }
        
        return null; // No alert needed
    }
}