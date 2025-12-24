package model;

/**
 * Staff class extending User.
 * Represents staff members in the Healthcare Management System.
 */
public class Staff extends User {
    
    private String staffId;
    private String department;
    private String hireDate;
    
    public Staff() {
        super();
    }
    
    public Staff(String username, String password, String email,
                 String staffId, String department, String hireDate) {
        super(username, password, email);
        this.staffId = staffId;
        this.department = department;
        this.hireDate = hireDate;
    }
    
    // Backward compatibility constructor (for CSV loading)
    public Staff(String staffId, String firstName, String lastName,
                 String phone, String email, String role, String department,
                 String facilityId, String employmentStatus, String startDate,
                 String lineManager, String accessLevel) {
        // Generate username from email or staffId
        super(generateUsername(email, staffId), "default", email);
        this.staffId = staffId;
        this.department = department;
        this.hireDate = startDate;  // hireDate maps to startDate from CSV
    }
    
    private static String generateUsername(String email, String staffId) {
        if (email != null && !email.isEmpty()) {
            return email.split("@")[0];
        }
        return staffId;
    }
    
    // Backward compatibility methods
    public String getId() {
        return staffId;
    }
    
    public void setId(String id) {
        this.staffId = id;
    }
    
    public String getName() {
        // This would need firstName/lastName if we want to keep it
        // For now, return staffId
        return staffId;
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
    }
}
