package com.alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.ArrayList;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

/**
 * Tests for the AlertGenerator class.
 */
public class AlertGeneratorTest {

    private AlertGenerator alertGenerator;
    private DataStorage dataStorage;
    private Patient patient;
    private static final int PATIENT_ID = 123;
    
    @BeforeEach
    public void setup() {
        // Create test objects
        dataStorage = new DataStorage();
        alertGenerator = new AlertGenerator(dataStorage);
        patient = new Patient(PATIENT_ID);
    }
    
    @Test
    @DisplayName("Test high systolic blood pressure alert")
    public void testHighSystolicBPAlert() {
        // Add a record with high systolic BP (above threshold)
        patient.addRecord(185.0, "SystolicBP", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        assertTrue(alerts.size() > 0, "Should have at least one alert");
        
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.HIGH_SYSTOLIC_BP) {
                foundAlert = true;
                assertEquals(AlertSeverity.CRITICAL, alert.getSeverity(), 
                        "High systolic BP should have CRITICAL severity");
                break;
            }
        }
        assertTrue(foundAlert, "Should find a HIGH_SYSTOLIC_BP alert");
    }
    
    @Test
    @DisplayName("Test low systolic blood pressure alert")
    public void testLowSystolicBPAlert() {
        // Add a record with low systolic BP (below threshold)
        patient.addRecord(85.0, "SystolicBP", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.LOW_SYSTOLIC_BP) {
                foundAlert = true;
                assertEquals(AlertSeverity.HIGH, alert.getSeverity(), 
                        "Low systolic BP should have HIGH severity");
                break;
            }
        }
        assertTrue(foundAlert, "Should find a LOW_SYSTOLIC_BP alert");
    }
    
    @Test
    @DisplayName("Test high diastolic blood pressure alert")
    public void testHighDiastolicBPAlert() {
        // Add a record with high diastolic BP (above threshold)
        patient.addRecord(130.0, "DiastolicBP", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.HIGH_DIASTOLIC_BP) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a HIGH_DIASTOLIC_BP alert");
    }
    
    @Test
    @DisplayName("Test low diastolic blood pressure alert")
    public void testLowDiastolicBPAlert() {
        // Add a record with low diastolic BP (below threshold)
        patient.addRecord(55.0, "DiastolicBP", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.LOW_DIASTOLIC_BP) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a LOW_DIASTOLIC_BP alert");
    }
    
    @Test
    @DisplayName("Test increasing blood pressure trend alert")
    public void testIncreasingBPTrendAlert() {
        // Add records showing increasing systolic BP
        long now = System.currentTimeMillis();
        patient.addRecord(140.0, "SystolicBP", now - 3000); // First reading
        patient.addRecord(155.0, "SystolicBP", now - 2000); // +15 mmHg
        patient.addRecord(170.0, "SystolicBP", now - 1000); // +15 mmHg
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for trend alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.BP_INCREASING_TREND) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a BP_INCREASING_TREND alert");
    }
    
    @Test
    @DisplayName("Test decreasing blood pressure trend alert")
    public void testDecreasingBPTrendAlert() {
        // Add records showing decreasing systolic BP
        long now = System.currentTimeMillis();
        patient.addRecord(170.0, "SystolicBP", now - 3000); // First reading
        patient.addRecord(155.0, "SystolicBP", now - 2000); // -15 mmHg
        patient.addRecord(140.0, "SystolicBP", now - 1000); // -15 mmHg
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for trend alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.BP_DECREASING_TREND) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a BP_DECREASING_TREND alert");
    }
    
    @Test
    @DisplayName("Test no alert for normal blood pressure")
    public void testNormalBP() {
        // Add a record with normal systolic BP
        patient.addRecord(120.0, "SystolicBP", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Verify no blood pressure alerts
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        for (Alert alert : alerts) {
            assertFalse(alert.getType() == AlertType.HIGH_SYSTOLIC_BP ||
                       alert.getType() == AlertType.LOW_SYSTOLIC_BP,
                       "Should not find any systolic BP alerts");
        }
    }
    
    @Test
    @DisplayName("Test low oxygen saturation alert")
    public void testLowOxygenAlert() {
        // Add a record with low oxygen saturation
        patient.addRecord(90.0, "OxygenSaturation", System.currentTimeMillis());
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.LOW_OXYGEN_SATURATION) {
                foundAlert = true;
                assertEquals(AlertSeverity.HIGH, alert.getSeverity(),
                        "Low oxygen should have HIGH severity");
                break;
            }
        }
        assertTrue(foundAlert, "Should find a LOW_OXYGEN_SATURATION alert");
    }
    
    @Test
    @DisplayName("Test rapid oxygen saturation drop alert")
    public void testRapidOxygenDropAlert() {
        // Add records showing rapid drop in oxygen
        long now = System.currentTimeMillis();
        patient.addRecord(98.0, "OxygenSaturation", now - 500000); // 8.3 min ago
        patient.addRecord(92.0, "OxygenSaturation", now); // Drop of 6%
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.RAPID_OXYGEN_DROP) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a RAPID_OXYGEN_DROP alert");
    }
    
    @Test
    @DisplayName("Test hypotensive hypoxemia combined alert")
    public void testHypotensiveHypoxemiaAlert() {
        // Add records for both low BP and low oxygen
        long now = System.currentTimeMillis();
        patient.addRecord(85.0, "SystolicBP", now);
        patient.addRecord(91.0, "OxygenSaturation", now);
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for combined alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundCombinedAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.HYPOTENSIVE_HYPOXEMIA) {
                foundCombinedAlert = true;
                assertEquals(AlertSeverity.CRITICAL, alert.getSeverity(),
                        "Combined alert should have CRITICAL severity");
                break;
            }
        }
        assertTrue(foundCombinedAlert, "Should find a HYPOTENSIVE_HYPOXEMIA alert");
    }
    
    @Test
    @DisplayName("Test ECG abnormal peak alert")
    public void testECGAbnormalPeakAlert() {
        // Add enough ECG records to establish a baseline
        long now = System.currentTimeMillis();
        for (int i = 0; i < 19; i++) {
            patient.addRecord(70.0 + (Math.random() * 2), "ECG", now - (20 - i) * 1000);
        }
        // Add an abnormal peak
        patient.addRecord(120.0, "ECG", now);
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for ECG alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.ECG_ABNORMAL_PEAK) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find an ECG_ABNORMAL_PEAK alert");
    }
    
    @Test
    @DisplayName("Test manual trigger alert")
    public void testManualTriggerAlert() {
        // Add a manually triggered alert
        patient.addRecord(1.0, "Alert", System.currentTimeMillis(), "triggered");
        
        // Evaluate data
        alertGenerator.evaluateData(patient);
        
        // Check for manual alert
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.MANUAL_TRIGGER) {
                foundAlert = true;
                break;
            }
        }
        assertTrue(foundAlert, "Should find a MANUAL_TRIGGER alert");
    }
    
    @Test
    @DisplayName("Test alert resolution")
    public void testAlertResolution() {
        // First add a record that will trigger an alert
        patient.addRecord(185.0, "SystolicBP", System.currentTimeMillis() - 1000);
        alertGenerator.evaluateData(patient);
        
        // Verify alert was created
        List<Alert> alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean foundInitialAlert = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.HIGH_SYSTOLIC_BP) {
                foundInitialAlert = true;
                break;
            }
        }
        assertTrue(foundInitialAlert, "Should find initial HIGH_SYSTOLIC_BP alert");
        
        // Now add a normal reading that should resolve the alert
        patient.addRecord(120.0, "SystolicBP", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        
        // Verify alert was resolved
        alerts = alertGenerator.getActiveAlertsForPatient(PATIENT_ID);
        boolean alertStillExists = false;
        for (Alert alert : alerts) {
            if (alert.getType() == AlertType.HIGH_SYSTOLIC_BP) {
                alertStillExists = true;
                break;
            }
        }
        assertFalse(alertStillExists, "HIGH_SYSTOLIC_BP alert should be resolved");
    }
    
    
    @Test
    @DisplayName("Test getting all active alerts")
    public void testGetAllActiveAlerts() {
        // Add alerts for multiple patients
        Patient patient2 = new Patient(456);
        
        // Add records for first patient
        patient.addRecord(185.0, "SystolicBP", System.currentTimeMillis());
        alertGenerator.evaluateData(patient);
        
        // Add records for second patient
        patient2.addRecord(91.0, "OxygenSaturation", System.currentTimeMillis());
        alertGenerator.evaluateData(patient2);
        
        // Get all alerts
        List<Alert> allAlerts = alertGenerator.getAllActiveAlerts();
        
        // Should have at least one alert per patient
        assertTrue(allAlerts.size() >= 2, "Should have at least 2 alerts across all patients");
        
        // Verify alerts for different patients
        boolean foundPatient1Alert = false;
        boolean foundPatient2Alert = false;
        
        for (Alert alert : allAlerts) {
            if (alert.getPatientId() == PATIENT_ID) {
                foundPatient1Alert = true;
            } else if (alert.getPatientId() == 456) {
                foundPatient2Alert = true;
            }
        }
        
        assertTrue(foundPatient1Alert, "Should find alert for patient 1");
        assertTrue(foundPatient2Alert, "Should find alert for patient 2");
    }
}