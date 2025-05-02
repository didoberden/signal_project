package com;

import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;

/**
 * Main entry point for the Signal Project application.
 * This class allows running different components based on
 * command-line parameters.
 */
public class Main {
    /**
     * Main method that dispatches to the appropriate class based on
     * the first command-line argument.
     * 
     * @param args command-line arguments
     * @throws Exception if an error occurs during execution
     */
    public static void main(String[] args) throws Exception {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            System.out.println("Starting DataStorage component...");
            // Get all arguments except the first one
            String[] remainingArgs = new String[args.length - 1];
            System.arraycopy(args, 1, remainingArgs, 0, args.length - 1);
            
            // Run DataStorage with the remaining arguments
            DataStorage.main(remainingArgs);
        } else {
            System.out.println("Starting HealthDataSimulator component...");
            
            // If the first arg is explicitly "HealthDataSimulator", skip it
            String[] simulatorArgs = args;
            if (args.length > 0 && args[0].equals("HealthDataSimulator")) {
                simulatorArgs = new String[args.length - 1];
                System.arraycopy(args, 1, simulatorArgs, 0, args.length - 1);
            }
            
            // Run the simulator with appropriate arguments
            HealthDataSimulator.main(simulatorArgs);
        }
    }
    
    /**
     * Prints usage information for the application.
     */
    public static void printHelp() {
        System.out.println("Usage: java -jar cardio_generator-1.0-SNAPSHOT.jar [component] [options]");
        System.out.println("Components:");
        System.out.println("  DataStorage          - Run the DataStorage component");
        System.out.println("  HealthDataSimulator  - Run the HealthDataSimulator (default)");
        System.out.println("\nFor HealthDataSimulator options:");
        System.out.println("  --patient-count <n>  - Number of patients to simulate");
        System.out.println("  --output <type>      - Output strategy (console, file:path, websocket:port, tcp:port)");
        System.out.println("\nExamples:");
        System.out.println("  java -jar cardio_generator-1.0-SNAPSHOT.jar");
        System.out.println("  java -jar cardio_generator-1.0-SNAPSHOT.jar DataStorage");
        System.out.println("  java -jar cardio_generator-1.0-SNAPSHOT.jar HealthDataSimulator --patient-count 100");
    }
}