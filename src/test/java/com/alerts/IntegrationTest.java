package com.alerts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;  // Add this import
import com.alerts.decorator.PriorityAlertDecorator;
import com.alerts.decorator.RepeatedAlertDecorator;
import com.alerts.factory.AlertFactory;
import com.alerts.strategy.BloodPressureStrategy;

public class IntegrationTest {
    
    private DataStorage dataStorage;
    private AlertGenerator alertGenerator;
    
    @BeforeEach
    public void setup() {
        // Get the singleton instance and clear data
        dataStorage = DataStorage.getInstance();
        dataStorage.clearAllData();
        
        // Create the alert generator
        alertGenerator = new AlertGenerator(dataStorage);
    }
    
    @Test
    @DisplayName("Test integration of factory, strategy, and decorator patterns")
    public void testPatternIntegration() {
        // Create test patient
        Patient patient = new Patient(123);
        
        // Use factory to create alert
        AlertFactory factory = AlertFactory.getFactory("bloodpressure");
        Alert baseAlert = factory.createAlert(123, "high_systolic", System.currentTimeMillis(), 185.0);
        
        // Use strategy to check for alert condition
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        patient.addRecord(185.0, "SystolicBP", System.currentTimeMillis());
        List<PatientRecord> records = patient.getAllRecords();
        Alert strategyAlert = strategy.checkAlert(patient.getPatientId(), records);
        
        // Compare factory and strategy alerts
        assertNotNull(strategyAlert, "Strategy should generate alert");
        assertEquals(baseAlert.getType(), strategyAlert.getType(), 
            "Factory and strategy should create same alert type");
        
        // Decorate the alert
        Alert priorityAlert = new PriorityAlertDecorator(baseAlert, "Patient has cardiac history");
        
        // Verify decorator enhanced alert
        assertEquals(AlertSeverity.CRITICAL, priorityAlert.getSeverity(), 
            "Severity should be escalated to CRITICAL");
        assertTrue(priorityAlert.getMessage().contains("PRIORITY"), 
            "Message should include priority indicator");
        
        // Send to alert generator to process
        alertGenerator.evaluateData(patient);
        
        // Verify alert was triggered in the system
        List<Alert> activeAlerts = alertGenerator.getActiveAlertsForPatient(123);
        assertFalse(activeAlerts.isEmpty(), "Should have active alerts");
        assertEquals(AlertType.HIGH_SYSTOLIC_BP, activeAlerts.get(0).getType(),
            "Should have high systolic BP alert");
    }
}