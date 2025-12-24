package model;

/**
 * Appointment class representing patient appointments.
 * Matches CSV structure: appointment_id, patient_id, clinician_id, facility_id,
 * appointment_date, appointment_time, duration_minutes, appointment_type,
 * status, reason_for_visit, notes, created_date, last_modified
 */
public class Appointment {
    
    private String appointmentId;
    private String patientId;
    private String clinicianId;
    private String facilityId;
    private String appointmentDate;
    private String appointmentTime;
    private String durationMinutes;
    private String appointmentType;
    private String status;
    private String reasonForVisit;
    private String notes;
    private String createdDate;
    private String lastModified;
    
    public Appointment() {
    }
    
    /**
     * Full-parameter constructor for CSV loading (all 13 fields).
     */
    public Appointment(String appointmentId, String patientId, String clinicianId,
                       String facilityId, String appointmentDate,
                       String appointmentTime, String durationMinutes,
                       String appointmentType, String status,
                       String reasonForVisit, String notes,
                       String createdDate, String lastModified) {
        
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.facilityId = facilityId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.durationMinutes = durationMinutes;
        this.appointmentType = appointmentType;
        this.status = status;
        this.reasonForVisit = reasonForVisit;
        this.notes = notes;
        this.createdDate = createdDate;
        this.lastModified = lastModified;
    }
    
    // Backward compatibility methods
    public String getId() {
        return appointmentId;
    }
    
    public void setId(String id) {
        this.appointmentId = id;
    }
    
    // ============================================================
    // REQUIRED FIELDS (as per specification)
    // ============================================================
    public String getAppointmentId() {
        return appointmentId;
    }
    
    public void setAppointmentId(String appointmentId) {
        this.appointmentId = appointmentId;
    }
    
    public String getDate() {
        return appointmentDate;
    }
    
    public void setDate(String date) {
        this.appointmentDate = date;
    }
    
    public String getReason() {
        return reasonForVisit;
    }
    
    public void setReason(String reason) {
        this.reasonForVisit = reason;
    }
    
    // ============================================================
    // ALL CSV FIELDS - GETTERS AND SETTERS
    // ============================================================
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getClinicianId() {
        return clinicianId;
    }
    
    public void setClinicianId(String clinicianId) {
        this.clinicianId = clinicianId;
    }
    
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getAppointmentDate() {
        return appointmentDate;
    }
    
    public void setAppointmentDate(String appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    public String getAppointmentTime() {
        return appointmentTime;
    }
    
    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }
    
    public String getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(String durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getAppointmentType() {
        return appointmentType;
    }
    
    public void setAppointmentType(String appointmentType) {
        this.appointmentType = appointmentType;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReasonForVisit() {
        return reasonForVisit;
    }
    
    public void setReasonForVisit(String reasonForVisit) {
        this.reasonForVisit = reasonForVisit;
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
    
    public String getLastModified() {
        return lastModified;
    }
    
    public void setLastModified(String lastModified) {
        this.lastModified = lastModified;
    }
}
