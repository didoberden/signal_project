package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Factory for creating manually triggered alerts.
 */
public class ManualAlertFactory extends AlertFactory {
    
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp, double value) {
        switch(condition) {
            case "triggered":
                return new Alert(
                    patientId,
                    AlertType.MANUAL_TRIGGER,
                    "Manual alert triggered by patient or staff",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            default:
                throw new IllegalArgumentException("Unknown manual alert condition: " + condition);
        }
    }
}