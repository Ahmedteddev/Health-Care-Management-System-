package model;

import java.io.IOException;
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
                // CSV columns (14):
                // 0: appointment_id
                // 1: patient_id
                // 2: clinician_id
                // 3: facility_id
                // 4: appointment_date
                // 5: appointment_time
                // 6: duration_minutes
                // 7: appointment_type
                // 8: status
                // 9: reason_for_visit
                //10: notes
                //11: created_date
                //12: last_modified

                Appointment a = new Appointment(
                        row[0],  // id
                        row[1],  // patient_id
                        row[2],  // clinician_id
                        row[3],  // facility_id
                        row[4],  // appointment_date
                        row[5],  // appointment_time
                        row[6],  // duration_minutes
                        row[7],  // appointment_type
                        row[8],  // status
                        row[9],  // reason_for_visit
                        row[10], // notes
                        row[11], // created_date
                        row[12]  // last_modified
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

    // Optional but handy
    public String generateNewId() {
        int max = 0;
        for (Appointment a : appointments) {
            try {
                int n = Integer.parseInt(a.getId().substring(1)); // "A001" → 1
                if (n > max) max = n;
            } catch (Exception ignore) {}
        }
        return String.format("A%03d", max + 1);
    }

    public void add(Appointment a) {
        appointments.add(a);
    }

    public void addAndAppend(Appointment a) {
        appointments.add(a);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes(),
                    a.getCreatedDate(),
                    a.getLastModified()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append appointment: " + ex.getMessage());
        }
    }

    public void remove(Appointment a) {
        appointments.remove(a);
    }

    public Appointment findById(String id) {
        for (Appointment a : appointments)
            if (a.getId().equals(id)) return a;
        return null;
    }
}
