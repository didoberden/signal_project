package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Factory for creating blood oxygen related alerts.
 */
public class BloodOxygenAlertFactory extends AlertFactory {
    
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp, double value) {
        switch(condition) {
            case "low_saturation":
                return new Alert(
                    patientId,
                    AlertType.LOW_OXYGEN_SATURATION,
                    "Low oxygen saturation: " + value + "%",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            case "rapid_drop":
                return new Alert(
                    patientId,
                    AlertType.RAPID_OXYGEN_DROP,
                    "Rapid drop in oxygen saturation of " + String.format("%.1f", value) + "% within 10 minutes",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            default:
                throw new IllegalArgumentException("Unknown blood oxygen condition: " + condition);
        }
    }
}