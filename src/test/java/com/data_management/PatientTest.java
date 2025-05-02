package com.data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.List;
import java.util.Map;

/**
 * Tests for the Patient class.
 */
public class PatientTest {

    private Patient patient;
    private static final int PATIENT_ID = 123;
    private static final long BASE_TIME = System.currentTimeMillis();
    
    @BeforeEach
    public void setup() {
        // Create a new patient for each test
        patient = new Patient(PATIENT_ID);
    }
    
    @Test
    @DisplayName("Test patient creation")
    public void testPatientCreation() {
        assertEquals(PATIENT_ID, patient.getPatientId(), "Patient ID should match");
        assertTrue(patient.getAllRecords().isEmpty(), "New patient should have no records");
    }
    
    @Test
    @DisplayName("Test adding record without additional info")
    public void testAddRecord() {
        // Add a simple record
        double value = 98.6;
        String type = "Temperature";
        long timestamp = BASE_TIME;
        
        patient.addRecord(value, type, timestamp);
        
        // Verify record was added
        List<PatientRecord> records = patient.getAllRecords();
        assertEquals(1, records.size(), "Should have 1 record");
        
        PatientRecord record = records.get(0);
        assertEquals(PATIENT_ID, record.getPatientId(), "Record should have correct patient ID");
        assertEquals(value, record.getMeasurementValue(), "Record should have correct value");
        assertEquals(type, record.getRecordType(), "Record should have correct type");
        assertEquals(timestamp, record.getTimestamp(), "Record should have correct timestamp");
        assertEquals("", record.getAdditionalInfo(), "Record should have empty additional info");
    }
    
    @Test
    @DisplayName("Test adding record with additional info")
    public void testAddRecordWithAdditionalInfo() {
        // Add a record with additional info
        double value = 120.0;
        String type = "BloodPressure";
        long timestamp = BASE_TIME;
        String info = "Standing position";
        
        patient.addRecord(value, type, timestamp, info);
        
        // Verify record was added with additional info
        List<PatientRecord> records = patient.getAllRecords();
        assertEquals(1, records.size(), "Should have 1 record");
        
        PatientRecord record = records.get(0);
        assertEquals(info, record.getAdditionalInfo(), "Record should have correct additional info");
    }
    
    @Test
    @DisplayName("Test getting records by time range")
    public void testGetRecords() {
        // Add records at different times
        patient.addRecord(98.6, "Temperature", BASE_TIME - 2000);
        patient.addRecord(99.1, "Temperature", BASE_TIME - 1000);
        patient.addRecord(99.5, "Temperature", BASE_TIME);
        
        // Test getting all records
        List<PatientRecord> allRecords = patient.getRecords(BASE_TIME - 3000, BASE_TIME + 1000);
        assertEquals(3, allRecords.size(), "Should get all 3 records");
        
        // Test getting some records
        List<PatientRecord> middleRecords = patient.getRecords(BASE_TIME - 1500, BASE_TIME - 500);
        assertEquals(1, middleRecords.size(), "Should get 1 record in middle range");
        assertEquals(99.1, middleRecords.get(0).getMeasurementValue(), "Should get correct middle record");
        
        // Test empty range
        List<PatientRecord> noRecords = patient.getRecords(BASE_TIME + 1000, BASE_TIME + 2000);
        assertTrue(noRecords.isEmpty(), "Should get no records for future range");
    }
    
    @Test
    @DisplayName("Test getting records by exact timestamp")
    public void testGetRecordsByExactTime() {
        // Add a record
        patient.addRecord(98.6, "Temperature", BASE_TIME);
        
        // Test getting record by exact timestamp
        List<PatientRecord> records = patient.getRecords(BASE_TIME, BASE_TIME);
        assertEquals(1, records.size(), "Should get record with exact timestamp match");
    }
    
    @Test
    @DisplayName("Test getting records with invalid time range")
    public void testGetRecordsInvalidRange() {
        // Add a record
        patient.addRecord(98.6, "Temperature", BASE_TIME);
        
        // Test getting records with end time before start time
        List<PatientRecord> records = patient.getRecords(BASE_TIME + 1000, BASE_TIME - 1000);
        assertTrue(records.isEmpty(), "Should get no records for invalid time range");
    }
    
    @Test
    @DisplayName("Test getting records by type")
    public void testGetRecordsByType() {
        // Add records of different types
        patient.addRecord(98.6, "Temperature", BASE_TIME - 2000);
        patient.addRecord(120.0, "BloodPressure", BASE_TIME - 1000);
        patient.addRecord(99.1, "Temperature", BASE_TIME);
        
        // Get records by type
        Map<String, List<PatientRecord>> recordsByType = patient.getRecordsByType();
        
        // Check number of types
        assertEquals(2, recordsByType.size(), "Should have 2 record types");
        
        // Check records for each type
        List<PatientRecord> temperatureRecords = recordsByType.get("Temperature");
        assertEquals(2, temperatureRecords.size(), "Should have 2 temperature records");
        
        List<PatientRecord> bloodPressureRecords = recordsByType.get("BloodPressure");
        assertEquals(1, bloodPressureRecords.size(), "Should have 1 blood pressure record");
    }
    
    @Test
    @DisplayName("Test getting all records")
    public void testGetAllRecords() {
        // Add multiple records
        patient.addRecord(98.6, "Temperature", BASE_TIME - 2000);
        patient.addRecord(120.0, "BloodPressure", BASE_TIME - 1000);
        patient.addRecord(99.1, "Temperature", BASE_TIME);
        
        // Get all records
        List<PatientRecord> allRecords = patient.getAllRecords();
        
        // Check count
        assertEquals(3, allRecords.size(), "Should get all 3 records");
        
        // Check records are not affected by modifying returned list
        allRecords.clear();
        assertEquals(3, patient.getAllRecords().size(), "Original records should remain unaffected");
    }
    
    @Test
    @DisplayName("Test edge case with many records")
    public void testManyRecords() {
        // Add many records
        for (int i = 0; i < 1000; i++) {
            patient.addRecord(98.0 + (i * 0.01), "Temperature", BASE_TIME + i);
        }
        
        // Verify all records were added
        assertEquals(1000, patient.getAllRecords().size(), "Should have 1000 records");
        
        // Verify getting records in time range works with many records
        List<PatientRecord> middleRecords = patient.getRecords(BASE_TIME + 400, BASE_TIME + 600);
        assertEquals(201, middleRecords.size(), "Should get correct number of records in range");
    }
}