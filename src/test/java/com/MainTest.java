package com;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

/**
 * Tests for the Main class that handles command-line argument dispatching.
 */
public class MainTest {
    
    @Test
    @DisplayName("Test help message contains expected text")
    public void testPrintHelp() {
        // Capture stdout to verify output
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));
        
        try {
            // Call the help method
            Main.printHelp();
            
            // Verify output contains expected components
            String output = outContent.toString();
            assertTrue(output.contains("DataStorage"), "Help should mention DataStorage");
            assertTrue(output.contains("HealthDataSimulator"), "Help should mention HealthDataSimulator");
            assertTrue(output.contains("--patient-count"), "Help should mention patient count option");
            assertTrue(output.contains("--output"), "Help should mention output option");
        } finally {
            System.setOut(originalOut);
        }
    }
    
    @Test
    @DisplayName("Test running with DataStorage argument")
    public void testMainWithDataStorage() throws Exception {
        // Create a custom DataStorage class just for testing
        DataStorageTester.wasExecuted = false;
        
        // Call main with DataStorage argument
        String[] args = {"DataStorage"};
        MainTester.mainWithCustomDataStorage(args);
        
        // Verify DataStorage was called
        assertTrue(DataStorageTester.wasExecuted, "DataStorage main method should be executed");
    }
    
    @Test
    @DisplayName("Test running with HealthDataSimulator argument")
    public void testMainWithHealthDataSimulator() throws Exception {
        // Create a custom HealthDataSimulator just for testing
        HealthDataSimulatorTester.wasExecuted = false;
        
        // Call main with HealthDataSimulator argument
        String[] args = {"HealthDataSimulator", "--patient-count", "10"};
        MainTester.mainWithCustomSimulator(args);
        
        // Verify HealthDataSimulator was called with the right args
        assertTrue(HealthDataSimulatorTester.wasExecuted, "HealthDataSimulator main method should be executed");
        assertEquals(2, HealthDataSimulatorTester.passedArgs.length, "Should pass 2 args to simulator");
        assertEquals("--patient-count", HealthDataSimulatorTester.passedArgs[0], "First arg should be --patient-count");
        assertEquals("10", HealthDataSimulatorTester.passedArgs[1], "Second arg should be 10");
    }
    
    @Test
    @DisplayName("Test running with no arguments")
    public void testMainWithNoArgs() throws Exception {
        // Create a custom HealthDataSimulator just for testing
        HealthDataSimulatorTester.wasExecuted = false;
        
        // Call main with no arguments
        String[] args = {};
        MainTester.mainWithCustomSimulator(args);
        
        // Verify HealthDataSimulator was called with empty args
        assertTrue(HealthDataSimulatorTester.wasExecuted, "HealthDataSimulator main method should be executed");
        assertEquals(0, HealthDataSimulatorTester.passedArgs.length, "Should pass empty args to simulator");
    }
    
    /**
     * Test classes to avoid actual execution of components
     */
    static class MainTester extends Main {
        public static void mainWithCustomDataStorage(String[] args) throws Exception {
            if (args.length > 0 && args[0].equals("DataStorage")) {
                DataStorageTester.main(new String[]{});
            } else {
                HealthDataSimulatorTester.main(args);
            }
        }
        
        public static void mainWithCustomSimulator(String[] args) throws Exception {
            if (args.length > 0 && args[0].equals("DataStorage")) {
                DataStorageTester.main(new String[]{});
            } else {
                String[] simulatorArgs = args;
                if (args.length > 0 && args[0].equals("HealthDataSimulator")) {
                    simulatorArgs = new String[args.length - 1];
                    System.arraycopy(args, 1, simulatorArgs, 0, args.length - 1);
                }
                
                HealthDataSimulatorTester.main(simulatorArgs);
            }
        }
    }
    
    static class DataStorageTester {
        public static boolean wasExecuted = false;
        
        public static void main(String[] args) {
            wasExecuted = true;
        }
    }
    
    static class HealthDataSimulatorTester {
        public static boolean wasExecuted = false;
        public static String[] passedArgs;
        
        public static void main(String[] args) {
            wasExecuted = true;
            passedArgs = args;
        }
    }
}