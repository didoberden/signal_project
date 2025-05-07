package com.cardio_generator.generators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import com.cardio_generator.outputs.OutputStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the BloodPressureDataGenerator class.
 */
public class BloodPressureDataGeneratorTest {
    
    private static final int TEST_PATIENT_COUNT = 10;
    private BloodPressureDataGenerator generator;
    
    @BeforeEach
    public void setUp() {
        generator = new BloodPressureDataGenerator(TEST_PATIENT_COUNT);
    }
    
    @Test
    @DisplayName("Test constructor initializes generator correctly")
    public void testConstructor() {
        assertNotNull(generator, "Generator should be created successfully");
    }
    
    @Test
    @DisplayName("Test generate method produces both systolic and diastolic values")
    public void testGenerate() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate data for a patient
        int patientId = 5;
        generator.generate(patientId, outputStrategy);
        
        // Verify that we have output for both systolic and diastolic
        boolean hasSystolic = false;
        boolean hasDiastolic = false;
        
        for (OutputRecord record : outputStrategy.getRecords()) {
            if (record.patientId == patientId) {
                if ("SystolicPressure".equals(record.recordType)) {
                    hasSystolic = true;
                } else if ("DiastolicPressure".equals(record.recordType)) {
                    hasDiastolic = true;
                }
            }
        }
        
        assertTrue(hasSystolic, "Should generate SystolicPressure data");
        assertTrue(hasDiastolic, "Should generate DiastolicPressure data");
    }
    
    @Test
    @DisplayName("Test values are within physiologically reasonable ranges")
    public void testValueRanges() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate multiple data points to check ranges
        for (int patientId = 1; patientId <= TEST_PATIENT_COUNT; patientId++) {
            generator.generate(patientId, outputStrategy);
        }
        
        // Check all systolic values (typically 90-180)
        for (OutputRecord record : outputStrategy.getRecords()) {
            if ("SystolicPressure".equals(record.recordType)) {
                double value = Double.parseDouble(record.value);
                assertTrue(value >= 80 && value <= 200, 
                    "Systolic value should be in reasonable range: " + value);
            }
            else if ("DiastolicPressure".equals(record.recordType)) {
                double value = Double.parseDouble(record.value);
                assertTrue(value >= 40 && value <= 120, 
                    "Diastolic value should be in reasonable range: " + value);
            }
        }
    }
    
    /**
     * Helper class to record outputs from the generator.
     */
    private static class RecordingOutputStrategy implements OutputStrategy {
        private List<OutputRecord> records = new ArrayList<>();
        
        @Override
        public void output(int patientId, long timestamp, String recordType, String value) {
            records.add(new OutputRecord(patientId, timestamp, recordType, value));
        }
        
        public List<OutputRecord> getRecords() {
            return records;
        }
    }
    
    /**
     * Simple record class to store output data.
     */
    private static class OutputRecord {
        public final int patientId;
        public final long timestamp;
        public final String recordType;
        public final String value;
        
        public OutputRecord(int patientId, long timestamp, String recordType, String value) {
            this.patientId = patientId;
            this.timestamp = timestamp;
            this.recordType = recordType;
            this.value = value;
        }
    }
}