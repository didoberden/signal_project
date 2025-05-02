package com.data_management;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a patient and manages their medical records.
 * This class stores patient-specific data, allowing for the addition and
 * retrieval of medical records based on specified criteria.
 */
public class Patient {
    private int patientId;
    private List<PatientRecord> patientRecords;

    /**
     * Constructs a new Patient with a specified ID.
     * Initializes an empty list of patient records.
     *
     * @param patientId the unique identifier for the patient
     */
    public Patient(int patientId) {
        this.patientId = patientId;
        this.patientRecords = new ArrayList<>();
    }

    /**
     * Returns the patient's unique ID.
     *
     * @return the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Adds a new record to this patient's list of medical records.
     * The record is created with the specified measurement value, record type, and
     * timestamp.
     *
     * @param measurementValue the measurement value to store in the record
     * @param recordType       the type of record, e.g., "HeartRate",
     *                         "BloodPressure"
     * @param timestamp        the time at which the measurement was taken, in
     *                         milliseconds since UNIX epoch
     */
    public void addRecord(double measurementValue, String recordType, long timestamp) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp);
        this.patientRecords.add(record);
    }

    /**
     * Adds a new record with additional information.
     * 
     * @param measurementValue the measurement value to store
     * @param recordType the type of record
     * @param timestamp the time at which the measurement was taken
     * @param additionalInfo additional information associated with the record
     */
    public void addRecord(double measurementValue, String recordType, long timestamp, String additionalInfo) {
        PatientRecord record = new PatientRecord(this.patientId, measurementValue, recordType, timestamp, additionalInfo);
        this.patientRecords.add(record);
    }

    /**
     * Retrieves a list of PatientRecord objects for this patient that fall within a
     * specified time range.
     * The method filters records based on the start and end times provided.
     *
     * @param startTime the start of the time range, in milliseconds since UNIX
     *                  epoch
     * @param endTime   the end of the time range, in milliseconds since UNIX epoch
     * @return a list of PatientRecord objects that fall within the specified time
     *         range
     */
    public List<PatientRecord> getRecords(long startTime, long endTime) {
        List<PatientRecord> filteredRecords = new ArrayList<>();
        for (PatientRecord record : patientRecords) {
            if (record.getTimestamp() >= startTime && record.getTimestamp() <= endTime) {
                filteredRecords.add(record);
            }
        }
        return filteredRecords;
    }
    
    /**
     * Gets all records for this patient organized by record type.
     * 
     * @return a map of record types to lists of records
     */
    public Map<String, List<PatientRecord>> getRecordsByType() {
        Map<String, List<PatientRecord>> recordsByType = new HashMap<>();
        
        for (PatientRecord record : patientRecords) {
            String recordType = record.getRecordType();
            if (!recordsByType.containsKey(recordType)) {
                recordsByType.put(recordType, new ArrayList<>());
            }
            recordsByType.get(recordType).add(record);
        }
        
        return recordsByType;
    }
    
    /**
     * Gets all records for this patient.
     * 
     * @return a list of all patient records
     */
    public List<PatientRecord> getAllRecords() {
        return new ArrayList<>(patientRecords);
    }
}
