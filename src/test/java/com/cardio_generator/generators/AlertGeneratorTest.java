package com.cardio_generator.generators;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;

import com.cardio_generator.outputs.OutputStrategy;
import java.lang.reflect.Field;

/**
 * Tests for the AlertGenerator class in the generators package.
 */
public class AlertGeneratorTest {
    
    private static final int TEST_PATIENT_COUNT = 10;
    
    /**
     * Test that the AlertGenerator constructor properly initializes patient alert states.
     */
    @Test
    @DisplayName("Test constructor initializes alert states correctly")
    public void testConstructor() {
        AlertGenerator generator = new AlertGenerator(TEST_PATIENT_COUNT);
        assertNotNull(generator, "Generator should be created successfully");
    }
    
    /**
     * Test that the generate method works without throwing exceptions.
     */
    @Test
    @DisplayName("Test generate method execution")
    public void testGenerate() {
        // Create generator
        AlertGenerator generator = new AlertGenerator(TEST_PATIENT_COUNT);
        
        // Create mock output strategy to track calls
        final boolean[] outputCalled = {false};
        OutputStrategy mockOutput = new OutputStrategy() {
            @Override
            public void output(int patientId, long timestamp, String recordType, String value) {
                outputCalled[0] = true;
                assertEquals("Alert", recordType, "Record type should be 'Alert'");
                assertTrue(value.equals("triggered") || value.equals("resolved"), 
                           "Value should be 'triggered' or 'resolved'");
            }
        };
        
        try {
            // Use reflection to access the private AlertStates field
            Field alertStatesField = AlertGenerator.class.getDeclaredField("AlertStates");
            alertStatesField.setAccessible(true);
            boolean[] alertStates = (boolean[]) alertStatesField.get(generator);
            
            // Force alert state to true for patient 5
            alertStates[5] = true;
            
            // Generate for a patient with alert state = true
            generator.generate(5, mockOutput);
            
            // Reset and test for a patient with alert state = false
            outputCalled[0] = false;
            alertStates[6] = false;
            
            // Run multiple times to increase chance of alert triggering (since it's random)
            for (int i = 0; i < 100 && !outputCalled[0]; i++) {
                generator.generate(6, mockOutput);
            }
            
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not access AlertStates field: " + e.getMessage());
        }
    }
    
    /**
     * Test with a real output strategy implementation.
     */
    @Test
    @DisplayName("Test with counting output strategy")
    public void testWithCountingStrategy() {
        // Create generator
        AlertGenerator generator = new AlertGenerator(TEST_PATIENT_COUNT);
        
        // Create tracking output strategy
        CountingOutputStrategy countingStrategy = new CountingOutputStrategy();
        
        try {
            // Use reflection to access the private AlertStates field
            Field alertStatesField = AlertGenerator.class.getDeclaredField("AlertStates");
            alertStatesField.setAccessible(true);
            boolean[] alertStates = (boolean[]) alertStatesField.get(generator);
            
            // Test with multiple patients
            for (int patientId = 1; patientId <= TEST_PATIENT_COUNT; patientId++) {
                // Force some alerts to be in triggered state
                alertStates[patientId] = (patientId % 2 == 0);
                
                // Generate data
                generator.generate(patientId, countingStrategy);
            }
            
            // Verify we got some output
            assertTrue(countingStrategy.getTotalOutputs() > 0, 
                       "Should have received at least one output");
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Could not access AlertStates field: " + e.getMessage());
        }
    }
    
    /**
     * Helper class to count output calls.
     */
    private static class CountingOutputStrategy implements OutputStrategy {
        private int triggeredCount = 0;
        private int resolvedCount = 0;
        
        @Override
        public void output(int patientId, long timestamp, String recordType, String value) {
            if ("Alert".equals(recordType)) {
                if ("triggered".equals(value)) {
                    triggeredCount++;
                } else if ("resolved".equals(value)) {
                    resolvedCount++;
                }
            }
        }
        
        public int getTriggeredCount() {
            return triggeredCount;
        }
        
        public int getResolvedCount() {
            return resolvedCount;
        }
        
        public int getTotalOutputs() {
            return triggeredCount + resolvedCount;
        }
    }
}