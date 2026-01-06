package model;

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
    
    /**
     * Public refresh method to allow controllers to reload data from disk.
     */
    public void refresh() {
        this.patients.clear(); 
        load();
        System.out.println("Repository Refreshed: " + patients.size() + " patients now in memory.");
    }
    
    // Changed to protected so refresh() can definitely access it, though private works too
    private void load() {
        final int EXPECTED_COLUMNS = 14;
        
        try {
            List<String[]> allRows = CsvUtils.readCsv(csvPath);
            
            for (String[] currentRow : allRows) {
                // Skip empty rows and the header row
                if (currentRow == null || currentRow.length == 0 || 
                   (currentRow.length == 1 && currentRow[0].trim().isEmpty()) ||
                    currentRow[0].equalsIgnoreCase("patient_id")) {
                    continue;
                }

                if (currentRow.length < EXPECTED_COLUMNS) {
                    continue;
                }
                
                Patient newPatient = new Patient(
                    currentRow[0], currentRow[1], currentRow[2], currentRow[3],
                    currentRow[4], currentRow[5], currentRow[6], currentRow[7],
                    currentRow[8], currentRow[9], currentRow[10], currentRow[11],
                    currentRow[12], currentRow[13]
                );
                
                patients.add(newPatient);
            }
            
        } catch (IOException ex) {
            System.err.println("Failed to load patients: " + ex.getMessage());
        }
    }
    
    public List<Patient> getAll() {
        return new ArrayList<>(patients);
    }
    
    public Patient findById(String id) {
        if (id == null) return null;
        String trimmedId = id.trim();
        for (Patient patient : patients) {
            String pId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
            if (trimmedId.equalsIgnoreCase(pId)) {
                return patient;
            }
        }
        return null;
    }
    
    public void add(Patient patient) {
        if (patient == null) return;
        patients.add(patient);
        saveToCsv(patient);
    }

    private void saveToCsv(Patient patient) {
        try {
            String[] rowData = {
                patient.getPatientId(), patient.getFirstName(), patient.getLastName(),
                patient.getDateOfBirth(), patient.getNhsNumber(), patient.getGender(),
                patient.getPhoneNumber(), patient.getEmail(), patient.getAddress(),
                patient.getPostcode(), patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone(), patient.getRegistrationDate(),
                patient.getGpSurgeryId()
            };
            CsvUtils.appendLine(csvPath, rowData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public String generateNewId() {
        int max = 0;
        for (Patient patient : patients) {
            String id = patient.getPatientId();
            if (id != null && id.startsWith("P")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }
        return String.format("P%03d", max + 1);
    }

    public void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            bw.write("patient_id,first_name,last_name,date_of_birth,nhs_number,gender,phone_number,email,address,postcode,emergency_contact_name,emergency_contact_phone,registration_date,gp_surgery_id");
            bw.newLine();
            for (Patient p : patients) {
                bw.write(escapeCsv(p.getPatientId()) + "," + escapeCsv(p.getFirstName()) + "," +
                         escapeCsv(p.getLastName()) + "," + escapeCsv(p.getDateOfBirth()) + "," +
                         escapeCsv(p.getNhsNumber()) + "," + escapeCsv(p.getGender()) + "," +
                         escapeCsv(p.getPhoneNumber()) + "," + escapeCsv(p.getEmail()) + "," +
                         escapeCsv(p.getAddress()) + "," + escapeCsv(p.getPostcode()) + "," +
                         escapeCsv(p.getEmergencyContactName()) + "," + escapeCsv(p.getEmergencyContactPhone()) + "," +
                         escapeCsv(p.getRegistrationDate()) + "," + escapeCsv(p.getGpSurgeryId()));
                bw.newLine();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}