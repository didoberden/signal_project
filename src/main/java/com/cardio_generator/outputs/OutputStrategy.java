package com.cardio_generator.outputs;

/**
 * Interface for outputting patient data.
 */
public interface OutputStrategy {

    /**
     * Sends data for a patient to the output.
     *
     * @param patientId the patient ID
     * @param timestamp time the data was generated
     * @param label data type label
     * @param data the data value
     */
    void output(int patientId, long timestamp, String label, String data);
}
