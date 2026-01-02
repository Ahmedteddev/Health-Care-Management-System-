package repository;

import model.Patient;
import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repository class for managing Patient entities.
 * Handles loading from CSV and persisting new patients.
 */
public class PatientRepository {
    
    private final List<Patient> patients = new ArrayList<>();
    private final String csvPath;
    
    /**
     * Constructor that initializes the repository and loads patients from CSV.
     * 
     * @param csvPath The path to the patients.csv file
     */
    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    /**
     * Loads patients from the CSV file.
     * Maps each row to a Patient object using the backward-compatible constructor.
     */
    private void load() {
        final int EXPECTED_COLUMNS = 14;
        
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                // Data integrity check: skip rows with insufficient columns
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid patient row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                // Map CSV row to Patient constructor
                // CSV order: patient_id, first_name, last_name, date_of_birth, nhs_number,
                //            gender, phone_number, email, address, postcode,
                //            emergency_contact_name, emergency_contact_phone,
                //            registration_date, gp_surgery_id
                Patient patient = new Patient(
                    row[0],   // patientId
                    row[1],   // firstName
                    row[2],   // lastName
                    row[3],   // dateOfBirth
                    row[4],   // nhsNumber
                    row[5],   // gender
                    row[6],   // phoneNumber
                    row[7],   // email
                    row[8],   // address
                    row[9],   // postcode
                    row[10],  // emergencyContactName
                    row[11],  // emergencyContactPhone
                    row[12],  // registrationDate
                    row[13]   // gpSurgeryId
                );
                
                patients.add(patient);
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
    
    /**
     * Returns all patients in the repository.
     * 
     * @return List of all Patient objects
     */
    public List<Patient> getAll() {
        return new ArrayList<>(patients); // Return a copy to prevent external modification
    }
    
    /**
     * Finds a patient by their ID.
     * 
     * @param id The patient ID to search for
     * @return The Patient object if found, null otherwise
     */
    public Patient findById(String id) {
        if (id == null) {
            return null;
        }
        
        for (Patient patient : patients) {
            if (id.equals(patient.getPatientId()) || id.equals(patient.getId())) {
                return patient;
            }
        }
        return null;
    }
    
    /**
     * Adds a new patient to the repository and appends it to the CSV file.
     * 
     * @param patient The Patient object to add
     */
    public void add(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot add null patient to repository.");
            return;
        }
        
        // Check if patient already exists
        if (findById(patient.getPatientId()) != null) {
            System.err.println("Patient with ID " + patient.getPatientId() + " already exists.");
            return;
        }
        
        // Add to in-memory list
        patients.add(patient);
        
        // Append to CSV file
        try {
            String[] rowData = {
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getDateOfBirth(),
                patient.getNhsNumber(),
                patient.getGender(),
                patient.getPhoneNumber(),
                patient.getEmail(),
                patient.getAddress(),
                patient.getPostcode(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(),
                patient.getRegistrationDate(),
                patient.getGpSurgeryId()
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
    
    /**
     * Generates a new patient ID based on existing IDs.
     * Format: P001, P002, P003, etc.
     * 
     * @return A new unique patient ID
     */
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
    
    /**
     * Adds a new patient and appends it to CSV.
     * Alias for add() method for backward compatibility.
     * 
     * @param patient The Patient object to add
     */
    public void addAndAppend(Patient patient) {
        add(patient);
    }
    
    /**
     * Updates an existing patient in the repository and saves to CSV.
     * 
     * @param patient The updated Patient object
     */
    public void updatePatient(Patient patient) {
        if (patient == null) {
            System.err.println("Cannot update null patient.");
            return;
        }
        
        // Find and update the patient in the list
        for (int i = 0; i < patients.size(); i++) {
            if (patients.get(i).getPatientId().equals(patient.getPatientId())) {
                patients.set(i, patient);
                // Save all to CSV
                saveAll();
                System.out.println("Successfully updated patient " + patient.getPatientId());
                return;
            }
        }
        
        System.err.println("Patient with ID " + patient.getPatientId() + " not found for update.");
    }
    
    /**
     * Removes a patient from the repository.
     * Note: This does not remove from CSV file (would require rewriting the entire file).
     * 
     * @param patient The Patient object to remove
     */
    public void remove(Patient patient) {
        if (patient != null) {
            patients.remove(patient);
        }
    }
    
    /**
     * Saves all patients to the CSV file.
     */
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

