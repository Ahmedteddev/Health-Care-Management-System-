package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {

    private final List<Staff> staff = new ArrayList<>();
    private final String csvPath;

    public StaffRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {
                String id = row[0];
                String name = row[1];
                String phone = row[2];
                String email = row[3];
                String position = row[4];
                String facilityId = row[5];

                staff.add(new Staff(id, name, phone, email,
                        position, facilityId));
            }
        } catch (IOException ex) {
            System.err.println("Failed to load staff: " + ex.getMessage());
        }
    }

    public List<Staff> getAll() { return staff; }
}
