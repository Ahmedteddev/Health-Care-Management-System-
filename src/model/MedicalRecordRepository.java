package model;

import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository for managing medical records.
 * Each patient has a medical record with allergies, blood type, and history.
 */
public class MedicalRecordRepository {
    
    private final List<MedicalRecord> records = new ArrayList<>();
    private final String csvPath;
    
    // Expected CSV columns: patient_id, allergies, blood_type, history
    private static final int EXPECTED_COLUMNS = 4;
    
    public MedicalRecordRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    /**
     * Loads medical records from CSV file.
     */
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                // Skip rows with insufficient columns
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid medical record row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + ")");
                    continue;
                }
                
                MedicalRecord record = new MedicalRecord(
                    row[0],  // patientId
                    row[1],  // allergies
                    row[2],  // bloodType
                    row[3]   // history
                );
                
                records.add(record);
            }
            
            System.out.println("Loaded " + records.size() + " medical records from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load medical records from CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("The repository will start with an empty list.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading medical records: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Initializes a new medical record for a patient with default empty values.
     * 
     * @param patientId The patient ID to create a record for
     */
    public void initializeRecord(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot initialize medical record: patient ID is null or empty.");
            return;
        }
        
        // Check if record already exists
        if (findByPatientId(patientId) != null) {
            System.out.println("Medical record for patient " + patientId + " already exists.");
            return;
        }
        
        // Create new record with default empty values
        MedicalRecord newRecord = new MedicalRecord(patientId, "", "", "");
        records.add(newRecord);
        
        // Append to CSV file
        try {
            String[] rowData = {
                newRecord.getPatientId(),
                newRecord.getAllergies(),
                newRecord.getBloodType(),
                newRecord.getHistory()
            };
            
            CsvUtils.appendLine(csvPath, rowData);
            System.out.println("Successfully initialized medical record for patient " + patientId);
            
        } catch (IOException ex) {
            System.err.println("Failed to append medical record to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Record added to repository but not persisted to file.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while initializing medical record: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    /**
     * Deletes a medical record for a specific patient.
     * 
     * @param patientId The patient ID whose record should be deleted
     */
    public void deleteRecord(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete medical record: patient ID is null or empty.");
            return;
        }
        
        // Remove from in-memory list
        records.removeIf(record -> patientId.equals(record.getPatientId()));
        
        // Save all remaining records to CSV
        saveAll();
        
        System.out.println("Deleted medical record for patient " + patientId);
    }
    
    /**
     * Finds a medical record by patient ID.
     * 
     * @param patientId The patient ID to search for
     * @return The MedicalRecord object if found, null otherwise
     */
    public MedicalRecord findByPatientId(String patientId) {
        if (patientId == null) {
            return null;
        }
        
        for (MedicalRecord record : records) {
            if (patientId.equals(record.getPatientId())) {
                return record;
            }
        }
        return null;
    }
    
    /**
     * Returns all medical records.
     * 
     * @return List of all MedicalRecord objects
     */
    public List<MedicalRecord> getAll() {
        return new ArrayList<>(records);
    }
    
    /**
     * Saves all medical records to the CSV file.
     */
    private void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
            // Write header
            bw.write("patient_id,allergies,blood_type,history");
            bw.newLine();
            
            // Write all records
            for (MedicalRecord record : records) {
                bw.write(record.getPatientId() + ",");
                bw.write(escapeCsv(record.getAllergies()) + ",");
                bw.write(escapeCsv(record.getBloodType()) + ",");
                bw.write(escapeCsv(record.getHistory()));
                bw.newLine();
            }
            
        } catch (IOException ex) {
            System.err.println("Failed to save medical records to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
        }
    }
    
    /**
     * Escapes CSV values that contain commas or quotes.
     */
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}

