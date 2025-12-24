package model;

/**
 * Specialist class extending Clinician.
 * Represents specialist doctors (consultants).
 */
public class Specialist extends Clinician {
    
    public Specialist() {
        super();
    }
    
    public Specialist(String username, String password, String email,
                     String staffId, String department, String hireDate,
                     String qualification) {
        super(username, password, email, staffId, department, hireDate, qualification);
    }
    
    // Backward compatibility constructor
    public Specialist(String clinicianId, String firstName, String lastName,
                      String title, String speciality, String gmcNumber,
                      String phone, String email, String workplaceId,
                      String workplaceType, String employmentStatus,
                      String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber,
              phone, email, workplaceId, workplaceType, employmentStatus, startDate);
    }
}

