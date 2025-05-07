package com.alerts.factory;

import com.alerts.Alert;

/**
 * Base AlertFactory class that defines the factory method pattern.
 * Concrete factories will implement the createAlert method to return 
 * specific types of alerts.
 */
public abstract class AlertFactory {
    
    /**
     * Factory method to create an alert based on the specific condition.
     *
     * @param patientId the ID of the patient
     * @param condition the medical condition or specific alert trigger
     * @param timestamp the time when the alert condition was detected
     * @param value the measurement value that triggered the alert
     * @return an appropriate Alert instance
     */
    public abstract Alert createAlert(int patientId, String condition, long timestamp, double value);
    
    /**
     * Creates a factory for the specified alert category.
     *
     * @param category the category of alert to create a factory for
     * @return appropriate AlertFactory implementation
     */
    public static AlertFactory getFactory(String category) {
        switch(category.toLowerCase()) {
            case "bloodpressure":
                return new BloodPressureAlertFactory();
            case "bloodoxygen":
                return new BloodOxygenAlertFactory();
            case "ecg":
                return new ECGAlertFactory();
            case "manual":
                return new ManualAlertFactory();
            default:
                throw new IllegalArgumentException("Unknown alert category: " + category);
        }
    }
}