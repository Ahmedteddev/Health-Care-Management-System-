package model;

import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    private final List<Appointment> appointments = new ArrayList<>();
    private final String csvPath;

    public AppointmentRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
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
        for (int i = 0; i < appointments.size(); i++) {
            if (appointments.get(i).getId().equals(updated.getId())) {
                appointments.set(i, updated);
                saveAll();
                return;
            }
        }
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
        for (Appointment a : appointments)
            if (a.getId().equals(id)) return a;
        return null;
    }
    
    /**
     * Deletes all appointments for a specific patient.
     * Alias method to match user's method name requirement.
     * 
     * @param patientId The patient ID whose appointments should be deleted
     */
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    /**
     * Deletes all appointments for a specific patient.
     * 
     * @param patientId The patient ID whose appointments should be deleted
     */
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete appointments: patient ID is null or empty.");
            return;
        }
        
        // Count how many will be removed
        int removedCount = 0;
        for (Appointment a : appointments) {
            if (patientId.equals(a.getPatientId())) {
                removedCount++;
            }
        }
        
        // Remove all appointments matching the patient ID
        appointments.removeIf(appointment -> patientId.equals(appointment.getPatientId()));
        
        // Save updated list to CSV
        saveAll();
        
        System.out.println("Deleted " + removedCount + " appointment(s) for patient " + patientId);
    }

    public void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
            // Write header
            bw.write("appointment_id,patient_id,clinician_id,facility_id,appointment_date,");
            bw.write("appointment_time,duration_minutes,appointment_type,status,reason_for_visit,");
            bw.write("notes,created_date,last_modified");
            bw.newLine();
            
            // Write all appointments
            for (Appointment a : appointments) {
                bw.write(a.getId() + ",");
                bw.write(a.getPatientId() + ",");
                bw.write(a.getClinicianId() + ",");
                bw.write(a.getFacilityId() + ",");
                bw.write(a.getAppointmentDate() + ",");
                bw.write(a.getAppointmentTime() + ",");
                bw.write(a.getDurationMinutes() + ",");
                bw.write(a.getAppointmentType() + ",");
                bw.write(a.getStatus() + ",");
                bw.write(a.getReasonForVisit() + ",");
                bw.write(a.getNotes() + ",");
                bw.write(a.getCreatedDate() + ",");
                bw.write(a.getLastModified());
                bw.newLine();
            }
        } catch (IOException ex) {
            System.err.println("Failed to save appointments: " + ex.getMessage());
        }
    }
}
