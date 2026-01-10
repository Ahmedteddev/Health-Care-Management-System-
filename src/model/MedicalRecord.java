package model;

// Medical record - stores allergies, blood type
public class MedicalRecord {
    
    private String patientId;
    private String allergies;
    private String bloodType;

    public MedicalRecord() {
        this("", "", "","");
    }
    
    public MedicalRecord(String patientId, String allergies, String bloodType, String history) {
        this.patientId = patientId;
        this.allergies = allergies != null ? allergies : "";
        this.bloodType = bloodType != null ? bloodType : "";
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getAllergies() {
        return allergies;
    }
    
    public void setAllergies(String allergies) {
        this.allergies = allergies != null ? allergies : "";
    }
    
    public String getBloodType() {
        return bloodType;
    }
    
    public void setBloodType(String bloodType) {
        this.bloodType = bloodType != null ? bloodType : "";
    }
}

