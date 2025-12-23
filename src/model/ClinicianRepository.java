package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianRepository {

    private final List<Clinician> clinicians = new ArrayList<>();
    private final String csvPath;

    public ClinicianRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    public List<String> getAllIds() {
    List<String> ids = new ArrayList<>();
    for (Clinician c : clinicians) ids.add(c.getId());
    return ids;
}


    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                Clinician c = new Clinician(
                        row[0],   // id
                        row[1],   // title
                        row[2],   // first
                        row[3],   // last
                        row[4],   // speciality
                        row[5],   // gmc
                        row[6],   // phone
                        row[7],   // email
                        row[8],   // workplace id
                        row[9],   // workplace type
                        row[10],  // employment
                        row[11]   // start date
                );
                clinicians.add(c);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians: " + ex.getMessage());
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
            CsvUtils.appendLine(csvPath, new String[]{
                    c.getId(), c.getTitle(), c.getFirstName(), c.getLastName(),
                    c.getSpeciality(), c.getGmcNumber(), c.getPhone(), c.getEmail(),
                    c.getWorkplaceId(), c.getWorkplaceType(), c.getEmploymentStatus(),
                    c.getStartDate()
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
