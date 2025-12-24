package model;

/**
 * Clinician class extending Staff.
 * Represents clinical staff members with medical qualifications.
 */
public class Clinician extends Staff {
    
    private String qualification;
    private String firstName;
    private String lastName;
    private String title;
    private String speciality;
    private String gmcNumber;
    private String phone;
    private String workplaceId;
    private String workplaceType;
    private String employmentStatus;
    
    public Clinician() {
        super();
    }
    
    public Clinician(String username, String password, String email,
                     String staffId, String department, String hireDate,
                     String qualification) {
        super(username, password, email, staffId, department, hireDate);
        this.qualification = qualification;
    }
    
    // Backward compatibility constructor (for CSV loading)
    public Clinician(String clinicianId, String firstName, String lastName,
                     String title, String speciality, String gmcNumber,
                     String phone, String email, String workplaceId,
                     String workplaceType, String employmentStatus,
                     String startDate) {
        // Generate username from email or clinicianId
        super(generateUsername(email, clinicianId), "default", email,
              clinicianId, determineDepartment(speciality, workplaceType), startDate);
        this.firstName = firstName;
        this.lastName = lastName;
        this.title = title;
        this.speciality = speciality;
        this.gmcNumber = gmcNumber;
        this.phone = phone;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        // Set qualification based on title/speciality
        this.qualification = determineQualification(title, speciality, gmcNumber);
    }
    
    private static String generateUsername(String email, String clinicianId) {
        if (email != null && !email.isEmpty()) {
            return email.split("@")[0];
        }
        return clinicianId;
    }
    
    private static String determineDepartment(String speciality, String workplaceType) {
        if (speciality != null && !speciality.isEmpty()) {
            return speciality;
        }
        return workplaceType != null ? workplaceType : "Clinical";
    }
    
    private static String determineQualification(String title, String speciality, String gmcNumber) {
        if (gmcNumber != null && !gmcNumber.isEmpty() && !gmcNumber.startsWith("N")) {
            return "Medical Degree (GMC: " + gmcNumber + ")";
        } else if (title != null && title.toLowerCase().contains("nurse")) {
            return "Nursing Qualification";
        }
        return "Clinical Qualification";
    }
    
    // Backward compatibility methods
    public String getId() {
        return getStaffId();
    }
    
    public void setId(String id) {
        setStaffId(id);
    }
    
    public String getFullName() {
        if (title != null && !title.isEmpty()) {
            return title + " " + firstName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getSpeciality() {
        return speciality;
    }
    
    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }
    
    public String getGmcNumber() {
        return gmcNumber;
    }
    
    public void setGmcNumber(String gmcNumber) {
        this.gmcNumber = gmcNumber;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getWorkplaceId() {
        return workplaceId;
    }
    
    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
    }
    
    public String getWorkplaceType() {
        return workplaceType;
    }
    
    public void setWorkplaceType(String workplaceType) {
        this.workplaceType = workplaceType;
    }
    
    public String getEmploymentStatus() {
        return employmentStatus;
    }
    
    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
}
