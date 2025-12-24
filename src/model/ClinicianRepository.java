package model;

import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianRepository {

    private final List<Clinician> clinicians = new ArrayList<>();
    private final String csvPath;
    private static final int EXPECTED_COLUMNS = 12;

    public ClinicianRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicians) ids.add(c.getId());
        return ids;
    }

    /**
     * Loads clinicians from CSV file.
     * CSV structure: clinician_id, first_name, last_name, title, speciality,
     *                 gmc_number, phone_number, email, workplace_id,
     *                 workplace_type, employment_status, start_date
     */
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                // Data integrity check: skip rows with insufficient columns
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid clinician row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                // Map CSV row to Clinician constructor - matching CSV headers exactly
                // CSV order: clinician_id (0), first_name (1), last_name (2), title (3),
                //            speciality (4), gmc_number (5), phone_number (6),
                //            email (7), workplace_id (8), workplace_type (9),
                //            employment_status (10), start_date (11)
                Clinician c = new Clinician(
                        row[0],   // clinicianId
                        row[1],   // firstName
                        row[2],   // lastName
                        row[3],   // title
                        row[4],   // speciality
                        row[5],   // gmcNumber
                        row[6],   // phoneNumber
                        row[7],   // email
                        row[8],   // workplaceId
                        row[9],   // workplaceType
                        row[10],  // employmentStatus
                        row[11]   // startDate
                );
                clinicians.add(c);
            }
            
            System.out.println("Loaded " + clinicians.size() + " clinicians from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading clinicians: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // ============================================================
    // AUTO-ID: C001 → C002 → C003...
    // ============================================================
    public String generateNewId() {
        int max = 0;
        for (Clinician c : clinicians) {
            try {
                int n = Integer.parseInt(c.getId().substring(1));
                if (n > max) max = n;
            } catch (Exception ignored) {}
        }
        return String.format("C%03d", max + 1);
    }

    // ============================================================
    // ADD + APPEND TO CSV
    // ============================================================
    public void addAndAppend(Clinician c) {
        clinicians.add(c);
        try {
            // Match CSV header order exactly: clinician_id, first_name, last_name, title, speciality,
            //                                  gmc_number, phone_number, email, workplace_id,
            //                                  workplace_type, employment_status, start_date
            CsvUtils.appendLine(csvPath, new String[]{
                    c.getClinicianId(),  // clinician_id
                    c.getFirstName(),    // first_name
                    c.getLastName(),     // last_name
                    c.getTitle(),        // title
                    c.getSpeciality(),   // speciality
                    c.getGmcNumber(),    // gmc_number
                    c.getPhoneNumber(),  // phone_number
                    c.getEmail(),        // email
                    c.getWorkplaceId(),  // workplace_id
                    c.getWorkplaceType(), // workplace_type
                    c.getEmploymentStatus(), // employment_status
                    c.getStartDate()     // start_date
            });
        } catch (IOException ex) {
            System.err.println("Failed to append clinician: " + ex.getMessage());
        }
    }

    public List<Clinician> getAll() {
        return clinicians;
    }

    // ============================================================
    // REMOVE
    // ============================================================
    public void remove(Clinician c) {
        clinicians.remove(c);
    }

    public Clinician findById(String id) {
        for (Clinician c : clinicians)
            if (c.getId().equals(id)) return c;
        return null;
    }
}
