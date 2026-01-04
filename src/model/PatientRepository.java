package model;

// Handles loading and saving patient data from CSV
import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {
    
    private final List<Patient> patients = new ArrayList<>();
    private final String csvPath;
    
    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    // Load all patients from the CSV file
    private void load() {
        final int EXPECTED_COLUMNS = 14;
        
        try {
            List<String[]> allRows = CsvUtils.readCsv(csvPath);
            
            for (String[] currentRow : allRows) {
                if (currentRow.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid patient row with insufficient columns (" + 
                                     currentRow.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", currentRow));
                    continue;
                }
                
                Patient newPatient = new Patient(
                    currentRow[0],
                    currentRow[1],
                    currentRow[2],
                    currentRow[3],
                    currentRow[4],
                    currentRow[5],
                    currentRow[6],
                    currentRow[7],
                    currentRow[8],
                    currentRow[9],
                    currentRow[10],
                    currentRow[11],
                    currentRow[12],
                    currentRow[13]
                );
                
                patients.add(newPatient);
            }
            
            System.out.println("Loaded " + patients.size() + " patients from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load patients from CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("The repository will start with an empty list.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading patients: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }
    
    // Find a patient by ID (using trim to handle spaces in CSV)
    public Patient findById(String id) {
        if (id == null) {
            return null;
        }
        
        String trimmedId = id.trim();
        
        for (Patient patient : patients) {
            String patientId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
            String patientIdAlt = patient.getId() != null ? patient.getId().trim() : "";
            if (trimmedId.equals(patientId) || trimmedId.equals(patientIdAlt)) {
                return patient;
            }
        }
        return null;
    }
    
    // Add a new patient and save to CSV (keeps P001 format)
    public void add(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot add null patient to repository.");
            return;
        }
        
        String patientId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
        if (!patientId.matches("^P\\d{3}$")) {
            System.err.println("Invalid patient ID format. Must be P001, P002, etc. Got: " + patientId);
            return;
        }
        patient.setPatientId(patientId);
        
        if (findById(patientId) != null) {
            System.err.println("Patient with ID " + patientId + " already exists.");
            return;
        }
        
        patients.add(patient);
        
        try {
            String[] rowData = {
                patient.getPatientId(),
                patient.getFirstName() != null ? patient.getFirstName() : "",
                patient.getLastName() != null ? patient.getLastName() : "",
                patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "",
                patient.getNhsNumber() != null ? patient.getNhsNumber() : "",
                patient.getGender() != null ? patient.getGender() : "",
                patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "",
                patient.getEmail() != null ? patient.getEmail() : "",
                patient.getAddress() != null ? patient.getAddress() : "",
                patient.getPostcode() != null ? patient.getPostcode() : "",
                patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "",
                patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "",
                patient.getRegistrationDate() != null ? patient.getRegistrationDate() : "",
                patient.getGpSurgeryId() != null ? patient.getGpSurgeryId() : ""
            };
            
            CsvUtils.appendLine(csvPath, rowData);
            System.out.println("Successfully added patient " + patient.getPatientId() + " to repository and CSV.");
            
        } catch (IOException ex) {
            System.err.println("Failed to append patient to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Patient added to repository but not persisted to file.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while adding patient: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Generate a new patient ID (P001, P002, etc.)
    public String generateNewId() {
        int max = 0;
        
        for (Patient patient : patients) {
            String id = patient.getPatientId();
            if (id != null && id.startsWith("P")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        return String.format("P%03d", max + 1);
    }
    
    public void addAndAppend(Patient patient) {
        add(patient);
    }
    
    public void addAndSave(Patient patient) {
        add(patient);
    }
    
    // Update a patient and save to CSV
    public void updatePatient(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot update null patient.");
            return;
        }
        
        String patientId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
        if (patientId.isEmpty()) {
            System.err.println("Cannot update patient with empty ID.");
            return;
        }
        
        if (!patientId.matches("^P\\d{3}$")) {
            System.err.println("Invalid patient ID format. Must be P001, P002, etc. Got: " + patientId);
            return;
        }
        patient.setPatientId(patientId);
        
        for (int i = 0; i < patients.size(); i++) {
            String existingId = patients.get(i).getPatientId() != null ? patients.get(i).getPatientId().trim() : "";
            if (patientId.equals(existingId)) {
                patients.set(i, patient);
                saveAll();
                System.out.println("Successfully updated patient " + patientId);
                return;
            }
        }
        
        System.err.println("Patient with ID " + patientId + " not found for update.");
    }
    
    // Remove a patient from the list (doesn't update CSV)
    public void remove(Patient patient) {
        if (patient != null) {
            patients.remove(patient);
        }
    }
    
    // Save all patients back to CSV
    public void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            // Write header
            bw.write("patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode,emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id");
            bw.newLine();
            
            // Write all patients
            for (Patient p : patients) {
                bw.write(escapeCsv(p.getPatientId()) + ",");
                bw.write(escapeCsv(p.getFirstName()) + ",");
                bw.write(escapeCsv(p.getLastName()) + ",");
                bw.write(escapeCsv(p.getDateOfBirth()) + ",");
                bw.write(escapeCsv(p.getNhsNumber()) + ",");
                bw.write(escapeCsv(p.getGender()) + ",");
                bw.write(escapeCsv(p.getPhoneNumber()) + ",");
                bw.write(escapeCsv(p.getEmail()) + ",");
                bw.write(escapeCsv(p.getAddress()) + ",");
                bw.write(escapeCsv(p.getPostcode()) + ",");
                bw.write(escapeCsv(p.getEmergencyContactName()) + ",");
                bw.write(escapeCsv(p.getEmergencyContactPhone()) + ",");
                bw.write(escapeCsv(p.getRegistrationDate()) + ",");
                bw.write(escapeCsv(p.getGpSurgeryId()));
                bw.newLine();
            }
            
        } catch (java.io.IOException ex) {
            System.err.println("Failed to save patients to CSV file: " + csvPath);
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


