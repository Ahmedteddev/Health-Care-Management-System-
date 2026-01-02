package controller;

import model.*;
import repository.PatientRepository;
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
        
        refreshData();
    }
    
    public void refreshData() {
        Patient patient = patientRepository.findById(loggedInPatientId);
        if (patient != null) {
            view.setPatientName(patient.getFullName());
        } else {
            view.setPatientName("Patient " + loggedInPatientId);
        }
        
        view.clearAppointments();
        view.clearPrescriptions();
        loadAppointments();
        loadPrescriptions();
    }
    
    private void loadAppointments() {
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (loggedInPatientId.equals(appointment.getPatientId())) {
                Clinician clinician = clinicianRepository.findById(appointment.getClinicianId());
                String clinicianName = clinician != null ? clinician.getFullName() : appointment.getClinicianId();
                
                String facilityName = appointment.getFacilityId();
                if (facilityRepository != null) {
                    Facility facility = facilityRepository.findById(appointment.getFacilityId());
                    if (facility != null) {
                        facilityName = facility.getFacilityName();
                    }
                }
                
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
        for (Prescription prescription : prescriptionRepository.getAll()) {
            if (loggedInPatientId.equals(prescription.getPatientId())) {
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

