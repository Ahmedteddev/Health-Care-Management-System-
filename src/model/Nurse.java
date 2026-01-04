package model;

// Nurse class - for nursing staff
public class Nurse extends Clinician {
    
    private String nursingGrade;
    
    public Nurse() {
        super();
    }
    
    public Nurse(String username, String password, String email,
                 String staffId, String department, String hireDate,
                 String qualification) {
        super(username, password, email, staffId, department, hireDate, qualification);
    }
    
    public Nurse(String clinicianId, String firstName, String lastName,
                 String title, String speciality, String gmcNumber,
                 String phone, String email, String workplaceId,
                 String workplaceType, String employmentStatus,
                 String startDate) {
        super(clinicianId, firstName, lastName, title, speciality, gmcNumber,
              phone, email, workplaceId, workplaceType, employmentStatus, startDate);
        this.nursingGrade = title != null ? title : "";
    }
    
    public String getNursingGrade() {
        return nursingGrade;
    }
    
    public void setNursingGrade(String nursingGrade) {
        this.nursingGrade = nursingGrade;
    }
}

