package model;

public class Referral {

    private String id;                    // referral_id
    private String patientId;             // patient_id
    private String referringClinicianId;  // referring_clinician
    private String referredToClinicianId; // referred_to
    private String referringFacilityId;   // referring_facility
    private String referredToFacilityId;  // referred_to_facility
    private String referralDate;          // referral_date
    private String urgencyLevel;          // urgency_level
    private String referralReason;        // referral_reason
    private String clinicalSummary;       // clinical_summary
    private String requestedService;      // requested_service
    private String status;                // status
    private String appointmentId;         // appointment_id
    private String notes;                 // notes
    private String createdDate;           // created_date
    private String lastUpdated;           // last_updated

    public Referral() {}

    public Referral(String id, String patientId, String referringClinicianId,
                    String referredToClinicianId, String referringFacilityId,
                    String referredToFacilityId, String referralDate,
                    String urgencyLevel, String referralReason,
                    String clinicalSummary, String requestedService,
                    String status, String appointmentId, String notes,
                    String createdDate, String lastUpdated) {

        this.id = id;
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedService = requestedService;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }

    // ============================================================
    // REQUIRED FIELDS (as per specification)
    // ============================================================
    public String getReferralId() {
        return id;
    }
    
    public void setReferralId(String referralId) {
        this.id = referralId;
    }
    
    public String getSourceDoctorId() {
        return referringClinicianId;
    }
    
    public void setSourceDoctorId(String sourceDoctorId) {
        this.referringClinicianId = sourceDoctorId;
    }
    
    public String getTargetSpecialistId() {
        return referredToClinicianId;
    }
    
    public void setTargetSpecialistId(String targetSpecialistId) {
        this.referredToClinicianId = targetSpecialistId;
    }
    
    public String getUrgency() {
        return urgencyLevel;
    }
    
    public void setUrgency(String urgency) {
        this.urgencyLevel = urgency;
    }
    
    // ============================================================
    // EXISTING GETTERS AND SETTERS
    // ============================================================
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getReferringClinicianId() { return referringClinicianId; }
    public void setReferringClinicianId(String referringClinicianId) { this.referringClinicianId = referringClinicianId; }

    public String getReferredToClinicianId() { return referredToClinicianId; }
    public void setReferredToClinicianId(String referredToClinicianId) { this.referredToClinicianId = referredToClinicianId; }

    public String getReferringFacilityId() { return referringFacilityId; }
    public void setReferringFacilityId(String referringFacilityId) { this.referringFacilityId = referringFacilityId; }

    public String getReferredToFacilityId() { return referredToFacilityId; }
    public void setReferredToFacilityId(String referredToFacilityId) { this.referredToFacilityId = referredToFacilityId; }

    public String getReferralDate() { return referralDate; }
    public void setReferralDate(String referralDate) { this.referralDate = referralDate; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getReferralReason() { return referralReason; }
    public void setReferralReason(String referralReason) { this.referralReason = referralReason; }

    public String getClinicalSummary() { return clinicalSummary; }
    public void setClinicalSummary(String clinicalSummary) { this.clinicalSummary = clinicalSummary; }

    public String getRequestedService() { return requestedService; }
    public void setRequestedService(String requestedService) { this.requestedService = requestedService; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAppointmentId() { return appointmentId; }
    public void setAppointmentId(String appointmentId) { this.appointmentId = appointmentId; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }

    public String getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(String lastUpdated) { this.lastUpdated = lastUpdated; }
}
