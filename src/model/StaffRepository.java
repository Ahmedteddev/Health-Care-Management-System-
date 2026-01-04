package model;

// Handles loading and saving staff and clinician data from CSV (uses singleton pattern)
import util.CsvUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaffRepository {
    
    private static StaffRepository instance;
    private final List<Staff> staffList = new ArrayList<>();
    private final List<Clinician> clinicianList = new ArrayList<>();
    private final String staffCsvPath;
    private final String clinicianCsvPath;
    
    public StaffRepository(String staffCsvPath, String clinicianCsvPath) {
        this.staffCsvPath = staffCsvPath;
        this.clinicianCsvPath = clinicianCsvPath;
        loadStaff();
        loadClinicians();
        if (instance == null) {
            instance = this;
        }
    }
    
    // Get the singleton instance (only one copy of the data)
    public static synchronized StaffRepository getInstance(String staffCsvPath, String clinicianCsvPath) {
        if (instance == null) {
            instance = new StaffRepository(staffCsvPath, clinicianCsvPath);
        }
        return instance;
    }
    
    // Load staff from staff.csv
    private void loadStaff() {
        final int EXPECTED_COLUMNS = 12;
        
        try {
            List<String[]> rows = CsvUtils.readCsv(staffCsvPath);
            
            for (String[] row : rows) {
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid staff row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                Staff staff = new Staff(
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
                
                staffList.add(staff);
            }
            
            System.out.println("Loaded " + staffList.size() + " staff members from " + staffCsvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load staff from CSV file: " + staffCsvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("The staff list will start empty.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading staff: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Load clinicians from clinicians.csv
    private void loadClinicians() {
        final int EXPECTED_COLUMNS = 12;
        
        try {
            List<String[]> rows = CsvUtils.readCsv(clinicianCsvPath);
            
            for (String[] row : rows) {
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid clinician row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                Clinician clinician = new Clinician(
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
                
                clinicianList.add(clinician);
            }
            
            System.out.println("Loaded " + clinicianList.size() + " clinicians from " + clinicianCsvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load clinicians from CSV file: " + clinicianCsvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("The clinician list will start empty.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading clinicians: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public List<Staff> getAllStaff() {
        return new ArrayList<>(staffList);
    }
    
    public List<Clinician> getAllClinicians() {
        return new ArrayList<>(clinicianList);
    }
    
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(staffList);
        allUsers.addAll(clinicianList);
        return allUsers;
    }
    
    public Staff findStaffById(String id) {
        if (id == null) {
            return null;
        }
        
        for (Staff staff : staffList) {
            if (id.equals(staff.getStaffId()) || id.equals(staff.getId())) {
                return staff;
            }
        }
        return null;
    }
    
    public Clinician findClinicianById(String id) {
        if (id == null) {
            return null;
        }
        
        for (Clinician clinician : clinicianList) {
            if (id.equals(clinician.getClinicianId()) || id.equals(clinician.getId())) {
                return clinician;
            }
        }
        return null;
    }
    
    public User findUserById(String id) {
        if (id == null) {
            return null;
        }
        
        Staff staff = findStaffById(id);
        if (staff != null) {
            return staff;
        }
        
        Clinician clinician = findClinicianById(id);
        if (clinician != null) {
            return clinician;
        }
        
        return null;
    }
    
    // Add a new staff member and append to CSV
    public void addStaff(Staff staff) {
        if (staff == null) {
            System.err.println("Cannot add null staff to repository.");
            return;
        }
        
        if (findStaffById(staff.getStaffId()) != null) {
            System.err.println("Staff with ID " + staff.getStaffId() + " already exists.");
            return;
        }
        
        staffList.add(staff);
        
        try {
            String[] rowData = {
                staff.getStaffId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getRole(),
                staff.getDepartment(),
                staff.getFacilityId(),
                staff.getPhoneNumber(),
                staff.getEmail(),
                staff.getEmploymentStatus(),
                staff.getStartDate(),
                staff.getLineManager(),
                staff.getAccessLevel()
            };
            
            CsvUtils.appendLine(staffCsvPath, rowData);
            System.out.println("Successfully added staff " + staff.getStaffId() + " to repository and CSV.");
            
        } catch (IOException ex) {
            System.err.println("Failed to append staff to CSV file: " + staffCsvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Staff added to repository but not persisted to file.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while adding staff: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Add a new clinician and append to CSV
    public void addClinician(Clinician clinician) {
        if (clinician == null) {
            System.err.println("Cannot add null clinician to repository.");
            return;
        }
        
        if (findClinicianById(clinician.getClinicianId()) != null) {
            System.err.println("Clinician with ID " + clinician.getClinicianId() + " already exists.");
            return;
        }
        
        clinicianList.add(clinician);
        
        try {
            String[] rowData = {
                clinician.getClinicianId(),
                clinician.getFirstName(),
                clinician.getLastName(),
                clinician.getTitle(),
                clinician.getSpeciality(),
                clinician.getGmcNumber(),
                clinician.getPhoneNumber(),
                clinician.getEmail(),
                clinician.getWorkplaceId(),
                clinician.getWorkplaceType(),
                clinician.getEmploymentStatus(),
                clinician.getStartDate()
            };
            
            CsvUtils.appendLine(clinicianCsvPath, rowData);
            System.out.println("Successfully added clinician " + clinician.getClinicianId() + " to repository and CSV.");
            
        } catch (IOException ex) {
            System.err.println("Failed to append clinician to CSV file: " + clinicianCsvPath);
            System.err.println("Error: " + ex.getMessage());
            System.err.println("Clinician added to repository but not persisted to file.");
        } catch (Exception ex) {
            System.err.println("Unexpected error while adding clinician: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Update a staff member and save to CSV
    public void updateStaff(Staff staff) {
        if (staff == null) {
            System.err.println("Cannot update null staff.");
            return;
        }
        
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equals(staff.getStaffId())) {
                staffList.set(i, staff);
                saveAllStaff();
                System.out.println("Successfully updated staff " + staff.getStaffId());
                return;
            }
        }
        
        System.err.println("Staff with ID " + staff.getStaffId() + " not found for update.");
    }
    
    // Remove a staff member and save to CSV
    public void removeStaff(Staff staff) {
        if (staff != null) {
            staffList.remove(staff);
            saveAllStaff();
        }
    }
    
    // Save all staff back to CSV
    private void saveAllStaff() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(staffCsvPath))) {
            bw.write("staff_id,first_name,last_name,role,department,facility_id,phone_number,email,employment_status,start_date,line_manager,access_level");
            bw.newLine();
            
            for (Staff staff : staffList) {
                bw.write(escapeCsv(staff.getStaffId()) + ",");
                bw.write(escapeCsv(staff.getFirstName()) + ",");
                bw.write(escapeCsv(staff.getLastName()) + ",");
                bw.write(escapeCsv(staff.getRole()) + ",");
                bw.write(escapeCsv(staff.getDepartment()) + ",");
                bw.write(escapeCsv(staff.getFacilityId()) + ",");
                bw.write(escapeCsv(staff.getPhoneNumber()) + ",");
                bw.write(escapeCsv(staff.getEmail()) + ",");
                bw.write(escapeCsv(staff.getEmploymentStatus()) + ",");
                bw.write(escapeCsv(staff.getStartDate()) + ",");
                bw.write(escapeCsv(staff.getLineManager()) + ",");
                bw.write(escapeCsv(staff.getAccessLevel()));
                bw.newLine();
            }
            
        } catch (java.io.IOException ex) {
            System.err.println("Failed to save staff to CSV file: " + staffCsvPath);
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
    
    public void removeClinician(Clinician clinician) {
        if (clinician != null) {
            clinicianList.remove(clinician);
        }
    }
}


