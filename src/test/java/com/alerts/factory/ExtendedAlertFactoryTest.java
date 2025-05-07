package com.alerts.factory;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.alerts.Alert;
import com.alerts.AlertSeverity;
import com.alerts.AlertType;

public class ExtendedAlertFactoryTest {

    @Test
    @DisplayName("Test high diastolic BP alert factory")
    public void testHighDiastolicBPAlert() {
        AlertFactory factory = AlertFactory.getFactory("bloodpressure");
        
        Alert alert = factory.createAlert(123, "high_diastolic", System.currentTimeMillis(), 125.0);
        
        assertEquals(AlertType.HIGH_DIASTOLIC_BP, alert.getType(), 
            "Should create high diastolic BP alert");
        assertEquals(AlertSeverity.HIGH, alert.getSeverity(), 
            "High diastolic BP should have HIGH severity");
        assertTrue(alert.getMessage().contains("125.0 mmHg"), 
            "Message should include the measurement value");
    }
    
    @Test
    @DisplayName("Test low diastolic BP alert factory")
    public void testLowDiastolicBPAlert() {
        AlertFactory factory = AlertFactory.getFactory("bloodpressure");
        
        Alert alert = factory.createAlert(123, "low_diastolic", System.currentTimeMillis(), 55.0);
        
        assertEquals(AlertType.LOW_DIASTOLIC_BP, alert.getType(), 
            "Should create low diastolic BP alert");
        assertEquals(AlertSeverity.MEDIUM, alert.getSeverity(), 
            "Low diastolic BP should have MEDIUM severity");
    }
    
    @Test
    @DisplayName("Test BP trend alert factories")
    public void testBPTrendAlerts() {
        AlertFactory factory = AlertFactory.getFactory("bloodpressure");
        
        Alert increasingAlert = factory.createAlert(123, "increasing_trend", System.currentTimeMillis(), 150.0);
        assertEquals(AlertType.BP_INCREASING_TREND, increasingAlert.getType(),
            "Should create BP increasing trend alert");
        
        Alert decreasingAlert = factory.createAlert(123, "decreasing_trend", System.currentTimeMillis(), 110.0);
        assertEquals(AlertType.BP_DECREASING_TREND, decreasingAlert.getType(),
            "Should create BP decreasing trend alert");
    }
    
    
    @Test
    @DisplayName("Test combined alert factory")
    public void testCombinedAlertFactory() {
        AlertFactory factory = new CombinedAlertFactory();
        
        Alert alert = factory.createAlert(123, "hypotensive_hypoxemia", System.currentTimeMillis(), 0);
        
        assertEquals(AlertType.HYPOTENSIVE_HYPOXEMIA, alert.getType(),
            "Should create hypotensive hypoxemia alert");
        assertEquals(AlertSeverity.CRITICAL, alert.getSeverity(),
            "Combined alert should have CRITICAL severity");
    }
    
    @Test
    @DisplayName("Test exception for unknown condition")
    public void testUnknownCondition() {
        AlertFactory[] factories = {
            AlertFactory.getFactory("bloodpressure"),
            AlertFactory.getFactory("bloodoxygen"),
            AlertFactory.getFactory("ecg"),
            AlertFactory.getFactory("manual"),
            new CombinedAlertFactory()
        };
        
        for (AlertFactory factory : factories) {
            assertThrows(IllegalArgumentException.class, () -> {
                factory.createAlert(123, "unknown_condition", System.currentTimeMillis(), 0);
            }, "Should throw exception for unknown condition");
        }
    }
}