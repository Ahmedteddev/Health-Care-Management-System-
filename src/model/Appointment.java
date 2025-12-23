package model;

public class Appointment {

    private String id;                  // appointment_id
    private String patientId;           // patient_id
    private String clinicianId;         // clinician_id
    private String facilityId;          // facility_id
    private String appointmentDate;     // appointment_date  (e.g. 20/09/2025)
    private String appointmentTime;     // appointment_time  (e.g. 09:00)
    private String durationMinutes;     // duration_minutes  (e.g. 15)
    private String appointmentType;     // appointment_type  (e.g. Routine Consultation)
    private String status;              // status            (Scheduled, Cancelled, etc.)
    private String reasonForVisit;      // reason_for_visit
    private String notes;               // notes
    private String createdDate;         // created_date
    private String lastModified;        // last_modified

    public Appointment() { }

    public Appointment(String id,
                       String patientId,
                       String clinicianId,
                       String facilityId,
                       String appointmentDate,
                       String appointmentTime,
                       String durationMinutes,
                       String appointmentType,
                       String status,
                       String reasonForVisit,
                       String notes,
                       String createdDate,
                       String lastModified) {

        this.id = id;
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

    public String getId()                { return id; }
    public String getPatientId()         { return patientId; }
    public String getClinicianId()       { return clinicianId; }
    public String getFacilityId()        { return facilityId; }
    public String getAppointmentDate()   { return appointmentDate; }
    public String getAppointmentTime()   { return appointmentTime; }
    public String getDurationMinutes()   { return durationMinutes; }
    public String getAppointmentType()   { return appointmentType; }
    public String getStatus()            { return status; }
    public String getReasonForVisit()    { return reasonForVisit; }
    public String getNotes()             { return notes; }
    public String getCreatedDate()       { return createdDate; }
    public String getLastModified()      { return lastModified; }

    public void setId(String id)                          { this.id = id; }
    public void setPatientId(String patientId)            { this.patientId = patientId; }
    public void setClinicianId(String clinicianId)        { this.clinicianId = clinicianId; }
    public void setFacilityId(String facilityId)          { this.facilityId = facilityId; }
    public void setAppointmentDate(String appointmentDate){ this.appointmentDate = appointmentDate; }
    public void setAppointmentTime(String appointmentTime){ this.appointmentTime = appointmentTime; }
    public void setDurationMinutes(String durationMinutes){ this.durationMinutes = durationMinutes; }
    public void setAppointmentType(String appointmentType){ this.appointmentType = appointmentType; }
    public void setStatus(String status)                  { this.status = status; }
    public void setReasonForVisit(String reasonForVisit)  { this.reasonForVisit = reasonForVisit; }
    public void setNotes(String notes)                    { this.notes = notes; }
    public void setCreatedDate(String createdDate)        { this.createdDate = createdDate; }
    public void setLastModified(String lastModified)      { this.lastModified = lastModified; }
}
