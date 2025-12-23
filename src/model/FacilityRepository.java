package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FacilityRepository {

    private final List<Facility> facilities = new ArrayList<>();
    private final String csvPath;

    public FacilityRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }
    
    public List<String> getAllIds() {
    List<String> ids = new ArrayList<>();
    for (Facility f : facilities) {
        ids.add(f.getId());
    }
    
    
    return ids;
}

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                // Read ALL columns correctly
                String id            = row[0];
                String name          = row[1];
                String type          = row[2];
                String address       = row[3];
                String postcode      = row[4];
                String phone         = row[5];
                String email         = row[6];
                String openingHours  = row[7];
                String managerName   = row[8];

                int capacity = 0;
                try {
                    capacity = Integer.parseInt(row[9]);
                } catch (Exception ex) {
                    System.out.println("Warning: Invalid capacity → " + row[9]);
                }

                String specialities = row[10];

                // Create Facility object correctly
                Facility f = new Facility(
                        id, name, type,
                        address, postcode, phone,
                        email, openingHours, managerName,
                        capacity, specialities
                );

                facilities.add(f);
            }
        } catch (IOException ex) {
            System.err.println("Failed to load facilities: " + ex.getMessage());
        }
    }

    public List<Facility> getAll() {
        return facilities;
    }

    public Facility findById(String id) {
        for (Facility f : facilities) {
            if (f.getId().equals(id)) return f;
        }
        return null;
    }
}
