package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PatientRepository {

    private static PatientRepository instance;
    private final List<Patient> patients = new ArrayList<>();
    private final String csvPath;
    private MedicalRecordRepository medicalRecordRepository;
    private AppointmentRepository appointmentRepository;
    private PrescriptionRepository prescriptionRepository;

    /**
     * Public constructor for backward compatibility.
     * Creates a new instance. If instance is null, sets it as the singleton instance.
     * Note: For new code, use getInstance() instead for proper singleton pattern.
     */
    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
        // If instance is null, set it (allows singleton pattern to work)
        if (instance == null) {
            instance = this;
        }
    }
    
    /**
     * Public static method to get the singleton instance.
     * Implements lazy initialization.
     */
    public static synchronized PatientRepository getInstance(String csvPath) {
        if (instance == null) {
            instance = new PatientRepository(csvPath);
        }
        return instance;
    }
    
    /**
     * Sets the MedicalRecordRepository for cascading operations.
     */
    public void setMedicalRecordRepository(MedicalRecordRepository medicalRecordRepository) {
        this.medicalRecordRepository = medicalRecordRepository;
    }
    
    /**
     * Sets the AppointmentRepository for cascading deletion.
     */
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }
    
    /**
     * Sets the PrescriptionRepository for cascading deletion.
     */
    public void setPrescriptionRepository(PrescriptionRepository prescriptionRepository) {
        this.prescriptionRepository = prescriptionRepository;
    }

    public List<String> getAllIds() {
    List<String> ids = new ArrayList<>();
    for (Patient p : patients) ids.add(p.getId());
    return ids;
}

    // ============================================================
    // LOAD PATIENTS FROM CSV (all 14 fields)
    // ============================================================
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                Patient p = new Patient(
                        row[0],   // patient_id
                        row[1],   // first_name
                        row[2],   // last_name
                        row[3],   // date_of_birth
                        row[4],   // nhs_number
                        row[5],   // gender
                        row[6],   // phone_number
                        row[7],   // email
                        row[8],   // address
                        row[9],   // postcode
                        row[10],  // emergency_contact_name
                        row[11],  // emergency_contact_phone
                        row[12],  // registration_date
                        row[13]   // gp_surgery_id
                );

                patients.add(p);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load patients: " + ex.getMessage());
        }
    }

    // ============================================================
    // AUTO-ID GENERATOR  (P001 → P002 → P003 → …)
    // ============================================================
    public String generateNewId() {

        int max = 0;

        for (Patient p : patients) {
            try {
                int num = Integer.parseInt(p.getId().substring(1));
                if (num > max) max = num;
            } catch (Exception ignore) {}
        }

        return String.format("P%03d", max + 1);
    }

    // ============================================================
    // ADD PATIENT + APPEND TO CSV
    // ============================================================
    public void addAndAppend(Patient p) {
        patients.add(p);

        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    p.getId(),
                    p.getFirstName(),
                    p.getLastName(),
                    p.getDateOfBirth(),
                    p.getNhsNumber(),
                    p.getGender(),
                    p.getPhoneNumber(),
                    p.getEmail(),
                    p.getAddress(),
                    p.getPostcode(),
                    p.getEmergencyContactName(),
                    p.getEmergencyContactPhone(),
                    p.getRegistrationDate(),
                    p.getGpSurgeryId()
            });
            
            // After successfully saving, initialize medical record
            if (medicalRecordRepository != null) {
                medicalRecordRepository.initializeRecord(p.getId());
            } else {
                System.err.println("Warning: MedicalRecordRepository not set. Medical record not initialized for patient " + p.getId());
            }

        } catch (IOException ex) {
            System.err.println("Failed to append patient: " + ex.getMessage());
        }
    }
    
    /**
     * Alias for addAndAppend to match user's method name requirement.
     */
    public void addPatient(Patient p) {
        addAndAppend(p);
    }

    public List<Patient> getAll() {
        return patients;
    }

    public void remove(Patient p) {
        patients.remove(p);
    }

    public Patient findById(String id) {
        for (Patient p : patients) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
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
            if (patients.get(i).getId().equals(patient.getId())) {
                patients.set(i, patient);
                // Save all to CSV
                saveAll();
                System.out.println("Successfully updated patient " + patient.getId());
                return;
            }
        }
        
        System.err.println("Patient with ID " + patient.getId() + " not found for update.");
    }
    
    
    /**
     * Deletes a patient and all related data (cascading deletion).
     * Removes appointments, prescriptions, and medical records before deleting the patient.
     * 
     * @param patientId The patient ID to delete
     * @param skipConfirmation If true, skips the confirmation dialog (for programmatic calls)
     */
    public boolean deletePatient(String patientId, boolean skipConfirmation) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete patient: patient ID is null or empty.");
            return false;
        }
        
        Patient patient = findById(patientId);
        if (patient == null) {
            System.err.println("Patient with ID " + patientId + " not found.");
            return false;
        }
        
        // Show confirmation dialog unless skipped
        if (!skipConfirmation) {
            int result = JOptionPane.showConfirmDialog(
                null,
                "Deleting this patient will permanently remove all their medical history, appointments, and prescriptions. Proceed?",
                "Confirm Patient Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result != JOptionPane.YES_OPTION) {
                System.out.println("Patient deletion cancelled by user.");
                return false;
            }
        }
        
        // Cascading deletion: remove related data first
        if (appointmentRepository != null) {
            appointmentRepository.deleteAllByPatientId(patientId);
        } else {
            System.err.println("Warning: AppointmentRepository not set. Appointments not deleted for patient " + patientId);
        }
        
        if (prescriptionRepository != null) {
            prescriptionRepository.deleteAllByPatientId(patientId);
        } else {
            System.err.println("Warning: PrescriptionRepository not set. Prescriptions not deleted for patient " + patientId);
        }
        
        if (medicalRecordRepository != null) {
            medicalRecordRepository.deleteRecord(patientId);
        } else {
            System.err.println("Warning: MedicalRecordRepository not set. Medical record not deleted for patient " + patientId);
        }
        
        // Finally, remove patient from in-memory list
        patients.remove(patient);
        
        // Save updated patient list to CSV
        saveAll();
        
        System.out.println("Successfully deleted patient " + patientId + " and all related data.");
        return true;
    }
    
    /**
     * Deletes a patient with confirmation dialog (default behavior).
     * 
     * @param patientId The patient ID to delete
     * @return true if deletion was successful, false if cancelled or failed
     */
    public boolean deletePatient(String patientId) {
        return deletePatient(patientId, false);
    }
    
    /**
     * Saves all patients to the CSV file.
     */
    private void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            // Write header
            bw.write("patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode,emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id");
            bw.newLine();
            
            // Write all patients
            for (Patient p : patients) {
                bw.write(p.getId() + ",");
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
            
        } catch (IOException ex) {
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
