package com.data_management;

/**
 * Represents a single medical record for a patient.
 * Each record contains a measurement value, record type, and timestamp.
 */
public class PatientRecord {
    private int patientId;
    private double measurementValue;
    private String recordType;
    private long timestamp;
    private String additionalInfo;

    /**
     * Constructs a new PatientRecord with the specified parameters.
     *
     * @param patientId       the ID of the patient this record belongs to
     * @param measurementValue the value of the measurement recorded
     * @param recordType      the type of record, e.g., "HeartRate", "BloodPressure"
     * @param timestamp       the time at which the measurement was taken, in milliseconds since UNIX epoch
     */
    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
        this.additionalInfo = "";
    }
    
    /**
     * Constructs a new PatientRecord with additional information.
     *
     * @param patientId the ID of the patient this record belongs to
     * @param measurementValue the value of the measurement recorded
     * @param recordType the type of record
     * @param timestamp the time at which the measurement was taken
     * @param additionalInfo additional information for the record
     */
    public PatientRecord(int patientId, double measurementValue, String recordType, long timestamp, String additionalInfo) {
        this.patientId = patientId;
        this.measurementValue = measurementValue;
        this.recordType = recordType;
        this.timestamp = timestamp;
        this.additionalInfo = additionalInfo;
    }

    /**
     * Returns the patient ID associated with this record.
     *
     * @return the patient ID
     */
    public int getPatientId() {
        return patientId;
    }

    /**
     * Returns the measurement value stored in this record.
     *
     * @return the measurement value
     */
    public double getMeasurementValue() {
        return measurementValue;
    }

    /**
     * Returns the type of this record.
     *
     * @return the record type
     */
    public String getRecordType() {
        return recordType;
    }

    /**
     * Returns the timestamp of this record.
     *
     * @return the timestamp, in milliseconds since UNIX epoch
     */
    public long getTimestamp() {
        return timestamp;
    }
    
    /**
     * Returns additional information associated with this record.
     *
     * @return additional information string
     */
    public String getAdditionalInfo() {
        return additionalInfo;
    }
    
    /**
     * Sets additional information for this record.
     *
     * @param additionalInfo the information to set
     */
    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }
}
