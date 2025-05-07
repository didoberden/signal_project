package com.alerts.decorator;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

public class AlertDecoratorTest {

    @Test
    @DisplayName("Test PriorityAlertDecorator functionality")
    public void testPriorityAlertDecorator() {
        // Create a base alert
        Alert baseAlert = new Alert(123, AlertType.HIGH_SYSTOLIC_BP, 
            "High systolic blood pressure: 185.0 mmHg", 
            System.currentTimeMillis(), AlertSeverity.MEDIUM);
        
        // Create a priority decorator with a reason
        String reason = "Patient has cardiac history";
        PriorityAlertDecorator priorityAlert = new PriorityAlertDecorator(baseAlert, reason);
        
        // Test decorator behavior
        assertEquals(AlertSeverity.HIGH, priorityAlert.getSeverity(), 
            "Severity should be escalated from MEDIUM to HIGH");
        assertTrue(priorityAlert.getMessage().contains("PRIORITY"), 
            "Message should indicate priority status");
        assertTrue(priorityAlert.getMessage().contains(reason), 
            "Message should include priority reason");
        assertEquals(baseAlert.getPatientId(), priorityAlert.getPatientId(),
            "Patient ID should be preserved");
        assertEquals(baseAlert.getType(), priorityAlert.getType(),
            "Alert type should be preserved");
    }

    @Test
    @DisplayName("Test RepeatedAlertDecorator functionality")
    public void testRepeatedAlertDecorator() {
        // Create a base alert
        Alert baseAlert = new Alert(456, AlertType.LOW_OXYGEN_SATURATION,
            "Low oxygen saturation: 90.0%",
            System.currentTimeMillis(), AlertSeverity.HIGH);
        
        // Create a repeated decorator
        long interval = 1000;
        int maxCount = 3;
        RepeatedAlertDecorator repeatedAlert = new RepeatedAlertDecorator(baseAlert, interval, maxCount);
        
        // Test decorator behavior
        assertTrue(repeatedAlert.getMessage().contains("[REPEAT"),
            "Message should indicate repeat status");
        assertEquals(baseAlert.getPatientId(), repeatedAlert.getPatientId(),
            "Patient ID should be preserved");
        assertEquals(baseAlert.getType(), repeatedAlert.getType(),
            "Alert type should be preserved");
        
        // Clean up the timer
        repeatedAlert.cancelRepeats();
    }

    @Test
    @DisplayName("Test escalation of all severity levels")
    public void testSeverityEscalation() {
        // Test LOW to MEDIUM
        Alert lowAlert = new Alert(123, AlertType.LOW_DIASTOLIC_BP, "Test", 
            System.currentTimeMillis(), AlertSeverity.LOW);
        PriorityAlertDecorator lowToMedium = new PriorityAlertDecorator(lowAlert, "Test");
        assertEquals(AlertSeverity.MEDIUM, lowToMedium.getSeverity(),
            "LOW severity should escalate to MEDIUM");
        
        // Test MEDIUM to HIGH
        Alert mediumAlert = new Alert(123, AlertType.LOW_DIASTOLIC_BP, "Test", 
            System.currentTimeMillis(), AlertSeverity.MEDIUM);
        PriorityAlertDecorator mediumToHigh = new PriorityAlertDecorator(mediumAlert, "Test");
        assertEquals(AlertSeverity.HIGH, mediumToHigh.getSeverity(),
            "MEDIUM severity should escalate to HIGH");
        
        // Test HIGH to CRITICAL
        Alert highAlert = new Alert(123, AlertType.LOW_DIASTOLIC_BP, "Test", 
            System.currentTimeMillis(), AlertSeverity.HIGH);
        PriorityAlertDecorator highToCritical = new PriorityAlertDecorator(highAlert, "Test");
        assertEquals(AlertSeverity.CRITICAL, highToCritical.getSeverity(),
            "HIGH severity should escalate to CRITICAL");
        
        // Test CRITICAL remains CRITICAL
        Alert criticalAlert = new Alert(123, AlertType.LOW_DIASTOLIC_BP, "Test", 
            System.currentTimeMillis(), AlertSeverity.CRITICAL);
        PriorityAlertDecorator criticalToCritical = new PriorityAlertDecorator(criticalAlert, "Test");
        assertEquals(AlertSeverity.CRITICAL, criticalToCritical.getSeverity(),
            "CRITICAL severity should remain CRITICAL");
    }
    
    @Test
    @DisplayName("Test combining multiple decorators")
    public void testDecoratorCombination() {
        // Create a base alert
        Alert baseAlert = new Alert(123, AlertType.HIGH_SYSTOLIC_BP, 
            "High systolic blood pressure: 185.0 mmHg", 
            System.currentTimeMillis(), AlertSeverity.MEDIUM);
        
        // Apply priority decorator
        PriorityAlertDecorator priorityAlert = new PriorityAlertDecorator(baseAlert, "Patient history");
        
        // Apply repeated decorator on top of priority decorator
        RepeatedAlertDecorator combinedAlert = new RepeatedAlertDecorator(priorityAlert, 1000, 2);
        
        // Test the combined behavior
        assertEquals(AlertSeverity.HIGH, combinedAlert.getSeverity(),
            "Should have escalated severity from MEDIUM to HIGH");
        assertTrue(combinedAlert.getMessage().contains("PRIORITY"),
            "Message should contain PRIORITY indicator");
        assertTrue(combinedAlert.getMessage().contains("[REPEAT"),
            "Message should contain REPEAT indicator");
        
        // Clean up the timer
        combinedAlert.cancelRepeats();
    }
}