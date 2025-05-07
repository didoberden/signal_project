package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Factory for creating alerts based on combined conditions.
 */
public class CombinedAlertFactory extends AlertFactory {
    
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp, double value) {
        switch(condition) {
            case "hypotensive_hypoxemia":
                return new Alert(
                    patientId,
                    AlertType.HYPOTENSIVE_HYPOXEMIA,
                    "Critical condition: Hypotensive Hypoxemia detected - Low blood pressure and low oxygen saturation",
                    timestamp,
                    AlertSeverity.CRITICAL
                );
                
            default:
                throw new IllegalArgumentException("Unknown combined condition: " + condition);
        }
    }
}