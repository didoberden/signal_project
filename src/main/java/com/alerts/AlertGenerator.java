package com.alerts;

import java.util.*;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;
import com.alerts.factory.AlertFactory;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;
    private Map<Integer, Map<AlertType, Alert>> activeAlerts = new HashMap<>();
    
    // Store record history for trend analysis
    private Map<Integer, Map<String, List<PatientRecord>>> patientRecordHistory = new HashMap<>();
    
    // Alert thresholds for blood pressure (systolic/diastolic)
    private static final int HIGH_SYSTOLIC_BP_THRESHOLD = 180;
    private static final int LOW_SYSTOLIC_BP_THRESHOLD = 90;
    private static final int HIGH_DIASTOLIC_BP_THRESHOLD = 120;
    private static final int LOW_DIASTOLIC_BP_THRESHOLD = 60;
    private static final int BP_TREND_CHANGE_THRESHOLD = 10;
    private static final int BP_TREND_CONSECUTIVE_READINGS = 3;
    
    // Alert thresholds for oxygen saturation
    private static final double LOW_OXYGEN_THRESHOLD = 92.0;
    private static final double OXYGEN_DROP_THRESHOLD = 5.0;
    private static final long OXYGEN_DROP_TIME_WINDOW_MS = 10 * 60 * 1000; // 10 minutes
    
    // Alert thresholds for ECG
    private static final int ECG_WINDOW_SIZE = 20; // Size of sliding window for ECG analysis
    private static final double ECG_ABNORMAL_THRESHOLD = 2.0; // Multiple of standard deviation

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                   data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert} method.
     *
     * @param patient the patient data to evaluate for alert conditions
     */
    public void evaluateData(Patient patient) {
        int patientId = patient.getPatientId();
        
        // Initialize patient record history if needed
        if (!patientRecordHistory.containsKey(patientId)) {
            patientRecordHistory.put(patientId, new HashMap<>());
        }
        
        // Initialize active alerts for this patient if needed
        if (!activeAlerts.containsKey(patientId)) {
            activeAlerts.put(patientId, new HashMap<>());
        }
        
        // Get the records for each vital sign and update history
        updatePatientHistory(patient);
        
        // Get latest data from data storage to ensure we have the most up-to-date information
        long currentTime = System.currentTimeMillis();
        long twentyFourHoursAgo = currentTime - (24 * 60 * 60 * 1000);
        List<PatientRecord> recentStorageRecords = dataStorage.getRecords(patientId, twentyFourHoursAgo, currentTime);
        
        // Also add the records from storage to our history
        if (!recentStorageRecords.isEmpty()) {
            for (PatientRecord record : recentStorageRecords) {
                String recordType = record.getRecordType();
                
                if (!patientRecordHistory.get(patientId).containsKey(recordType)) {
                    patientRecordHistory.get(patientId).put(recordType, new ArrayList<>());
                }
                
                List<PatientRecord> typeHistory = patientRecordHistory.get(patientId).get(recordType);
                
                // Check if record already exists in history
                boolean exists = typeHistory.stream()
                    .anyMatch(r -> r.getTimestamp() == record.getTimestamp());
                    
                if (!exists) {
                    typeHistory.add(record);
                }
            }
            
            // Re-sort all record types by timestamp after adding storage records
            for (String recordType : patientRecordHistory.get(patientId).keySet()) {
                patientRecordHistory.get(patientId).get(recordType).sort(
                    Comparator.comparingLong(PatientRecord::getTimestamp)
                );
            }
        }
        
        // Process each type of alert
        checkBloodPressureAlerts(patientId);
        checkOxygenSaturationAlerts(patientId);
        checkCombinedAlerts(patientId);
        checkECGAlerts(patientId);
        checkManuallyTriggeredAlerts(patientId);
        
        // Print a summary of active alerts for the patient
        Map<AlertType, Alert> patientAlerts = activeAlerts.get(patientId);
        if (patientAlerts != null && !patientAlerts.isEmpty()) {
            System.out.println("Patient #" + patientId + " has " + patientAlerts.size() + 
                              " active alert(s): " + patientAlerts.keySet());
        }
    }
    
    /**
     * Updates the patient's record history with new data.
     * 
     * @param patient the patient whose history to update
     */
    private void updatePatientHistory(Patient patient) {
        int patientId = patient.getPatientId();
        Map<String, List<PatientRecord>> recordsByType = patient.getRecordsByType();
        
        for (Map.Entry<String, List<PatientRecord>> entry : recordsByType.entrySet()) {
            String recordType = entry.getKey();
            List<PatientRecord> records = entry.getValue();
            
            if (!records.isEmpty()) {
                if (!patientRecordHistory.get(patientId).containsKey(recordType)) {
                    patientRecordHistory.get(patientId).put(recordType, new ArrayList<>());
                }
                
                List<PatientRecord> history = patientRecordHistory.get(patientId).get(recordType);
                
                // Keep history manageable by limiting size
                if (history.size() > 100) {
                    history = history.subList(history.size() - 100, history.size());
                }
                
                // Add new records
                for (PatientRecord record : records) {
                    // Add only if not already in history (by timestamp comparison)
                    boolean exists = history.stream()
                        .anyMatch(r -> r.getTimestamp() == record.getTimestamp());
                    
                    if (!exists) {
                        history.add(record);
                    }
                }
                
                // Sort history by timestamp
                history.sort(Comparator.comparingLong(PatientRecord::getTimestamp));
                patientRecordHistory.get(patientId).put(recordType, history);
            }
        }
    }
    
    /**
     * Check for blood pressure related alerts.
     * 
     * @param patientId the ID of the patient to check
     */
    private void checkBloodPressureAlerts(int patientId) {
        Map<String, List<PatientRecord>> recordsByType = patientRecordHistory.get(patientId);
        if (recordsByType == null) return;
        
        List<PatientRecord> systolicRecords = recordsByType.get("SystolicBP");
        List<PatientRecord> diastolicRecords = recordsByType.get("DiastolicBP");
        
        // Check systolic blood pressure
        if (systolicRecords != null && !systolicRecords.isEmpty()) {
            PatientRecord latestSystolic = systolicRecords.get(systolicRecords.size() - 1);
            double systolicValue = latestSystolic.getMeasurementValue();
            long timestamp = latestSystolic.getTimestamp();
            
            // Critical high systolic BP
            if (systolicValue >= HIGH_SYSTOLIC_BP_THRESHOLD) {
                AlertFactory factory = AlertFactory.getFactory("bloodpressure");
                Alert alert = factory.createAlert(patientId, "high_systolic", timestamp, systolicValue);
                triggerAlert(alert);
            } else {
                resolveAlert(patientId, AlertType.HIGH_SYSTOLIC_BP);
            }
            
            // Critical low systolic BP
            if (systolicValue <= LOW_SYSTOLIC_BP_THRESHOLD) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.LOW_SYSTOLIC_BP,
                    "Critical low systolic blood pressure: " + systolicValue + " mmHg",
                    timestamp,
                    AlertSeverity.HIGH
                ));
            } else {
                resolveAlert(patientId, AlertType.LOW_SYSTOLIC_BP);
            }
            
            // Check for systolic BP trend
            if (systolicRecords.size() >= BP_TREND_CONSECUTIVE_READINGS) {
                checkBPTrend(patientId, systolicRecords, "systolic");
            }
        }
        
        // Check diastolic blood pressure
        if (diastolicRecords != null && !diastolicRecords.isEmpty()) {
            PatientRecord latestDiastolic = diastolicRecords.get(diastolicRecords.size() - 1);
            double diastolicValue = latestDiastolic.getMeasurementValue();
            long timestamp = latestDiastolic.getTimestamp();
            
            // Critical high diastolic BP
            if (diastolicValue >= HIGH_DIASTOLIC_BP_THRESHOLD) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.HIGH_DIASTOLIC_BP,
                    "Critical high diastolic blood pressure: " + diastolicValue + " mmHg",
                    timestamp,
                    AlertSeverity.HIGH
                ));
            } else {
                resolveAlert(patientId, AlertType.HIGH_DIASTOLIC_BP);
            }
            
            // Critical low diastolic BP
            if (diastolicValue <= LOW_DIASTOLIC_BP_THRESHOLD) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.LOW_DIASTOLIC_BP,
                    "Critical low diastolic blood pressure: " + diastolicValue + " mmHg",
                    timestamp,
                    AlertSeverity.MEDIUM
                ));
            } else {
                resolveAlert(patientId, AlertType.LOW_DIASTOLIC_BP);
            }
            
            // Check for diastolic BP trend
            if (diastolicRecords.size() >= BP_TREND_CONSECUTIVE_READINGS) {
                checkBPTrend(patientId, diastolicRecords, "diastolic");
            }
        }
    }
    
    /**
     * Check for blood pressure trends (increasing or decreasing).
     * 
     * @param patientId the ID of the patient
     * @param records the BP records to check
     * @param bpType whether this is "systolic" or "diastolic" pressure
     */
    private void checkBPTrend(int patientId, List<PatientRecord> records, String bpType) {
        int size = records.size();
        
        // Get the last BP_TREND_CONSECUTIVE_READINGS readings
        List<PatientRecord> recentReadings = records.subList(size - BP_TREND_CONSECUTIVE_READINGS, size);
        
        boolean increasing = true;
        boolean decreasing = true;
        
        // Check if each reading changes by more than BP_TREND_CHANGE_THRESHOLD
        for (int i = 1; i < recentReadings.size(); i++) {
            double current = recentReadings.get(i).getMeasurementValue();
            double previous = recentReadings.get(i - 1).getMeasurementValue();
            
            // For increasing trend
            if (current - previous <= BP_TREND_CHANGE_THRESHOLD) {
                increasing = false;
            }
            
            // For decreasing trend
            if (previous - current <= BP_TREND_CHANGE_THRESHOLD) {
                decreasing = false;
            }
        }
        
        PatientRecord latest = recentReadings.get(recentReadings.size() - 1);
        
        // Handle increasing trend alert
        if (increasing) {
            triggerAlert(new Alert(
                patientId,
                AlertType.BP_INCREASING_TREND,
                "Increasing trend in " + bpType + " blood pressure detected over " + BP_TREND_CONSECUTIVE_READINGS + " readings",
                latest.getTimestamp(),
                AlertSeverity.MEDIUM
            ));
        } else {
            resolveAlert(patientId, AlertType.BP_INCREASING_TREND);
        }
        
        // Handle decreasing trend alert
        if (decreasing) {
            triggerAlert(new Alert(
                patientId,
                AlertType.BP_DECREASING_TREND,
                "Decreasing trend in " + bpType + " blood pressure detected over " + BP_TREND_CONSECUTIVE_READINGS + " readings",
                latest.getTimestamp(),
                AlertSeverity.MEDIUM
            ));
        } else {
            resolveAlert(patientId, AlertType.BP_DECREASING_TREND);
        }
    }
    
    /**
     * Check for oxygen saturation related alerts.
     * 
     * @param patientId the ID of the patient to check
     */
    private void checkOxygenSaturationAlerts(int patientId) {
        Map<String, List<PatientRecord>> recordsByType = patientRecordHistory.get(patientId);
        if (recordsByType == null) return;
        
        List<PatientRecord> oxygenRecords = recordsByType.get("OxygenSaturation");
        
        if (oxygenRecords != null && !oxygenRecords.isEmpty()) {
            PatientRecord latestOxygen = oxygenRecords.get(oxygenRecords.size() - 1);
            double oxygenValue = latestOxygen.getMeasurementValue();
            long timestamp = latestOxygen.getTimestamp();
            
            // Low oxygen saturation alert
            if (oxygenValue < LOW_OXYGEN_THRESHOLD) {
                AlertFactory factory = AlertFactory.getFactory("bloodoxygen");
                Alert alert = factory.createAlert(patientId, "low_saturation", timestamp, oxygenValue);
                triggerAlert(alert);
            } else {
                resolveAlert(patientId, AlertType.LOW_OXYGEN_SATURATION);
            }
            
            // Check for rapid drop in oxygen
            if (oxygenRecords.size() >= 2) {
                checkOxygenRapidDrop(patientId, oxygenRecords);
            }
        }
    }
    
    /**
     * Check for rapid drops in oxygen saturation.
     * 
     * @param patientId the ID of the patient
     * @param records the oxygen saturation records to check
     */
    private void checkOxygenRapidDrop(int patientId, List<PatientRecord> records) {
        PatientRecord latest = records.get(records.size() - 1);
        double latestValue = latest.getMeasurementValue();
        long latestTime = latest.getTimestamp();
        
        // Find readings within the time window
        for (int i = records.size() - 2; i >= 0; i--) {
            PatientRecord earlier = records.get(i);
            long earlierTime = earlier.getTimestamp();
            
            // Only check records within the time window
            if (latestTime - earlierTime > OXYGEN_DROP_TIME_WINDOW_MS) {
                break;
            }
            
            double earlierValue = earlier.getMeasurementValue();
            double drop = earlierValue - latestValue;
            
            if (drop >= OXYGEN_DROP_THRESHOLD) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.RAPID_OXYGEN_DROP,
                    "Rapid drop in oxygen saturation of " + String.format("%.1f", drop) + "% within 10 minutes",
                    latestTime,
                    AlertSeverity.HIGH
                ));
                return;
            }
        }
        
        // No rapid drop found
        resolveAlert(patientId, AlertType.RAPID_OXYGEN_DROP);
    }
    
    /**
     * Check for combined alerts such as Hypotensive Hypoxemia.
     * 
     * @param patientId the ID of the patient to check
     */
    private void checkCombinedAlerts(int patientId) {
        Map<String, List<PatientRecord>> recordsByType = patientRecordHistory.get(patientId);
        if (recordsByType == null) return;
        
        List<PatientRecord> systolicRecords = recordsByType.get("SystolicBP");
        List<PatientRecord> oxygenRecords = recordsByType.get("OxygenSaturation");
        
        if (systolicRecords != null && !systolicRecords.isEmpty() && 
            oxygenRecords != null && !oxygenRecords.isEmpty()) {
            
            PatientRecord latestSystolic = systolicRecords.get(systolicRecords.size() - 1);
            PatientRecord latestOxygen = oxygenRecords.get(oxygenRecords.size() - 1);
            
            double systolicValue = latestSystolic.getMeasurementValue();
            double oxygenValue = latestOxygen.getMeasurementValue();
            
            // Check for hypotensive hypoxemia - low BP and low oxygen
            if (systolicValue < LOW_SYSTOLIC_BP_THRESHOLD && oxygenValue < LOW_OXYGEN_THRESHOLD) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.HYPOTENSIVE_HYPOXEMIA,
                    "Critical condition: Hypotensive Hypoxemia detected - Low blood pressure (" + 
                        systolicValue + " mmHg) and low oxygen saturation (" + oxygenValue + "%)",
                    Math.max(latestSystolic.getTimestamp(), latestOxygen.getTimestamp()),
                    AlertSeverity.CRITICAL
                ));
            } else {
                resolveAlert(patientId, AlertType.HYPOTENSIVE_HYPOXEMIA);
            }
        }
    }
    
    /**
     * Check for ECG abnormalities using a sliding window approach.
     * 
     * @param patientId the ID of the patient to check
     */
    private void checkECGAlerts(int patientId) {
        Map<String, List<PatientRecord>> recordsByType = patientRecordHistory.get(patientId);
        if (recordsByType == null) return;
        
        List<PatientRecord> ecgRecords = recordsByType.get("ECG");
        
        if (ecgRecords != null && ecgRecords.size() >= ECG_WINDOW_SIZE) {
            // Get the most recent window of ECG data
            List<PatientRecord> window = ecgRecords.subList(ecgRecords.size() - ECG_WINDOW_SIZE, ecgRecords.size());
            
            // Calculate mean and standard deviation
            double sum = 0, sumOfSquares = 0;
            
            for (PatientRecord record : window) {
                double value = record.getMeasurementValue();
                sum += value;
                sumOfSquares += value * value;
            }
            
            double mean = sum / ECG_WINDOW_SIZE;
            double variance = (sumOfSquares / ECG_WINDOW_SIZE) - (mean * mean);
            double stdDev = Math.sqrt(variance);
            
            // Check the most recent value against the mean + threshold * stdDev
            PatientRecord latest = window.get(window.size() - 1);
            double latestValue = latest.getMeasurementValue();
            
            if (Math.abs(latestValue - mean) > ECG_ABNORMAL_THRESHOLD * stdDev) {
                AlertFactory factory = AlertFactory.getFactory("ecg");
                Alert alert = factory.createAlert(patientId, "abnormal_peak", latest.getTimestamp(), latestValue);
                triggerAlert(alert);
            } else {
                resolveAlert(patientId, AlertType.ECG_ABNORMAL_PEAK);
            }
        }
    }
    
    /**
     * Check for manually triggered alerts.
     * 
     * @param patientId the ID of the patient to check
     */
    private void checkManuallyTriggeredAlerts(int patientId) {
        Map<String, List<PatientRecord>> recordsByType = patientRecordHistory.get(patientId);
        if (recordsByType == null) return;
        
        List<PatientRecord> alertRecords = recordsByType.get("Alert");
        
        if (alertRecords != null && !alertRecords.isEmpty()) {
            PatientRecord latestAlert = alertRecords.get(alertRecords.size() - 1);
            String alertStatus = latestAlert.getAdditionalInfo();
            
            if ("triggered".equalsIgnoreCase(alertStatus)) {
                triggerAlert(new Alert(
                    patientId,
                    AlertType.MANUAL_TRIGGER,
                    "Manual alert triggered by patient or staff",
                    latestAlert.getTimestamp(),
                    AlertSeverity.HIGH
                ));
            } else if ("resolved".equalsIgnoreCase(alertStatus)) {
                resolveAlert(patientId, AlertType.MANUAL_TRIGGER);
            }
        }
    }

    /**
     * Triggers an alert for the monitoring system. If an alert of the same type
     * is already active for the patient, it updates the existing alert.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        int patientId = alert.getPatientId();
        AlertType alertType = alert.getType();
        
        Map<AlertType, Alert> patientAlerts = activeAlerts.get(patientId);
        if (patientAlerts == null) {
            patientAlerts = new HashMap<>();
            activeAlerts.put(patientId, patientAlerts);
        }
        
        // If alert is already active, update it
        if (patientAlerts.containsKey(alertType)) {
            Alert existingAlert = patientAlerts.get(alertType);
            existingAlert.updateAlert(alert.getMessage(), alert.getTimestamp());
        } else {
            // New alert
            patientAlerts.put(alertType, alert);
            System.out.println("ALERT TRIGGERED: " + alert);
            // Here you could add code to notify medical staff, log to file, etc.
        }
    }
    
    /**
     * Resolves an active alert for a patient.
     * 
     * @param patientId ID of the patient
     * @param alertType type of alert to resolve
     */
    private void resolveAlert(int patientId, AlertType alertType) {
        Map<AlertType, Alert> patientAlerts = activeAlerts.get(patientId);
        if (patientAlerts != null && patientAlerts.containsKey(alertType)) {
            Alert alert = patientAlerts.remove(alertType);
            System.out.println("ALERT RESOLVED: " + alert);
            // Here you could add code to notify that the alert is resolved
        }
    }
    
    /**
     * Gets all active alerts for a patient.
     * 
     * @param patientId the patient ID
     * @return list of active alerts or empty list if none
     */
    public List<Alert> getActiveAlertsForPatient(int patientId) {
        Map<AlertType, Alert> patientAlerts = activeAlerts.get(patientId);
        if (patientAlerts != null) {
            return new ArrayList<>(patientAlerts.values());
        }
        return new ArrayList<>();
    }
    
    /**
     * Gets all active alerts in the system.
     * 
     * @return list of all active alerts
     */
    public List<Alert> getAllActiveAlerts() {
        List<Alert> allAlerts = new ArrayList<>();
        for (Map<AlertType, Alert> patientAlerts : activeAlerts.values()) {
            allAlerts.addAll(patientAlerts.values());
        }
        return allAlerts;
    }
}
