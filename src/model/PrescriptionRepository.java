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
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            for (String[] row : rows) {
                if (row == null || row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) {
                    continue;
                }

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

    // --- NEW METHODS FOR EDIT/DELETE/DASHBOARD ---

    public Prescription findById(String id) {
        if (id == null) return null;
        for (Prescription p : prescriptions) {
            if (id.equals(p.getId())) return p;
        }
        return null;
    }

    /**
     * Filters prescriptions for a specific patient.
     * Required by PatientDashboardController.
     */
    public List<Prescription> getByPatientId(String patientId) {
        List<Prescription> result = new ArrayList<>();
        if (patientId == null) return result;
        for (Prescription p : prescriptions) {
            if (patientId.equals(p.getPatientId())) {
                result.add(p);
            }
        }
        return result;
    }

    public void update(Prescription updatedPrescription) {
        for (int i = 0; i < prescriptions.size(); i++) {
            if (prescriptions.get(i).getId().equals(updatedPrescription.getId())) {
                prescriptions.set(i, updatedPrescription);
                saveAll(); 
                return;
            }
        }
    }

    public void removeById(String id) {
        prescriptions.removeIf(p -> p.getId().equals(id));
        saveAll();
    }

    public void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
            bw.write("prescription_id,patient_id,clinician_id,appointment_id,prescription_date," +
                     "medication_name,dosage,frequency,duration_days,quantity,instructions," +
                     "pharmacy_name,status,issue_date,collection_date");
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

    // --- ORIGINAL FEATURE METHODS ---

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
        } catch (IOException ex) {
            System.err.println("Failed to generate prescription file: " + ex.getMessage());
        }
    }
}