package com.cardio_generator.generators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import com.cardio_generator.outputs.OutputStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the ECGDataGenerator class.
 */
public class ECGDataGeneratorTest {
    
    private static final int TEST_PATIENT_COUNT = 10;
    private ECGDataGenerator generator;
    
    @BeforeEach
    public void setUp() {
        generator = new ECGDataGenerator(TEST_PATIENT_COUNT);
    }
    
    @Test
    @DisplayName("Test constructor initializes generator correctly")
    public void testConstructor() {
        assertNotNull(generator, "Generator should be created successfully");
    }
    
    @Test
    @DisplayName("Test generate method produces ECG values")
    public void testGenerate() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate data for a patient
        int patientId = 5;
        generator.generate(patientId, outputStrategy);
        
        // Verify that we have ECG output
        boolean hasECG = false;
        
        for (OutputRecord record : outputStrategy.getRecords()) {
            if (record.patientId == patientId && "ECG".equals(record.recordType)) {
                hasECG = true;
                break;
            }
        }
        
        assertTrue(hasECG, "Should generate ECG data");
    }
    
    @Test
    @DisplayName("Test ECG values are within physiologically reasonable ranges")
    public void testValueRanges() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate multiple data points to check ranges
        for (int patientId = 1; patientId <= TEST_PATIENT_COUNT; patientId++) {
            generator.generate(patientId, outputStrategy);
        }
        
        // Check all ECG values (typically heart rates between 30-200 bpm)
        for (OutputRecord record : outputStrategy.getRecords()) {
            if ("ECG".equals(record.recordType)) {
                double value = Double.parseDouble(record.value);
                assertTrue(value >= 30 && value <= 200, 
                    "ECG value should be in reasonable range: " + value);
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