package model;

// This model class handles all the saving and loading for Staff and Clinician data from the CSV
// It loads both staff.csv and clinicians.csv files
// Uses a Singleton pattern so we only have one copy of the data
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
    
    // Constructor - creates a new instance and loads data from both CSV files
    public StaffRepository(String staffCsvPath, String clinicianCsvPath) {
        this.staffCsvPath = staffCsvPath;
        this.clinicianCsvPath = clinicianCsvPath;
        loadStaff();
        loadClinicians();
        // If instance is null, set it (allows singleton pattern to work)
        if (instance == null) {
            instance = this;
        }
    }
    
    // Public static method to get the singleton instance
    // Making sure we only have one copy of the data so it doesn't reset
    public static synchronized StaffRepository getInstance(String staffCsvPath, String clinicianCsvPath) {
        if (instance == null) {
            instance = new StaffRepository(staffCsvPath, clinicianCsvPath);
        }
        return instance;
    }
    
    // Loads staff members from staff.csv
    // CSV structure: staff_id, first_name, last_name, role, department,
    //                 facility_id, phone_number, email, employment_status,
    //                 start_date, line_manager, access_level
    private void loadStaff() {
        final int EXPECTED_COLUMNS = 12;
        
        try {
            List<String[]> rows = CsvUtils.readCsv(staffCsvPath);
            
            for (String[] row : rows) {
                // Data integrity check: skip rows with insufficient columns
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid staff row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + "): " + 
                                     String.join(",", row));
                    continue;
                }
                
                // Map CSV row to Staff constructor - matching CSV headers exactly
                // CSV order: staff_id (0), first_name (1), last_name (2), role (3),
                //            department (4), facility_id (5), phone_number (6),
                //            email (7), employment_status (8), start_date (9),
                //            line_manager (10), access_level (11)
                Staff staff = new Staff(
                    row[0],   // staffId
                    row[1],   // firstName
                    row[2],   // lastName
                    row[3],   // role
                    row[4],   // department
                    row[5],   // facilityId
                    row[6],   // phoneNumber
                    row[7],   // email
                    row[8],   // employmentStatus
                    row[9],   // startDate
                    row[10], // lineManager
                    row[11]  // accessLevel
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
    
    // Loads clinicians from clinicians.csv
    // CSV structure: clinician_id, first_name, last_name, title, speciality,
    //                 gmc_number, phone_number, email, workplace_id,
    //                 workplace_type, employment_status, start_date
    private void loadClinicians() {
        final int EXPECTED_COLUMNS = 12;
        
        try {
            List<String[]> rows = CsvUtils.readCsv(clinicianCsvPath);
            
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
                Clinician clinician = new Clinician(
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
    
    // Returns all staff members (non-clinicians)
    public List<Staff> getAllStaff() {
        return new ArrayList<>(staffList); // Return a copy to prevent external modification
    }
    
    // Returns all clinicians
    public List<Clinician> getAllClinicians() {
        return new ArrayList<>(clinicianList); // Return a copy to prevent external modification
    }
    
    // Returns a unified list of all Users (both Staff and Clinicians)
    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        allUsers.addAll(staffList);
        allUsers.addAll(clinicianList);
        return allUsers;
    }
    
    // Finds a staff member by their ID
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
    
    // Finds a clinician by their ID
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
    
    // Finds any user (Staff or Clinician) by their ID
    public User findUserById(String id) {
        if (id == null) {
            return null;
        }
        
        // Try staff first
        Staff staff = findStaffById(id);
        if (staff != null) {
            return staff;
        }
        
        // Try clinicians
        Clinician clinician = findClinicianById(id);
        if (clinician != null) {
            return clinician;
        }
        
        return null;
    }
    
    // Adds a new staff member to the repository and appends it to the CSV file
    public void addStaff(Staff staff) {
        if (staff == null) {
            System.err.println("Cannot add null staff to repository.");
            return;
        }
        
        // Check if staff already exists
        if (findStaffById(staff.getStaffId()) != null) {
            System.err.println("Staff with ID " + staff.getStaffId() + " already exists.");
            return;
        }
        
        // Add to in-memory list
        staffList.add(staff);
        
        // Append to CSV file
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
    
    // Adds a new clinician to the repository and appends it to the CSV file
    public void addClinician(Clinician clinician) {
        if (clinician == null) {
            System.err.println("Cannot add null clinician to repository.");
            return;
        }
        
        // Check if clinician already exists
        if (findClinicianById(clinician.getClinicianId()) != null) {
            System.err.println("Clinician with ID " + clinician.getClinicianId() + " already exists.");
            return;
        }
        
        // Add to in-memory list
        clinicianList.add(clinician);
        
        // Append to CSV file
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
    
    // Updates an existing staff member in the repository and saves to CSV
    public void updateStaff(Staff staff) {
        if (staff == null) {
            System.err.println("Cannot update null staff.");
            return;
        }
        
        // Find and update the staff in the list
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equals(staff.getStaffId())) {
                staffList.set(i, staff);
                // Save all to CSV
                saveAllStaff();
                System.out.println("Successfully updated staff " + staff.getStaffId());
                return;
            }
        }
        
        System.err.println("Staff with ID " + staff.getStaffId() + " not found for update.");
    }
    
    // Removes a staff member from the repository
    public void removeStaff(Staff staff) {
        if (staff != null) {
            staffList.remove(staff);
            // Save updated list to CSV
            saveAllStaff();
        }
    }
    
    // Saves all staff to the CSV file
    private void saveAllStaff() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(staffCsvPath))) {
            // Write header
            bw.write("staff_id,first_name,last_name,role,department,facility_id,phone_number,email,employment_status,start_date,line_manager,access_level");
            bw.newLine();
            
            // Write all staff
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
    
    // Escapes CSV values that contain commas or quotes
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
    
    // Removes a clinician from the repository
    public void removeClinician(Clinician clinician) {
        if (clinician != null) {
            clinicianList.remove(clinician);
        }
    }
}


