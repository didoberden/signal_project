package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Factory for creating ECG related alerts.
 */
public class ECGAlertFactory extends AlertFactory {
    
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp, double value) {
        switch(condition) {
            case "abnormal_peak":
                return new Alert(
                    patientId,
                    AlertType.ECG_ABNORMAL_PEAK,
                    "Abnormal ECG peak detected: " + value + " (exceeds normal threshold)",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            default:
                throw new IllegalArgumentException("Unknown ECG condition: " + condition);
        }
    }
}