package model;

import util.CsvUtils;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class PrescriptionRepository {

    private final List<Prescription> prescriptions = new ArrayList<>();
    private final String csvPath;

    private static final int COLUMN_COUNT = 15;

    public PrescriptionRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                // FIX: Skip empty rows or null rows
                if (row == null || row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) {
                    continue;
                }

                // Skip header row
                if (row[0].equalsIgnoreCase("prescription_id"))
                    continue;

                String[] safe = new String[COLUMN_COUNT];
                for (int i = 0; i < COLUMN_COUNT; i++) {
                    safe[i] = (i < row.length) ? row[i] : "";
                }

                Prescription p = new Prescription(
                        safe[0], safe[1], safe[2], safe[3], safe[4], safe[5],
                        safe[6], safe[7], safe[8], safe[9], safe[10], safe[11],
                        safe[12], safe[13], safe[14]
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
    
    // Get all prescriptions for a patient
    public List<Prescription> getByPatientId(String patientId) {
        List<Prescription> result = new ArrayList<>();
        if (patientId == null || patientId.isEmpty()) {
            return result;
        }
        // FIX: Added .trim() and equalsIgnoreCase for robust filtering
        String trimmedId = patientId.trim();
        for (Prescription prescription : prescriptions) {
            String targetId = prescription.getPatientId() != null ? prescription.getPatientId().trim() : "";
            if (trimmedId.equalsIgnoreCase(targetId)) {
                result.add(prescription);
            }
        }
        return result;
    }

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

    public void addAndAppend(Prescription p) {
        prescriptions.add(p);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    p.getId(), p.getPatientId(), p.getClinicianId(), p.getAppointmentId(),
                    p.getPrescriptionDate(), p.getMedication(), p.getDosage(),
                    p.getFrequency(), p.getDurationDays(), p.getQuantity(),
                    p.getInstructions(), p.getPharmacyName(), p.getStatus(),
                    p.getIssueDate(), p.getCollectionDate()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append prescription: " + ex.getMessage());
        }
    }
    
    public void generatePrescriptionFile(Prescription p, String practitionerName, String practitionerId) {
        File prescriptionDir = new File("src/data/prescription");
        if (!prescriptionDir.exists()) {
            prescriptionDir.mkdirs();
        }
        
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "prescription_" + p.getPatientId() + "_" + dateStr + ".txt";
        File prescriptionFile = new File(prescriptionDir, filename);
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(prescriptionFile))) {
            writer.write("PRESCRIPTION");
            writer.newLine();
            writer.write("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.newLine();
            writer.write("Practitioner: " + (practitionerName != null ? practitionerName : ""));
            writer.newLine();
            writer.write("Patient ID: " + p.getPatientId());
            writer.newLine();
            writer.write("Medication: " + (p.getMedication() != null ? p.getMedication() : ""));
            writer.newLine();
            writer.write("Dosage: " + (p.getDosage() != null ? p.getDosage() : ""));
            writer.newLine();
            writer.write("Notes: " + (p.getInstructions() != null ? p.getInstructions() : ""));
            writer.newLine();
            writer.newLine();
            writer.write("Digitally signed by the practitioner.");
            writer.newLine();
            System.out.println("Prescription file generated: " + prescriptionFile.getAbsolutePath());
        } catch (IOException ex) {
            System.err.println("Failed to generate prescription file: " + ex.getMessage());
        }
    }

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
    }
    
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) return;
        
        String trimmedId = patientId.trim();
        prescriptions.removeIf(p -> {
            String targetId = p.getPatientId() != null ? p.getPatientId().trim() : "";
            return trimmedId.equalsIgnoreCase(targetId);
        });
        saveAll();
    }
    
    public void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            bw.write("prescription_id,patient_id,clinician_id,appointment_id,prescription_date,");
            bw.write("medication_name,dosage,frequency,duration_days,quantity,instructions,");
            bw.write("pharmacy_name,status,issue_date,collection_date");
            bw.newLine();
            
            for (Prescription p : prescriptions) {
                bw.write(escapeCsv(p.getId()) + ",");
                bw.write(escapeCsv(p.getPatientId()) + ",");
                bw.write(escapeCsv(p.getClinicianId()) + ",");
                bw.write(escapeCsv(p.getAppointmentId()) + ",");
                bw.write(escapeCsv(p.getPrescriptionDate()) + ",");
                bw.write(escapeCsv(p.getMedication()) + ",");
                bw.write(escapeCsv(p.getDosage()) + ",");
                bw.write(escapeCsv(p.getFrequency()) + ",");
                bw.write(escapeCsv(p.getDurationDays()) + ",");
                bw.write(escapeCsv(p.getQuantity()) + ",");
                bw.write(escapeCsv(p.getInstructions()) + ",");
                bw.write(escapeCsv(p.getPharmacyName()) + ",");
                bw.write(escapeCsv(p.getStatus()) + ",");
                bw.write(escapeCsv(p.getIssueDate()) + ",");
                bw.write(escapeCsv(p.getCollectionDate()));
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Failed to save prescriptions: " + ex.getMessage());
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