package controller;

import model.*;
import view.PatientDashboardFrame;

public class PatientDashboardController {
    
    private final String loggedInPatientId;
    private final PatientDashboardFrame view;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    
    public PatientDashboardController(PatientDashboardFrame view,
                                     AppointmentRepository appointmentRepository,
                                     PrescriptionRepository prescriptionRepository,
                                     PatientRepository patientRepository,
                                     ClinicianRepository clinicianRepository,
                                     FacilityRepository facilityRepository,
                                     String loggedInPatientId) {
        this.view = view;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.loggedInPatientId = loggedInPatientId;
        
        // Call refreshData() in constructor
        refreshData();
    }
    
    /**
     * Refresh data method that loads appointments and prescriptions for the logged-in patient.
     */
    public void refreshData() {
        
        // Set patient name in welcome label
        Patient patient = patientRepository.findById(loggedInPatientId);
        if (patient != null) {
            view.setPatientName(patient.getFullName());
        } else {
            view.setPatientName("Patient " + loggedInPatientId);
        }
        
        // Clear current tables
        view.clearAppointments();
        view.clearPrescriptions();
        
        // Load appointments
        loadAppointments();
        
        // Load prescriptions
        loadPrescriptions();
    }
    
    private void loadAppointments() {
        // Pull from AppointmentRepository: Find all rows where Patient ID matches loggedInPatientId
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (loggedInPatientId.equals(appointment.getPatientId())) {
                // Get clinician name
                Clinician clinician = clinicianRepository.findById(appointment.getClinicianId());
                String clinicianName = clinician != null ? clinician.getFullName() : appointment.getClinicianId();
                
                // Get facility name (include Facility column from CSV)
                String facilityName = appointment.getFacilityId();
                if (facilityRepository != null) {
                    Facility facility = facilityRepository.findById(appointment.getFacilityId());
                    if (facility != null) {
                        facilityName = facility.getFacilityName();
                    }
                }
                
                // Add row to Appointment table: Date, Time, Clinician, Facility, Status
                view.addAppointmentRow(
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    clinicianName,
                    facilityName,
                    appointment.getStatus()
                );
            }
        }
    }
    
    private void loadPrescriptions() {
        // Pull from PrescriptionRepository: Find all rows where Patient ID matches loggedInPatientId
        for (Prescription prescription : prescriptionRepository.getAll()) {
            if (loggedInPatientId.equals(prescription.getPatientId())) {
                // Add row to Prescription table: Drug Name, Dosage, Instructions, Date Prescribed
                view.addPrescriptionRow(
                    prescription.getMedication(),
                    prescription.getDosage(),
                    prescription.getInstructions(),
                    prescription.getPrescriptionDate()
                );
            }
        }
    }
}

