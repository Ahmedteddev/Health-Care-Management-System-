package model;

public class Prescription {

    private String id;               // prescription_id (RX001)
    private String patientId;        // patient_id (P001)
    private String clinicianId;      // clinician_id (C001)
    private String appointmentId;    // appointment_id (A001)
    private String prescriptionDate; // prescription_date (yyyy-MM-dd)

    private String medication;       // medication_name
    private String dosage;           // dosage (e.g. 20mg)
    private String frequency;        // frequency (e.g. Once daily)
    private String durationDays;     // duration_days (e.g. 28)
    private String quantity;         // quantity (e.g. 28 tablets)

    private String instructions;     // instructions
    private String pharmacyName;     // pharmacy_name
    private String status;           // status (Issued / Collected / etc.)
    private String issueDate;        // issue_date (yyyy-MM-dd)
    private String collectionDate;   // collection_date (yyyy-MM-dd or "")

    public Prescription() { }

    public Prescription(String id,
                        String patientId,
                        String clinicianId,
                        String appointmentId,
                        String prescriptionDate,
                        String medication,
                        String dosage,
                        String frequency,
                        String durationDays,
                        String quantity,
                        String instructions,
                        String pharmacyName,
                        String status,
                        String issueDate,
                        String collectionDate) {

        this.id = id;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.appointmentId = appointmentId;
        this.prescriptionDate = prescriptionDate;
        this.medication = medication;
        this.dosage = dosage;
        this.frequency = frequency;
        this.durationDays = durationDays;
        this.quantity = quantity;
        this.instructions = instructions;
        this.pharmacyName = pharmacyName;
        this.status = status;
        this.issueDate = issueDate;
        this.collectionDate = collectionDate;
    }

    // ============================================================
    // REQUIRED FIELDS (as per specification)
    // ============================================================
    public String getPrescriptionId() {
        return id;
    }
    
    public void setPrescriptionId(String prescriptionId) {
        this.id = prescriptionId;
    }
    
    // ============================================================
    // EXISTING GETTERS AND SETTERS
    // ============================================================
    public String getId()               { return id; }
    public String getPatientId()        { return patientId; }
    public String getClinicianId()      { return clinicianId; }
    public String getAppointmentId()    { return appointmentId; }
    public String getPrescriptionDate() { return prescriptionDate; }
    public String getMedication()       { return medication; }
    public String getDosage()           { return dosage; }
    public String getFrequency()        { return frequency; }
    public String getDurationDays()     { return durationDays; }
    public String getQuantity()         { return quantity; }
    public String getInstructions()     { return instructions; }
    public String getPharmacyName()     { return pharmacyName; }
    public String getStatus()           { return status; }
    public String getIssueDate()        { return issueDate; }
    public String getCollectionDate()   { return collectionDate; }

    public void setId(String id)                             { this.id = id; }
    public void setPatientId(String patientId)               { this.patientId = patientId; }
    public void setClinicianId(String clinicianId)           { this.clinicianId = clinicianId; }
    public void setAppointmentId(String appointmentId)       { this.appointmentId = appointmentId; }
    public void setPrescriptionDate(String prescriptionDate) { this.prescriptionDate = prescriptionDate; }
    public void setMedication(String medication)             { this.medication = medication; }
    public void setDosage(String dosage)                     { this.dosage = dosage; }
    public void setFrequency(String frequency)               { this.frequency = frequency; }
    public void setDurationDays(String durationDays)         { this.durationDays = durationDays; }
    public void setQuantity(String quantity)                 { this.quantity = quantity; }
    public void setInstructions(String instructions)         { this.instructions = instructions; }
    public void setPharmacyName(String pharmacyName)         { this.pharmacyName = pharmacyName; }
    public void setStatus(String status)                     { this.status = status; }
    public void setIssueDate(String issueDate)               { this.issueDate = issueDate; }
    public void setCollectionDate(String collectionDate)     { this.collectionDate = collectionDate; }
}
