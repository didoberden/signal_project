package com.alerts.factory;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Factory for creating blood pressure related alerts.
 */
public class BloodPressureAlertFactory extends AlertFactory {
    
    @Override
    public Alert createAlert(int patientId, String condition, long timestamp, double value) {
        switch(condition) {
            case "high_systolic":
                return new Alert(
                    patientId,
                    AlertType.HIGH_SYSTOLIC_BP,
                    "Critical high systolic blood pressure: " + value + " mmHg",
                    timestamp,
                    AlertSeverity.CRITICAL
                );
                
            case "low_systolic":
                return new Alert(
                    patientId,
                    AlertType.LOW_SYSTOLIC_BP,
                    "Critical low systolic blood pressure: " + value + " mmHg",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            case "high_diastolic":
                return new Alert(
                    patientId,
                    AlertType.HIGH_DIASTOLIC_BP,
                    "Critical high diastolic blood pressure: " + value + " mmHg",
                    timestamp,
                    AlertSeverity.HIGH
                );
                
            case "low_diastolic":
                return new Alert(
                    patientId,
                    AlertType.LOW_DIASTOLIC_BP,
                    "Critical low diastolic blood pressure: " + value + " mmHg",
                    timestamp,
                    AlertSeverity.MEDIUM
                );
                
            case "increasing_trend":
                return new Alert(
                    patientId,
                    AlertType.BP_INCREASING_TREND,
                    "Increasing trend in blood pressure detected",
                    timestamp,
                    AlertSeverity.MEDIUM
                );
                
            case "decreasing_trend":
                return new Alert(
                    patientId,
                    AlertType.BP_DECREASING_TREND,
                    "Decreasing trend in blood pressure detected",
                    timestamp,
                    AlertSeverity.MEDIUM
                );
                
            default:
                throw new IllegalArgumentException("Unknown blood pressure condition: " + condition);
        }
    }
}