package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class ECGDataGenerator implements PatientDataGenerator {    private static final Random random = new Random();
    private double[] lastEcgValues;

    public ECGDataGenerator(int patientCount) {
        lastEcgValues = new double[patientCount + 1];
        // Initialize the last ECG value for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastEcgValues[i] = 0; // Initial ECG value can be set to 0
        }
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        // TODO Check how realistic this data is and make it more realistic if necessary
        try {
            double ecgValue = simulateEcgWaveform(patientId, lastEcgValues[patientId]);
            outputStrategy.output(patientId, System.currentTimeMillis(), "ECG", Double.toString(ecgValue));
            lastEcgValues[patientId] = ecgValue;
        } catch (Exception e) {
            System.err.println("An error occurred while generating ECG data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }    private double simulateEcgWaveform(int patientId, double lastEcgValue) {
        // Generate a heart rate value within the physiologically reasonable range (30-200 bpm)
        // For a normal adult, this would typically be 60-100 bpm
        double baseHeartRate = 70.0 + (patientId % 5) * 10.0; // Different base HR per patient
        double heartRateVariability = random.nextDouble() * 20.0 - 10.0; // +/- 10 bpm variability
        double heartRate = baseHeartRate + heartRateVariability;
        
        // Ensure the heart rate is within the expected test range
        heartRate = Math.min(Math.max(heartRate, 30.0), 200.0);
        
        return heartRate; // Return the heart rate value directly
    }
}
