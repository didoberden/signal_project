package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * Interface for classes that generate patient data.
 */
public interface PatientDataGenerator {

    /**
     * Generates data for a patient and sends it to the output.
     *
     * @param patientId the patient ID
     * @param outputStrategy where to send the data
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}
