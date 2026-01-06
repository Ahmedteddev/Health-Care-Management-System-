package model;

import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private static AppointmentRepository instance;
    private final List<Appointment> appointments = new ArrayList<>();
    private final String csvPath;

    public AppointmentRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
        if (instance == null) {
            instance = this;
        }
    }
    
    public static synchronized AppointmentRepository getInstance(String csvPath) {
        if (instance == null) {
            instance = new AppointmentRepository(csvPath);
        }
        return instance;
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                // FIX: Skip empty rows or null rows
                if (row == null || row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) {
                    continue;
                }
                
                Appointment a = new Appointment(
                        row[0], row[1], row[2], row[3], row[4], row[5],
                        row[6], row[7], row[8], row[9], row[10], row[11], row[12]
                );
                appointments.add(a);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load appointments: " + ex.getMessage());
        }
    }

    public List<Appointment> getAll() {
        return appointments;
    }

    public String generateNewId() {
        int max = 0;
        for (Appointment a : appointments) {
            try {
                int n = Integer.parseInt(a.getId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignore) {}
        }
        return String.format("A%03d", max + 1);
    }

    public void add(Appointment a) {
        appointments.add(a);
        saveAll();
    }

    public void addAndAppend(Appointment a) {
        appointments.add(a);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    a.getId(), a.getPatientId(), a.getClinicianId(), a.getFacilityId(),
                    a.getAppointmentDate(), a.getAppointmentTime(), a.getDurationMinutes(),
                    a.getAppointmentType(), a.getStatus(), a.getReasonForVisit(),
                    a.getNotes(), a.getCreatedDate(), a.getLastModified()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append appointment: " + ex.getMessage());
        }
    }

    public void update(Appointment updated) {
        updateAppointment(updated);
    }
    
    public void updateAppointment(Appointment updated) {
        if (updated == null) {
            System.err.println("Cannot update null appointment.");
            return;
        }
        
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(updated.getId())) {
                appointments.set(i, updated);
                saveAll();
                System.out.println("Successfully updated appointment " + updated.getId());
                return;
            }
        }
        
        System.err.println("Appointment with ID " + updated.getId() + " not found for update.");
    }

    public void delete(String appointmentId) {
        Appointment toRemove = null;
        for (Appointment a : appointments) {
            if (a.getId().equals(appointmentId)) {
                toRemove = a;
                break;
            }
        }
        if (toRemove != null) {
            appointments.remove(toRemove);
            saveAll();
        }
    }

    public void remove(Appointment a) {
        appointments.remove(a);
        saveAll();
    }

    public Appointment findById(String id) {
        if (id == null) return null;
        String tid = id.trim();
        for (Appointment a : appointments)
            if (a.getId().trim().equals(tid)) return a;
        return null;
    }
    
    // Get all appointments for a patient
    public List<Appointment> getByPatientId(String patientId) {
        List<Appointment> result = new ArrayList<>();
        if (patientId == null || patientId.isEmpty()) {
            return result;
        }
        // FIX: Added .trim() to both sides to ensure matching works with CSV whitespace
        String trimmedId = patientId.trim();
        for (Appointment appointment : appointments) {
            String targetId = appointment.getPatientId() != null ? appointment.getPatientId().trim() : "";
            if (trimmedId.equalsIgnoreCase(targetId)) {
                result.add(appointment);
            }
        }
        return result;
    }
    
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    // Delete all appointments for a patient
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete appointments: patient ID is null or empty.");
            return;
        }
        
        String trimmedId = patientId.trim();
        int removedCount = 0;
        for (Appointment a : appointments) {
            String targetId = a.getPatientId() != null ? a.getPatientId().trim() : "";
            if (trimmedId.equalsIgnoreCase(targetId)) {
                removedCount++;
            }
        }
        
        appointments.removeIf(appointment -> {
            String targetId = appointment.getPatientId() != null ? appointment.getPatientId().trim() : "";
            return trimmedId.equalsIgnoreCase(targetId);
        });
        saveAll();
        
        System.out.println("Deleted " + removedCount + " appointment(s) for patient " + patientId);
    }

    public void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
            bw.write("appointment_id,patient_id,clinician_id,facility_id,appointment_date,");
            bw.write("appointment_time,duration_minutes,appointment_type,status,reason_for_visit,");
            bw.write("notes,created_date,last_modified");
            bw.newLine();
            
            for (Appointment a : appointments) {
                bw.write(escapeCsv(a.getId()) + ",");
                bw.write(escapeCsv(a.getPatientId()) + ",");
                bw.write(escapeCsv(a.getClinicianId()) + ",");
                bw.write(escapeCsv(a.getFacilityId()) + ",");
                bw.write(escapeCsv(a.getAppointmentDate()) + ",");
                bw.write(escapeCsv(a.getAppointmentTime()) + ",");
                bw.write(escapeCsv(a.getDurationMinutes()) + ",");
                bw.write(escapeCsv(a.getAppointmentType()) + ",");
                bw.write(escapeCsv(a.getStatus()) + ",");
                bw.write(escapeCsv(a.getReasonForVisit()) + ",");
                bw.write(escapeCsv(a.getNotes()) + ",");
                bw.write(escapeCsv(a.getCreatedDate()) + ",");
                bw.write(escapeCsv(a.getLastModified()));
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Failed to save appointments: " + ex.getMessage());
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