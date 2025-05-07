package com.cardio_generator.generators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import com.cardio_generator.outputs.OutputStrategy;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for the CompositeDataGenerator class.
 */
public class CompositeDataGeneratorTest {
    
    private static final int TEST_PATIENT_COUNT = 10;
    private CompositeDataGenerator compositeGenerator;
    private List<PatientDataGenerator> generators;
    
    @BeforeEach
    public void setUp() {
        generators = new ArrayList<>();
        generators.add(new BloodPressureDataGenerator(TEST_PATIENT_COUNT));
        generators.add(new ECGDataGenerator(TEST_PATIENT_COUNT));
        generators.add(new BloodSaturationDataGenerator(TEST_PATIENT_COUNT));
        
        compositeGenerator = new CompositeDataGenerator(generators);
    }
    
    @Test
    @DisplayName("Test constructor initializes generator correctly")
    public void testConstructor() {
        assertNotNull(compositeGenerator, "CompositeGenerator should be created successfully");
    }
    
    @Test
    @DisplayName("Test generate method calls all child generators")
    public void testGenerate() {
        // Create a mock generator that tracks if it was called
        final boolean[] wasCalled = {false};
        PatientDataGenerator mockGenerator = new PatientDataGenerator() {
            @Override
            public void generate(int patientId, OutputStrategy outputStrategy) {
                wasCalled[0] = true;
            }
        };
        
        // Create a new composite with our mock
        List<PatientDataGenerator> generatorsWithMock = new ArrayList<>(generators);
        generatorsWithMock.add(mockGenerator);
        CompositeDataGenerator compositeWithMock = new CompositeDataGenerator(generatorsWithMock);
        
        // Create a dummy output strategy
        OutputStrategy dummyOutput = new OutputStrategy() {
            @Override
            public void output(int patientId, long timestamp, String recordType, String value) {
                // Do nothing
            }
        };
        
        // Generate data
        compositeWithMock.generate(5, dummyOutput);
        
        // Verify the mock was called
        assertTrue(wasCalled[0], "Composite should call all child generators");
    }
    
    @Test
    @DisplayName("Test composite generator produces all types of data")
    public void testGeneratesAllDataTypes() {
        // Create recording output strategy
        RecordingOutputStrategy outputStrategy = new RecordingOutputStrategy();
        
        // Generate data
        compositeGenerator.generate(5, outputStrategy);
        
        // Check for all data types
        boolean hasSystolic = false;
        boolean hasDiastolic = false;
        boolean hasECG = false;
        boolean hasSaturation = false;
        
        for (OutputRecord record : outputStrategy.getRecords()) {
            switch (record.recordType) {
                case "SystolicPressure":
                    hasSystolic = true;
                    break;
                case "DiastolicPressure":
                    hasDiastolic = true;
                    break;
                case "ECG":
                    hasECG = true;
                    break;
                case "Saturation":
                    hasSaturation = true;
                    break;
            }
        }
        
        assertTrue(hasSystolic, "Should generate systolic pressure data");
        assertTrue(hasDiastolic, "Should generate diastolic pressure data");
        assertTrue(hasECG, "Should generate ECG data");
        assertTrue(hasSaturation, "Should generate saturation data");
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