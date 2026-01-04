package model;

import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClinicianRepository {

    private static ClinicianRepository instance;
    private final List<Clinician> clinicians = new ArrayList<>();
    private final String csvPath;
    private static final int EXPECTED_COLUMNS = 12;

    public ClinicianRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
        if (instance == null) {
            instance = this;
        }
    }
    
    // Get the singleton instance (only one copy of the data)
    public static synchronized ClinicianRepository getInstance(String csvPath) {
        if (instance == null) {
            instance = new ClinicianRepository(csvPath);
        }
        return instance;
    }
    
    public List<String> getAllIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicians) ids.add(c.getId());
        return ids;
    }

    // Load clinicians from CSV
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid clinician row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                // Check the CSV title to see if we should make a GP object or a Nurse object
                String title = row[3] != null ? row[3].toLowerCase() : "";
                String speciality = row[4] != null ? row[4].toLowerCase() : "";
                
                Clinician myNewClinician;
                
                // If the title/speciality contains "GP" or "General Practice" -> new GP(...)
                if (title.contains("gp") || speciality.contains("general practice")) {
                    GP myNewGP = new GP(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        row[8],
                        row[9],
                        row[10],
                        row[11]
                    );
                    myNewClinician = myNewGP;
                }
                // If the title/speciality contains "Consultant" or specialty areas -> new Specialist(...)
                else if (title.contains("consultant") || 
                         speciality.contains("cardiology") || 
                         speciality.contains("neurology") ||
                         speciality.contains("orthopaedics") ||
                         speciality.contains("dermatology") ||
                         speciality.contains("oncology") ||
                         speciality.contains("pediatrics")) {
                    Specialist specialistData = new Specialist(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        row[8],
                        row[9],
                        row[10],
                        row[11]
                    );
                    myNewClinician = specialistData;
                }
                // If the title/speciality contains "Nurse" -> new Nurse(...)
                else if (title.contains("nurse")) {
                    Nurse myNewNurse = new Nurse(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        row[8],
                        row[9],
                        row[10],
                        row[11]
                    );
                    myNewClinician = myNewNurse;
                }
                // Default: create a generic Clinician if we can't determine the type
                else {
                    myNewClinician = new Clinician(
                        row[0],
                        row[1],
                        row[2],
                        row[3],
                        row[4],
                        row[5],
                        row[6],
                        row[7],
                        row[8],
                        row[9],
                        row[10],
                        row[11]
                    );
                }
                
                clinicians.add(myNewClinician);
            }
            
            System.out.println("Loaded " + clinicians.size() + " clinicians from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading clinicians: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    // Generate a new clinician ID (C001, C002, etc.)
    public String generateNewId() {
        int max = 0;
        for (Clinician c : clinicians) {
            try {
                int n = Integer.parseInt(c.getId().substring(1));
                if (n > max) max = n;
                } catch (Exception ignored) {
                }
        }
        return String.format("C%03d", max + 1);
    }

    // Add a new clinician and append to CSV
    public void addAndAppend(Clinician c) {
        clinicians.add(c);
        try {
            CsvUtils.appendLine(csvPath, new String[]{
                    c.getClinicianId(),
                    c.getFirstName(),
                    c.getLastName(),
                    c.getTitle(),
                    c.getSpeciality(),
                    c.getGmcNumber(),
                    c.getPhoneNumber(),
                    c.getEmail(),
                    c.getWorkplaceId(),
                    c.getWorkplaceType(),
                    c.getEmploymentStatus(),
                    c.getStartDate()
            });
        } catch (IOException ex) {
            System.err.println("Failed to append clinician: " + ex.getMessage());
        }
    }

    public List<Clinician> getAll() {
        return clinicians;
    }

    // Update a clinician and save to CSV
    public void updateClinician(Clinician clinician) {
        if (clinician == null) {
            System.err.println("Cannot update null clinician.");
            return;
        }
        
        for (int i = 0; i < clinicians.size(); i++) {
            if (clinicians.get(i).getClinicianId().equals(clinician.getClinicianId())) {
                clinicians.set(i, clinician);
                saveAll();
                System.out.println("Successfully updated clinician " + clinician.getClinicianId());
                return;
            }
        }
        
        System.err.println("Clinician with ID " + clinician.getClinicianId() + " not found for update.");
    }
    
    public void remove(Clinician c) {
        if (c != null) {
            clinicians.remove(c);
        }
    }

    public Clinician findById(String id) {
        for (Clinician c : clinicians)
            if (c.getId().equals(id) || (c.getClinicianId() != null && c.getClinicianId().equals(id))) return c;
        return null;
    }
    
    // Save all clinicians back to CSV
    public void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            bw.write("clinician_id,first_name,last_name,title,speciality,gmc_number,phone_number,email,workplace_id,workplace_type,employment_status,start_date");
            bw.newLine();
            
            for (Clinician c : clinicians) {
                bw.write(escapeCsv(c.getClinicianId()) + ",");
                bw.write(escapeCsv(c.getFirstName()) + ",");
                bw.write(escapeCsv(c.getLastName()) + ",");
                bw.write(escapeCsv(c.getTitle()) + ",");
                bw.write(escapeCsv(c.getSpeciality()) + ",");
                bw.write(escapeCsv(c.getGmcNumber()) + ",");
                bw.write(escapeCsv(c.getPhoneNumber()) + ",");
                bw.write(escapeCsv(c.getEmail()) + ",");
                bw.write(escapeCsv(c.getWorkplaceId()) + ",");
                bw.write(escapeCsv(c.getWorkplaceType()) + ",");
                bw.write(escapeCsv(c.getEmploymentStatus()) + ",");
                bw.write(escapeCsv(c.getStartDate()));
                bw.newLine();
            }
            
        } catch (java.io.IOException ex) {
            System.err.println("Failed to save clinicians to CSV file: " + csvPath);
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
