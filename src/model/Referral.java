package model;

// Referral class - matches the referrals.csv file structure (all 16 fields)
public class Referral {
    
    private String referralId;
    private String patientId;
    private String referringClinicianId;
    private String referredToClinicianId;
    private String referringFacilityId;
    private String referredToFacilityId;
    private String referralDate;
    private String urgencyLevel;
    private String referralReason;
    private String clinicalSummary;
    private String requestedInvestigations;
    private String status;
    private String appointmentId;
    private String notes;
    private String createdDate;
    private String lastUpdated;
    
    public Referral() {
    }
    
    // Constructor for loading from CSV - all 16 fields
    public Referral(String referralId, String patientId, String referringClinicianId,
                    String referredToClinicianId, String referringFacilityId,
                    String referredToFacilityId, String referralDate,
                    String urgencyLevel, String referralReason,
                    String clinicalSummary, String requestedInvestigations,
                    String status, String appointmentId, String notes,
                    String createdDate, String lastUpdated) {
        
        this.referralId = referralId;
        this.patientId = patientId;
        this.referringClinicianId = referringClinicianId;
        this.referredToClinicianId = referredToClinicianId;
        this.referringFacilityId = referringFacilityId;
        this.referredToFacilityId = referredToFacilityId;
        this.referralDate = referralDate;
        this.urgencyLevel = urgencyLevel;
        this.referralReason = referralReason;
        this.clinicalSummary = clinicalSummary;
        this.requestedInvestigations = requestedInvestigations;
        this.status = status;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastUpdated = lastUpdated;
    }
    
    public String getId() {
        return referralId;
    }
    
    public void setId(String id) {
        this.referralId = id;
    }
    
    public String getReferralId() {
        return referralId;
    }
    
    public void setReferralId(String referralId) {
        this.referralId = referralId;
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
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getReferringClinicianId() {
        return referringClinicianId;
    }
    
    public void setReferringClinicianId(String referringClinicianId) {
        this.referringClinicianId = referringClinicianId;
    }
    
    public String getReferredToClinicianId() {
        return referredToClinicianId;
    }
    
    public void setReferredToClinicianId(String referredToClinicianId) {
        this.referredToClinicianId = referredToClinicianId;
    }
    
    public String getReferringFacilityId() {
        return referringFacilityId;
    }
    
    public void setReferringFacilityId(String referringFacilityId) {
        this.referringFacilityId = referringFacilityId;
    }
    
    public String getReferredToFacilityId() {
        return referredToFacilityId;
    }
    
    public void setReferredToFacilityId(String referredToFacilityId) {
        this.referredToFacilityId = referredToFacilityId;
    }
    
    public String getReferralDate() {
        return referralDate;
    }
    
    public void setReferralDate(String referralDate) {
        this.referralDate = referralDate;
    }
    
    public String getUrgencyLevel() {
        return urgencyLevel;
    }
    
    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }
    
    public String getReferralReason() {
        return referralReason;
    }
    
    public void setReferralReason(String referralReason) {
        this.referralReason = referralReason;
    }
    
    public String getClinicalSummary() {
        return clinicalSummary;
    }
    
    public void setClinicalSummary(String clinicalSummary) {
        this.clinicalSummary = clinicalSummary;
    }
    
    public String getRequestedInvestigations() {
        return requestedInvestigations;
    }
    
    public void setRequestedInvestigations(String requestedInvestigations) {
        this.requestedInvestigations = requestedInvestigations;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getNotes() {
        return notes;
    }
    
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    public String getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
    
    public String getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(String lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
