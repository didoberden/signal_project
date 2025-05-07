package com.data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;

import com.cardio_generator.HealthDataSimulator;

public class SingletonTest {

    @Test
    @DisplayName("Test DataStorage singleton instance")
    public void testDataStorageSingleton() {
        // Get the instance twice
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        
        // Both references should point to the same object
        assertSame(instance1, instance2, "DataStorage singleton should return same instance");
    }
    
    @Test
    @DisplayName("Test HealthDataSimulator singleton instance")
    public void testHealthDataSimulatorSingleton() {
        // Get the instance twice
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        
        // Both references should point to the same object
        assertSame(instance1, instance2, "HealthDataSimulator singleton should return same instance");
    }
    
    @Test
    @DisplayName("Test DataStorage singleton with concurrent access")
    public void testDataStorageSingletonConcurrent() throws Exception {
        final int threadCount = 10;
        final Set<DataStorage> instances = Collections.synchronizedSet(new HashSet<>());
        final CountDownLatch latch = new CountDownLatch(threadCount);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        // Have multiple threads get the instance simultaneously
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    // Force threads to attempt getting the instance at roughly the same time
                    latch.countDown();
                    latch.await();
                    
                    // Get the instance and add to set
                    DataStorage instance = DataStorage.getInstance();
                    instances.add(instance);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
        
        executor.shutdown();
        assertTrue(executor.awaitTermination(5, TimeUnit.SECONDS), 
            "All threads should complete within timeout");
        
        // All threads should get the same instance
        assertEquals(1, instances.size(), 
            "All threads should get the same DataStorage instance");
    }
    
    @Test
    @DisplayName("Test reset functionality of DataStorage")
    public void testDataStorageReset() {
        DataStorage storage = DataStorage.getInstance();
        
        // Add some test data
        storage.addPatientData(1, 100.0, "TestData", System.currentTimeMillis());
        
        // Verify data is present
        assertFalse(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty(),
            "Storage should contain records before reset");
        
        // Reset the storage
        storage.reset();
        
        // Verify data is gone
        assertTrue(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty(),
            "Storage should be empty after reset");
    }
    
    @Test
    @DisplayName("Test clearAllData functionality of DataStorage")
    public void testDataStorageClearAllData() {
        DataStorage storage = DataStorage.getInstance();
        
        // Add some test data
        storage.addPatientData(1, 100.0, "TestData", System.currentTimeMillis());
        
        // Verify data is present
        assertFalse(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty(),
            "Storage should contain records before clearing");
        
        // Clear the data
        storage.clearAllData();
        
        // Verify data is gone
        assertTrue(storage.getRecords(1, 0, Long.MAX_VALUE).isEmpty(),
            "Storage should be empty after clearing");
    }
}