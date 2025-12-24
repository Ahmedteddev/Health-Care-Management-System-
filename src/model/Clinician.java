package model;

/**
 * Clinician class extending Staff.
 * Matches CSV structure: clinician_id, first_name, last_name, title, speciality,
 * gmc_number, phone_number, email, workplace_id, workplace_type, employment_status, start_date
 */
public class Clinician extends Staff {
    
    private String clinicianId;
    private String title;
    private String speciality;
    private String gmcNumber;
    private String workplaceId;
    private String workplaceType;
    private String qualification;
    
    public Clinician() {
        super();
    }
    
    /**
     * Full-parameter constructor for CSV loading (all 12 fields).
     * Note: email is inherited from User class.
     */
    public Clinician(String clinicianId, String firstName, String lastName,
                     String title, String speciality, String gmcNumber,
                     String phoneNumber, String email, String workplaceId,
                     String workplaceType, String employmentStatus,
                     String startDate) {
        // Call Staff constructor with appropriate parameters
        // Note: Staff needs role, department, facilityId - we'll derive these
        super(clinicianId, firstName, lastName,
              determineRole(title, speciality),  // role
              determineDepartment(speciality, workplaceType),  // department
              workplaceId,  // facilityId (using workplaceId)
              phoneNumber, email, employmentStatus, startDate,
              null,  // lineManager (not in CSV)
              null); // accessLevel (not in CSV)
        
        this.clinicianId = clinicianId;
        this.title = title;
        this.speciality = speciality;
        this.gmcNumber = gmcNumber;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        // Set qualification based on title/speciality
        this.qualification = determineQualification(title, speciality, gmcNumber);
    }
    
    /**
     * Constructor for User-based initialization.
     */
    public Clinician(String username, String password, String email,
                     String staffId, String department, String hireDate,
                     String qualification) {
        super(username, password, email, staffId, department, hireDate);
        this.clinicianId = staffId;  // clinicianId maps to staffId
        this.qualification = qualification;
    }
    
    private static String determineRole(String title, String speciality) {
        if (title != null && title.toLowerCase().contains("nurse")) {
            return "Nurse";
        } else if (title != null && title.toLowerCase().contains("consultant")) {
            return "Consultant";
        } else if (title != null && title.equalsIgnoreCase("GP")) {
            return "GP";
        }
        return "Clinician";
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
        return clinicianId != null ? clinicianId : getStaffId();
    }
    
    public void setId(String id) {
        this.clinicianId = id;
        setStaffId(id);
    }
    
    public String getFullName() {
        String firstName = getFirstName();
        String lastName = getLastName();
        if (title != null && !title.isEmpty()) {
            return title + " " + firstName + " " + lastName;
        }
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        }
        return clinicianId;
    }
    
    // ============================================================
    // ALL CSV FIELDS - GETTERS AND SETTERS
    // ============================================================
    public String getClinicianId() {
        return clinicianId;
    }
    
    public void setClinicianId(String clinicianId) {
        this.clinicianId = clinicianId;
        // Also update staffId for consistency
        setStaffId(clinicianId);
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
    
    public String getWorkplaceId() {
        return workplaceId;
    }
    
    public void setWorkplaceId(String workplaceId) {
        this.workplaceId = workplaceId;
        // Also update facilityId for consistency
        setFacilityId(workplaceId);
    }
    
    public String getWorkplaceType() {
        return workplaceType;
    }
    
    public void setWorkplaceType(String workplaceType) {
        this.workplaceType = workplaceType;
    }
    
    public String getQualification() {
        return qualification;
    }
    
    public void setQualification(String qualification) {
        this.qualification = qualification;
    }
    
    // Additional getters for phoneNumber (inherited from Staff but may need override)
    @Override
    public String getPhoneNumber() {
        return super.getPhoneNumber();
    }
    
    @Override
    public void setPhoneNumber(String phoneNumber) {
        super.setPhoneNumber(phoneNumber);
    }
    
    // Note: employmentStatus and startDate are inherited from Staff
    // firstName, lastName, email are also inherited
}
