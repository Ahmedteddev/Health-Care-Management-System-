package model;

// Medical record - stores allergies, blood type, and medical history for a patient
public class MedicalRecord {
    
    private String patientId;
    private String allergies;
    private String bloodType;
    private String history;
    
    public MedicalRecord() {
        this("", "", "", "");
    }
    
    public MedicalRecord(String patientId, String allergies, String bloodType, String history) {
        this.patientId = patientId;
        this.allergies = allergies != null ? allergies : "";
        this.bloodType = bloodType != null ? bloodType : "";
        this.history = history != null ? history : "";
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
    
    public String getHistory() {
        return history;
    }
    
    public void setHistory(String history) {
        this.history = history != null ? history : "";
    }
}

