package model;

import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

// Repository for medical records - stores allergies, blood type, and history
public class MedicalRecordRepository {
    
    private final List<MedicalRecord> records = new ArrayList<>();
    private final String csvPath;
    
    private static final int EXPECTED_COLUMNS = 4;
    
    public MedicalRecordRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    // Load medical records from CSV
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid medical record row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + ")");
                    continue;
                }
                
                MedicalRecord record = new MedicalRecord(
                    row[0],
                    row[1],
                    row[2],
                    row[3]
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
    
    // Create a new empty medical record for a patient
    public void initializeRecord(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot initialize medical record: patient ID is null or empty.");
            return;
        }
        
        if (findByPatientId(patientId) != null) {
            System.out.println("Medical record for patient " + patientId + " already exists.");
            return;
        }
        
        MedicalRecord newRecord = new MedicalRecord(patientId, "", "", "");
        records.add(newRecord);
        
        try {
            String[] rowData = {
                newRecord.getPatientId(),
                newRecord.getAllergies(),
                newRecord.getBloodType(),
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
    
    // Delete a medical record for a patient
    public void deleteRecord(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete medical record: patient ID is null or empty.");
            return;
        }
        
        records.removeIf(record -> patientId.equals(record.getPatientId()));
        saveAll();
        
        System.out.println("Deleted medical record for patient " + patientId);
    }
    
    // Find a medical record by patient ID
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
    
    public List<MedicalRecord> getAll() {
        return new ArrayList<>(records);
    }
    
    // Save all records back to CSV
    private void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
            bw.write("patient_id,allergies,blood_type,history");
            bw.newLine();
            
            for (MedicalRecord record : records) {
                bw.write(record.getPatientId() + ",");
                bw.write(escapeCsv(record.getAllergies()) + ",");
                bw.write(escapeCsv(record.getBloodType()) + ",");
                bw.newLine();
            }
            
        } catch (IOException ex) {
            System.err.println("Failed to save medical records to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
        }
    }
    
    // Escape commas and quotes in CSV values
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

