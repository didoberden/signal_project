package com.cardio_generator.generators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import com.cardio_generator.outputs.OutputStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the BloodSaturationDataGenerator class.
 */
public class BloodSaturationDataGeneratorTest {
    
    private static final int TEST_PATIENT_COUNT = 10;
    private BloodSaturationDataGenerator generator;
    
    @BeforeEach
    public void setUp() {
        generator = new BloodSaturationDataGenerator(TEST_PATIENT_COUNT);
    }
    
    @Test
    @DisplayName("Test constructor initializes generator correctly")
    public void testConstructor() {
        assertNotNull(generator, "Generator should be created successfully");
    }
    
    @Test
    @DisplayName("Test generate method produces saturation values")
    public void testGenerate() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate data for a patient
        int patientId = 5;
        generator.generate(patientId, outputStrategy);
        
        // Verify that we have saturation output
        boolean hasSaturation = false;
        
        for (OutputRecord record : outputStrategy.getRecords()) {
            if (record.patientId == patientId && "Saturation".equals(record.recordType)) {
                hasSaturation = true;
                break;
            }
        }
        
        assertTrue(hasSaturation, "Should generate Saturation data");
    }
    
    @Test
    @DisplayName("Test saturation values are within physiologically reasonable ranges")
    public void testValueRanges() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate multiple data points to check ranges
        for (int patientId = 1; patientId <= TEST_PATIENT_COUNT; patientId++) {
            generator.generate(patientId, outputStrategy);
        }
        
        // Check all saturation values (typically 80-100%)
        for (OutputRecord record : outputStrategy.getRecords()) {
            if ("Saturation".equals(record.recordType)) {
                double value = Double.parseDouble(record.value);
                assertTrue(value >= 80 && value <= 100, 
                    "Saturation value should be in reasonable range: " + value);
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