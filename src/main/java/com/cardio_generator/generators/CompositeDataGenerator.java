package com.cardio_generator.generators;

import java.util.List;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * Composite implementation of PatientDataGenerator that combines multiple generators.
 * This follows the Composite design pattern, allowing clients to treat individual
 * generators and compositions of generators uniformly.
 */
public class CompositeDataGenerator implements PatientDataGenerator {
    
    private List<PatientDataGenerator> generators;
    
    /**
     * Creates a composite generator with the specified list of child generators.
     * 
     * @param generators List of child generators to use
     */
    public CompositeDataGenerator(List<PatientDataGenerator> generators) {
        this.generators = generators;
    }
    
    /**
     * Generates data by delegating to all child generators.
     * 
     * @param patientId The ID of the patient to generate data for
     * @param outputStrategy The strategy to use for outputting generated data
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        for (PatientDataGenerator generator : generators) {
            generator.generate(patientId, outputStrategy);
        }
    }
    
    /**
     * Adds a generator to this composite.
     * 
     * @param generator The generator to add
     */
    public void addGenerator(PatientDataGenerator generator) {
        generators.add(generator);
    }
    
    /**
     * Removes a generator from this composite.
     * 
     * @param generator The generator to remove
     * @return true if the generator was found and removed, false otherwise
     */
    public boolean removeGenerator(PatientDataGenerator generator) {
        return generators.remove(generator);
    }
    
    /**
     * Returns the number of child generators in this composite.
     * 
     * @return The number of child generators
     */
    public int getGeneratorCount() {
        return generators.size();
    }
}