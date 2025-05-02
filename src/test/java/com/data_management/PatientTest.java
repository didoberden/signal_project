package com.data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

/**
 * Tests for the Patient class
 */
public class PatientTest {

    @Test
    public void testPatient() {
        // Create patient
        Patient p = new Patient(100);
        
        // Test ID
        assertEquals(100, p.getPatientId());
        
        // Add some records
        p.addRecord(98.6, "Temperature", 1000);
        p.addRecord(120.0, "BloodPressure", 2000);
        
        // Test getting all records
        assertEquals(2, p.getAllRecords().size());
    }
    
    @Test
    public void testGetRecords() {
        Patient patient = new Patient(100);
        patient.addRecord(98.6, "Temperature", 1000);
        patient.addRecord(99.1, "Temperature", 2000);
        patient.addRecord(99.5, "Temperature", 3000);
        
        // Test time range
        List<PatientRecord> records = patient.getRecords(1000, 3000);
        assertEquals(3, records.size());
        
        // Test another range
        records = patient.getRecords(1500, 2500);
        assertEquals(1, records.size());
    }
    
    @Test
    public void testAdditionalInfo() {
        // Test adding a record with additional info
        Patient patient = new Patient(100);
        patient.addRecord(120.0, "BloodPressure", 1000, "Sitting position");
        
        List<PatientRecord> records = patient.getAllRecords();
        assertEquals("Sitting position", records.get(0).getAdditionalInfo());
    }
    
    @Test
    public void testGetRecordsByType() {
        Patient patient = new Patient(100);
        
        // Add records
        patient.addRecord(98.6, "Temperature", 1000);
        patient.addRecord(120.0, "BloodPressure", 2000);
        patient.addRecord(99.0, "Temperature", 3000);
        
        // Get by type
        Map<String, List<PatientRecord>> byType = patient.getRecordsByType();
        
        // Check results
        assertEquals(2, byType.get("Temperature").size());
        assertEquals(1, byType.get("BloodPressure").size());
    }
}