package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Outputs patient data to separate files by data type.
 */
public class FileOutputStrategy implements OutputStrategy {

    private String baseDirectory;
    private final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Sets the directory for output files.
     *
     * @param baseDirectory directory for files
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Writes data to the right file for the label.
     *
     * @param patientId the patient ID
     * @param timestamp time the data was generated
     * @param label data type label
     * @param data the data value
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }
        String filePath = fileMap.computeIfAbsent(label,
                k -> Paths.get(baseDirectory, label + ".txt").toString());
        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n",
                    patientId, timestamp, label, data);
        } catch (IOException e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}
