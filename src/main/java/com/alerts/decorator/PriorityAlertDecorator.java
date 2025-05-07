package com.alerts.decorator;

import com.alerts.Alert;
import com.alerts.AlertSeverity;

/**
 * Decorator that adds prioritization to alerts.
 * Increases the priority level of an alert dynamically.
 */
public class PriorityAlertDecorator extends AlertDecorator {
    
    private String priorityReason;
    
    /**
     * Creates a new PriorityAlertDecorator.
     *
     * @param alert the alert to decorate
     * @param priorityReason reason for priority escalation
     */
    public PriorityAlertDecorator(Alert alert, String priorityReason) {
        super(alert);
        this.priorityReason = priorityReason;
        
        // Escalate the severity level
        escalateSeverity();
    }
    
    /**
     * Escalates the severity level by one step.
     */
    private void escalateSeverity() {
        switch (wrappedAlert.getSeverity()) {
            case LOW:
                setSeverity(AlertSeverity.MEDIUM);
                break;
            case MEDIUM:
                setSeverity(AlertSeverity.HIGH);
                break;
            case HIGH:
                setSeverity(AlertSeverity.CRITICAL);
                break;
            case CRITICAL:
                // Already at the highest level
                break;
        }
    }
    
    @Override
    public String getMessage() {
        return "PRIORITY: " + wrappedAlert.getMessage() + " - " + priorityReason;
    }
}