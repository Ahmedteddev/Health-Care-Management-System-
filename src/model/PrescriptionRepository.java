package model;

import java.io.IOException;
import java.util.*;

public class PrescriptionRepository {

    private final List<Prescription> prescriptions = new ArrayList<>();
    private final String csvPath;

    // CSV has EXACTLY 15 columns
    private static final int COLUMN_COUNT = 15;

    public PrescriptionRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    // ============================================================
    // LOAD ALL PRESCRIPTIONS SAFELY
    // ============================================================
    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                // Skip header row
                if (row.length == 0 || row[0].equalsIgnoreCase("prescription_id"))
                    continue;

                // Guarantee 15 columns to prevent ArrayIndexOutOfBounds
                String[] safe = new String[COLUMN_COUNT];
                for (int i = 0; i < COLUMN_COUNT; i++) {
                    safe[i] = (i < row.length) ? row[i] : "";
                }

                Prescription p = new Prescription(
                        safe[0], // prescription_id
                        safe[1], // patient_id
                        safe[2], // clinician_id
                        safe[3], // appointment_id
                        safe[4], // prescription_date
                        safe[5], // medication_name
                        safe[6], // dosage
                        safe[7], // frequency
                        safe[8], // duration_days
                        safe[9], // quantity
                        safe[10],// instructions
                        safe[11],// pharmacy_name
                        safe[12],// status
                        safe[13],// issue_date
                        safe[14] // collection_date
                );

                prescriptions.add(p);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load prescriptions: " + ex.getMessage());
        }
    }

    public List<Prescription> getAll() {
        return prescriptions;
    }

    // ============================================================
    // AUTO-GENERATE RX IDs
    // ============================================================
    public String generateNewId() {
        int max = 0;
        for (Prescription p : prescriptions) {
            try {
                String id = p.getId();
                if (id != null && id.startsWith("RX")) {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) max = num;
                }
            } catch (Exception ignore) {}
        }
        return String.format("RX%03d", max + 1);
    }

    // ============================================================
    // DROPDOWN OPTIONS
    // ============================================================
    public List<String> getMedicationOptions() {
        Set<String> meds = new TreeSet<>();
        for (Prescription p : prescriptions) {
            if (p.getMedication() != null && !p.getMedication().isBlank())
                meds.add(p.getMedication());
        }
        return new ArrayList<>(meds);
    }

    public List<String> getPharmacyOptions() {
        Set<String> pharms = new TreeSet<>();
        for (Prescription p : prescriptions) {
            if (p.getPharmacyName() != null && !p.getPharmacyName().isBlank())
                pharms.add(p.getPharmacyName());
        }
        return new ArrayList<>(pharms);
    }

    // ============================================================
    // ADD + APPEND TO CSV
    // ============================================================
    public void addAndAppend(Prescription p) {

        prescriptions.add(p);

        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    p.getId(),
                    p.getPatientId(),
                    p.getClinicianId(),
                    p.getAppointmentId(),
                    p.getPrescriptionDate(),
                    p.getMedication(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getDurationDays(),
                    p.getQuantity(),
                    p.getInstructions(),
                    p.getPharmacyName(),
                    p.getStatus(),
                    p.getIssueDate(),
                    p.getCollectionDate()
            });

        } catch (IOException ex) {
            System.err.println("Failed to append prescription: " + ex.getMessage());
        }
    }

    // ============================================================
    // UPDATE IN-MEMORY ENTRY (no CSV rewrite)
    // ============================================================
    public void update(Prescription p) {
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getId().equals(p.getId())) {
                prescriptions.set(i, p);
                return;
            }
        }
    }

    public void removeById(String id) {
        prescriptions.removeIf(p -> p.getId().equals(id));
        // No CSV rewrite—acceptable for coursework
    }
}
