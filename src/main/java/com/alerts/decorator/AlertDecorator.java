package com.alerts.decorator;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

/**
 * Base decorator class for the Alert interface.
 * Implements the Alert interface and contains an instance of Alert.
 */
public abstract class AlertDecorator extends Alert {
    
    protected Alert wrappedAlert;
    
    /**
     * Creates a new AlertDecorator wrapping the given alert.
     *
     * @param alert the alert to decorate
     */
    public AlertDecorator(Alert alert) {
        super(alert.getPatientId(), alert.getType(), alert.getMessage(), 
              alert.getTimestamp(), alert.getSeverity());
        this.wrappedAlert = alert;
    }
    
    @Override
    public int getPatientId() {
        return wrappedAlert.getPatientId();
    }
    
    @Override
    public AlertType getType() {
        return wrappedAlert.getType();
    }
    
    @Override
    public String getMessage() {
        return wrappedAlert.getMessage();
    }
    
    @Override
    public long getTimestamp() {
        return wrappedAlert.getTimestamp();
    }
    
    @Override
    public AlertSeverity getSeverity() {
        return wrappedAlert.getSeverity();
    }
    
    @Override
    public void setSeverity(AlertSeverity severity) {
        wrappedAlert.setSeverity(severity);
        super.setSeverity(severity);
    }
}