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
    
    // Get all prescriptions for a patient
    public List<Prescription> getByPatientId(String patientId) {
        List<Prescription> result = new ArrayList<>();
        if (patientId == null || patientId.isEmpty()) {
            return result;
        }
        for (Prescription prescription : prescriptions) {
            if (patientId.equals(prescription.getPatientId())) {
                result.add(prescription);
            }
        }
        return result;
    }

    // Generate a new prescription ID (RX001, RX002, etc.)
    public String generateNewId() {
        int max = 0;
        for (Prescription p : prescriptions) {
            try {
                String id = p.getId();
                if (id != null && id.startsWith("RX")) {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) max = num;
                }
            } catch (Exception ignore) {
            }
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

    // Add a new prescription and append to CSV
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
    
    // Generate a prescription text file and save it
    public void generatePrescriptionFile(Prescription p, String practitionerName, String practitionerId) {
        File prescriptionDir = new File("src/data/prescription");
        if (!prescriptionDir.exists()) {
            prescriptionDir.mkdirs(); // Use mkdirs() to create parent directories if needed
        }
        
        // Format date for filename
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
    }
    
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    // Delete all prescriptions for a patient
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete prescriptions: patient ID is null or empty.");
            return;
        }
        
        int removedCount = 0;
        for (Prescription p : prescriptions) {
            if (patientId.equals(p.getPatientId())) {
                removedCount++;
            }
        }
        
        prescriptions.removeIf(prescription -> patientId.equals(prescription.getPatientId()));
        saveAll();
        
        System.out.println("Deleted " + removedCount + " prescription(s) for patient " + patientId);
    }
    
    // Save all prescriptions back to CSV
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
            System.err.println("Failed to save prescriptions to CSV file: " + csvPath);
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
