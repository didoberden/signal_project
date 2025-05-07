package com.alerts.strategy;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import com.alerts.Alert;
import com.alerts.AlertType;
import com.data_management.PatientRecord;

public class AlertStrategyTest {
    
    private static final int PATIENT_ID = 123;
    private static final long CURRENT_TIME = System.currentTimeMillis();
    
    @Test
    @DisplayName("Test BloodPressureStrategy with high systolic pressure")
    public void testHighSystolicPressure() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 185.0, "SystolicBP", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for high systolic BP");
        assertEquals(AlertType.HIGH_SYSTOLIC_BP, alert.getType(), 
            "Should be high systolic BP alert");
    }
    
    @Test
    @DisplayName("Test BloodPressureStrategy with low systolic pressure")
    public void testLowSystolicPressure() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 85.0, "SystolicBP", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for low systolic BP");
        assertEquals(AlertType.LOW_SYSTOLIC_BP, alert.getType(), 
            "Should be low systolic BP alert");
    }
    
    @Test
    @DisplayName("Test BloodPressureStrategy with normal systolic pressure")
    public void testNormalSystolicPressure() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 120.0, "SystolicBP", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify no alert was generated
        assertNull(alert, "Should not generate an alert for normal systolic BP");
    }
    
    @Test
    @DisplayName("Test BloodPressureStrategy with high diastolic pressure")
    public void testHighDiastolicPressure() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 125.0, "DiastolicBP", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for high diastolic BP");
        assertEquals(AlertType.HIGH_DIASTOLIC_BP, alert.getType(), 
            "Should be high diastolic BP alert");
    }

    @Test
    @DisplayName("Test BloodPressureStrategy with low diastolic pressure")
    public void testLowDiastolicPressure() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 55.0, "DiastolicBP", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for low diastolic BP");
        assertEquals(AlertType.LOW_DIASTOLIC_BP, alert.getType(), 
            "Should be low diastolic BP alert");
    }
    
    @Test
    @DisplayName("Test BloodPressureStrategy with BP trend")
    public void testBPTrend() {
        // Create strategy
        BloodPressureStrategy strategy = new BloodPressureStrategy();
        
        // Create test records showing increasing trend
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 140.0, "SystolicBP", CURRENT_TIME - 3000));
        records.add(new PatientRecord(PATIENT_ID, 155.0, "SystolicBP", CURRENT_TIME - 2000));
        records.add(new PatientRecord(PATIENT_ID, 170.0, "SystolicBP", CURRENT_TIME - 1000));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for BP trend");
        assertEquals(AlertType.BP_INCREASING_TREND, alert.getType(), 
            "Should be BP increasing trend alert");
    }
    
    @Test
    @DisplayName("Test HeartRateStrategy with abnormal ECG")
    public void testHeartRateStrategy() {
        // Create strategy
        HeartRateStrategy strategy = new HeartRateStrategy();
        
        // Create test records (20 needed for window size)
        List<PatientRecord> records = new ArrayList<>();
        
        // Add 19 records with normal values
        for (int i = 0; i < 19; i++) {
            records.add(new PatientRecord(PATIENT_ID, 70.0, "ECG", CURRENT_TIME - (19 - i) * 1000));
        }
        
        // Add 1 abnormal value
        records.add(new PatientRecord(PATIENT_ID, 120.0, "ECG", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for abnormal ECG");
        assertEquals(AlertType.ECG_ABNORMAL_PEAK, alert.getType(), 
            "Should be ECG abnormal peak alert");
    }
    
    @Test
    @DisplayName("Test HeartRateStrategy with normal ECG")
    public void testHeartRateStrategyNormal() {
        // Create strategy
        HeartRateStrategy strategy = new HeartRateStrategy();
        
        // Create test records (20 needed for window size)
        List<PatientRecord> records = new ArrayList<>();
        
        // Add 20 records with normal values
        for (int i = 0; i < 20; i++) {
            records.add(new PatientRecord(PATIENT_ID, 70.0 + (Math.random() * 2 - 1), "ECG", CURRENT_TIME - (20 - i) * 1000));
        }
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify no alert was generated
        assertNull(alert, "Should not generate an alert for normal ECG");
    }

    @Test
    @DisplayName("Test OxygenSaturationStrategy with low oxygen")
    public void testLowOxygenSaturation() {
        // Create strategy
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 91.0, "OxygenSaturation", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for low oxygen saturation");
        assertEquals(AlertType.LOW_OXYGEN_SATURATION, alert.getType(), 
            "Should be low oxygen saturation alert");
    }
    
    @Test
    @DisplayName("Test OxygenSaturationStrategy with normal oxygen")
    public void testNormalOxygenSaturation() {
        // Create strategy
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        
        // Create test records
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 96.0, "OxygenSaturation", CURRENT_TIME));
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify no alert was generated
        assertNull(alert, "Should not generate an alert for normal oxygen saturation");
    }
    
    @Test
    @DisplayName("Test OxygenSaturationStrategy with rapid drop")
    public void testRapidOxygenSaturationDrop() {
        // Create strategy
        OxygenSaturationStrategy strategy = new OxygenSaturationStrategy();
        
        // Create test records showing a drop
        List<PatientRecord> records = new ArrayList<>();
        records.add(new PatientRecord(PATIENT_ID, 98.0, "OxygenSaturation", CURRENT_TIME - 500000)); // 8.3 min ago
        records.add(new PatientRecord(PATIENT_ID, 92.0, "OxygenSaturation", CURRENT_TIME)); // Drop of 6%
        
        // Check alert
        Alert alert = strategy.checkAlert(PATIENT_ID, records);
        
        // Verify alert was generated
        assertNotNull(alert, "Should generate an alert for rapid oxygen drop");
        assertEquals(AlertType.RAPID_OXYGEN_DROP, alert.getType(), 
            "Should be rapid oxygen drop alert");
    }
}