package model;

// GP class - for General Practice doctors
public class GP extends Clinician {
    
    private String surgeryName;
    
    public GP() {
        super();
    }
    
    public GP(String username, String password, String email,
              String staffId, String department, String hireDate,
              String qualification) {
        super(username, password, email, staffId, department, hireDate, qualification);
    }
    
    public GP(String clinicianId, String firstName, String lastName,
              String title, String speciality, String gmcNumber,
              String phone, String email, String workplaceId,
              String workplaceType, String employmentStatus,
              String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber,
              phone, email, workplaceId, workplaceType, employmentStatus, startDate);
        this.surgeryName = workplaceType != null ? workplaceType : "";
    }
    
    public String getSurgeryName() {
        return surgeryName;
    }
    
    public void setSurgeryName(String surgeryName) {
        this.surgeryName = surgeryName;
    }
}

