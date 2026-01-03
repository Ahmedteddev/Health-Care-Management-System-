package model;

// This model class handles all the saving and loading for Patient data from the CSV
import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {
    
    private final List<Patient> patients = new ArrayList<>();
    private final String csvPath;
    
    // Constructor - this sets up the repository and loads patients from CSV
    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    // This method reads all the patient data from the CSV file
    // CSV files are like spreadsheets - each line is a row, and commas separate the columns
    private void load() {
        // We expect exactly 14 columns in each row of the CSV file
        final int EXPECTED_COLUMNS = 14;
        
        try {
            // CsvUtils.readCsv() reads the file and splits each line by commas
            // It returns a list where each item is an array of strings
            // For example, if the CSV has "P001,John,Smith,1990-01-01,..."
            // Then row[0] = "P001", row[1] = "John", row[2] = "Smith", etc.
            List<String[]> allRows = CsvUtils.readCsv(csvPath);
            
            // Go through each row in the CSV file
            for (String[] currentRow : allRows) {
                // Check if this row has enough columns (sometimes CSV files have bad data)
                if (currentRow.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid patient row with insufficient columns (" + 
                                     currentRow.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", currentRow));
                    continue; // Skip this row and move to the next one
                }
                
                // Now we create a Patient object using the data from this row
                // The CSV file has the data in this order:
                // patient_id, first_name, last_name, date_of_birth, nhs_number,
                // gender, phone_number, email, address, postcode,
                // emergency_contact_name, emergency_contact_phone,
                // registration_date, gp_surgery_id
                // So row[0] is the patient ID, row[1] is first name, etc.
                Patient newPatient = new Patient(
                    currentRow[0],   // patientId (like "P001")
                    currentRow[1],   // firstName
                    currentRow[2],   // lastName
                    currentRow[3],   // dateOfBirth
                    currentRow[4],   // nhsNumber
                    currentRow[5],   // gender
                    currentRow[6],   // phoneNumber
                    currentRow[7],   // email
                    currentRow[8],   // address
                    currentRow[9],   // postcode
                    currentRow[10],  // emergencyContactName
                    currentRow[11],  // emergencyContactPhone
                    currentRow[12],  // registrationDate
                    currentRow[13]   // gpSurgeryId
                );
                
                // Add this patient to our list
                patients.add(newPatient);
            }
            
            System.out.println("Loaded " + patients.size() + " patients from " + csvPath);
            
        } catch (IOException ex) {
            // If we can't read the file (maybe it doesn't exist or is locked)
            System.err.println("Failed to load patients from CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("The repository will start with an empty list.");
        } catch (Exception ex) {
            // If something else goes wrong that we didn't expect
            System.err.println("Unexpected error while loading patients: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Returns all patients in the repository
    public List<Patient> getAll() {
        return new ArrayList<>(patients); // Return a copy to prevent external modification
    }
    
    // Finds a patient by their ID
    // Uses .trim() on ID lookups to prevent issues with hidden spaces in the CSV
    public Patient findById(String id) {
        if (id == null) {
            return null;
        }
        
        // Use trim() on ID lookup to prevent issues with hidden spaces
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
    
    // Adds a new patient to the repository and appends it to the CSV file
    // Ensures the P001 format is strictly maintained for all new entries
    public void add(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot add null patient to repository.");
            return;
        }
        
        // Ensure P001 format is strictly maintained
        String patientId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
        if (!patientId.matches("^P\\d{3}$")) {
            System.err.println("Invalid patient ID format. Must be P001, P002, etc. Got: " + patientId);
            return;
        }
        patient.setPatientId(patientId);
        
        // Check if patient already exists (use trim() on lookup)
        if (findById(patientId) != null) {
            System.err.println("Patient with ID " + patientId + " already exists.");
            return;
        }
        
        // Add to in-memory list
        patients.add(patient);
        
        // Append to CSV file using the P001,Name,DOB... format
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
    
    // Generates a new patient ID based on existing IDs
    // Format: P001, P002, P003, etc.
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
                    // Ignore invalid ID format
                }
            }
        }
        
        return String.format("P%03d", max + 1);
    }
    
    // Adds a new patient and appends it to CSV (alias for add method)
    public void addAndAppend(Patient patient) {
        add(patient);
    }
    
    // Adds a new patient and saves to CSV (alias for add method)
    public void addAndSave(Patient patient) {
        add(patient);
    }
    
    // Updates an existing patient in the repository and saves to CSV
    // Finds the existing ID in the CSV and replaces that line with the new data
    // Uses .trim() on ID lookups to prevent issues with hidden spaces
    public void updatePatient(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot update null patient.");
            return;
        }
        
        // Use trim() on ID lookup to prevent issues with hidden spaces
        String patientId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
        if (patientId.isEmpty()) {
            System.err.println("Cannot update patient with empty ID.");
            return;
        }
        
        // Ensure P001 format is strictly maintained
        if (!patientId.matches("^P\\d{3}$")) {
            System.err.println("Invalid patient ID format. Must be P001, P002, etc. Got: " + patientId);
            return;
        }
        patient.setPatientId(patientId);
        
        // Find and update the patient in the list (use trim() on comparison)
        for (int i = 0; i < patients.size(); i++) {
            String existingId = patients.get(i).getPatientId() != null ? patients.get(i).getPatientId().trim() : "";
            if (patientId.equals(existingId)) {
                patients.set(i, patient);
                // Save all to CSV - this replaces the existing line with new data
                saveAll();
                System.out.println("Successfully updated patient " + patientId);
                return;
            }
        }
        
        System.err.println("Patient with ID " + patientId + " not found for update.");
    }
    
    // Removes a patient from the repository
    // Note: This does not remove from CSV file (would require rewriting the entire file)
    public void remove(Patient patient) {
        if (patient != null) {
            patients.remove(patient);
        }
    }
    
    // Saves all patients to the CSV file
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
    
    // Escapes CSV values that contain commas or quotes
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


