package model;

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

        } catch (IOException ex) {
            System.err.println("Failed to append patient: " + ex.getMessage());
        }
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
}
