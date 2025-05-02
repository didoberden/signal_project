package com.data_management;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Tests for FileDataReader
 */
public class FileDataReaderTest {

    @TempDir
    Path tempDir; // Using JUnit's temp directory feature
    
    @Test
    public void testReadingFile() throws IOException {
        // Create test file
        File file = new File(tempDir.toFile(), "test.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("101,120.0,SystolicBP,1000\n");
        writer.write("101,80.0,DiastolicBP,1000\n");
        writer.close();
        
        // Create data storage to collect data
        DataStorage storage = new DataStorage();
        
        // Create reader
        FileDataReader reader = new FileDataReader(tempDir.toString());
        
        // Read data
        reader.readData(storage);
        
        // Test reading worked - doesn't check what was actually added
        // No way to verify data was correctly parsed
    }
    
    @Test
    public void testEmptyDirectory() throws IOException {
        // Create reader with empty dir
        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage();
        
        // This should not throw exception
        reader.readData(storage);
    }
    
    @Test
    public void testBadData() throws IOException {
        // Create test file with bad data
        File file = new File(tempDir.toFile(), "bad.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write("not a valid line\n");
        writer.close();
        
        // Create reader and storage
        FileDataReader reader = new FileDataReader(tempDir.toString());
        DataStorage storage = new DataStorage();
        
        // This should not throw exception
        reader.readData(storage);
        
        // No assertions to verify behavior
    }
    
    @Test
    public void testDirectoryNotFound() {
        // Create reader with nonexistent directory
        FileDataReader reader = new FileDataReader("C:/this/does/not/exist");
        DataStorage storage = new DataStorage();
        
        // Should throw exception
        try {
            reader.readData(storage);
            fail("Should have thrown exception");
        } catch (IOException e) {
            // Expected exception
        }
    }
}