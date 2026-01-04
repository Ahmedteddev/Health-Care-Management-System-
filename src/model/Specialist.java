package model;

// Specialist class - for consultant doctors
public class Specialist extends Clinician {
    
    private String specialtyArea;
    
    public Specialist() {
        super();
    }
    
    public Specialist(String username, String password, String email,
                     String staffId, String department, String hireDate,
                     String qualification) {
        super(username, password, email, staffId, department, hireDate, qualification);
    }
    
    public Specialist(String clinicianId, String firstName, String lastName,
                      String title, String speciality, String gmcNumber,
                      String phone, String email, String workplaceId,
                      String workplaceType, String employmentStatus,
                      String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber,
              phone, email, workplaceId, workplaceType, employmentStatus, startDate);
        this.specialtyArea = speciality != null ? speciality : "";
    }
    
    public String getSpecialtyArea() {
        return specialtyArea;
    }
    
    public void setSpecialtyArea(String specialtyArea) {
        this.specialtyArea = specialtyArea;
    }
}

