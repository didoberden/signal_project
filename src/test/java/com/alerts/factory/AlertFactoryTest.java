package com.alerts.factory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.alerts.AlertType;
import com.alerts.AlertSeverity;

public class AlertFactoryTest {

    @Test
    @DisplayName("Test Blood Pressure Alert Factory")
    public void testBloodPressureAlertFactory() {
        AlertFactory factory = AlertFactory.getFactory("bloodpressure");
        
        // Test high systolic
        Alert highSystolicAlert = factory.createAlert(123, "high_systolic", System.currentTimeMillis(), 185.0);
        assertEquals(AlertType.HIGH_SYSTOLIC_BP, highSystolicAlert.getType());
        assertEquals(AlertSeverity.CRITICAL, highSystolicAlert.getSeverity());
        
        // Test low systolic
        Alert lowSystolicAlert = factory.createAlert(123, "low_systolic", System.currentTimeMillis(), 85.0);
        assertEquals(AlertType.LOW_SYSTOLIC_BP, lowSystolicAlert.getType());
        assertEquals(AlertSeverity.HIGH, lowSystolicAlert.getSeverity());
        
        // Test invalid condition
        assertThrows(IllegalArgumentException.class, () -> {
            factory.createAlert(123, "invalid_condition", System.currentTimeMillis(), 120.0);
        });
    }
    
    @Test
    @DisplayName("Test Blood Oxygen Alert Factory")
    public void testBloodOxygenAlertFactory() {
        AlertFactory factory = AlertFactory.getFactory("bloodoxygen");
        
        Alert lowOxygenAlert = factory.createAlert(123, "low_saturation", System.currentTimeMillis(), 90.0);
        assertEquals(AlertType.LOW_OXYGEN_SATURATION, lowOxygenAlert.getType());
        assertEquals(AlertSeverity.HIGH, lowOxygenAlert.getSeverity());
    }
    
    @Test
    @DisplayName("Test ECG Alert Factory")
    public void testECGAlertFactory() {
        AlertFactory factory = AlertFactory.getFactory("ecg");
        
        Alert ecgAlert = factory.createAlert(123, "abnormal_peak", System.currentTimeMillis(), 120.0);
        assertEquals(AlertType.ECG_ABNORMAL_PEAK, ecgAlert.getType());
        assertEquals(AlertSeverity.HIGH, ecgAlert.getSeverity());
    }
    
    @Test
    @DisplayName("Test Manual Alert Factory")
    public void testManualAlertFactory() {
        AlertFactory factory = AlertFactory.getFactory("manual");
        
        Alert manualAlert = factory.createAlert(123, "triggered", System.currentTimeMillis(), 1.0);
        assertEquals(AlertType.MANUAL_TRIGGER, manualAlert.getType());
        assertEquals(AlertSeverity.HIGH, manualAlert.getSeverity());
    }
    
    @Test
    @DisplayName("Test Factory Selection")
    public void testFactorySelection() {
        assertTrue(AlertFactory.getFactory("bloodpressure") instanceof BloodPressureAlertFactory);
        assertTrue(AlertFactory.getFactory("bloodoxygen") instanceof BloodOxygenAlertFactory);
        assertTrue(AlertFactory.getFactory("ecg") instanceof ECGAlertFactory);
        assertTrue(AlertFactory.getFactory("manual") instanceof ManualAlertFactory);
        
        assertThrows(IllegalArgumentException.class, () -> {
            AlertFactory.getFactory("unknown");
        });
    }
}