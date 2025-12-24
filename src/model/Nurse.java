package model;

/**
 * Nurse class extending Clinician.
 * Represents nursing staff.
 */
public class Nurse extends Clinician {
    
    public Nurse() {
        super();
    }
    
    public Nurse(String username, String password, String email,
                 String staffId, String department, String hireDate,
                 String qualification) {
        super(username, password, email, staffId, department, hireDate, qualification);
    }
    
    // Backward compatibility constructor
    public Nurse(String clinicianId, String firstName, String lastName,
                 String title, String speciality, String gmcNumber,
                 String phone, String email, String workplaceId,
                 String workplaceType, String employmentStatus,
                 String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber,
              phone, email, workplaceId, workplaceType, employmentStatus, startDate);
    }
}

