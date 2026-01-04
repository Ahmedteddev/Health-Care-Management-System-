package model;

// Staff class - matches the staff.csv file structure
public class Staff extends User {
    
    private String staffId;
    private String firstName;
    private String lastName;
    private String role;
    private String department;
    private String facilityId;
    private String phoneNumber;
    private String employmentStatus;
    private String startDate;
    private String lineManager;
    private String accessLevel;
    
    public Staff() {
        super();
    }
    
    // Constructor for loading from CSV - takes all 12 fields
    public Staff(String staffId, String firstName, String lastName,
                 String role, String department, String facilityId,
                 String phoneNumber, String email, String employmentStatus,
                 String startDate, String lineManager, String accessLevel) {
        super(generateUsername(email, staffId), "default", email);
        this.staffId = staffId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.department = department;
        this.facilityId = facilityId;
        this.phoneNumber = phoneNumber;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
        this.lineManager = lineManager;
        this.accessLevel = accessLevel;
    }
    
    public Staff(String username, String password, String email,
                 String staffId, String department, String hireDate) {
        super(username, password, email);
        this.staffId = staffId;
        this.department = department;
        this.startDate = hireDate;
    }
    
    private static String generateUsername(String email, String staffId) {
        if (email != null && !email.isEmpty()) {
            return email.split("@")[0];
        }
        return staffId;
    }
    
    public String getId() {
        return staffId;
    }
    
    public void setId(String id) {
        this.staffId = id;
    }
    
    public String getName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return staffId;
    }
    
    public String getHireDate() {
        return startDate;
    }
    
    public void setHireDate(String hireDate) {
        this.startDate = hireDate;
    }
    
    public String getStaffId() {
        return staffId;
    }
    
    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getDepartment() {
        return department;
    }
    
    public void setDepartment(String department) {
        this.department = department;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    
    public String getStartDate() {
        return startDate;
    }
    
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
    
    public String getLineManager() {
        return lineManager;
    }
    
    public void setLineManager(String lineManager) {
        this.lineManager = lineManager;
    }
    
    public String getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}
