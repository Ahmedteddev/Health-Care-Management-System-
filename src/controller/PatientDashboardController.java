package controller;

import model.*;
import repository.PatientRepository;
import view.PatientDashboardFrame;
import view.PatientDashboardPanel;
import java.util.List;

public class PatientDashboardController {
    
    private final String loggedInPatientId;
    private final Object view; // Can be PatientDashboardFrame or PatientDashboardPanel
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    
    // Constructor for PatientDashboardFrame (backward compatibility)
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
    
    // Constructor for PatientDashboardPanel (for NavigationCard)
    public PatientDashboardController(PatientDashboardPanel view,
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
        String patientName = patient != null ? patient.getFullName() : "Patient " + loggedInPatientId;
        
        if (view instanceof PatientDashboardFrame) {
            ((PatientDashboardFrame) view).setPatientName(patientName);
            ((PatientDashboardFrame) view).clearAppointments();
            ((PatientDashboardFrame) view).clearPrescriptions();
        } else if (view instanceof PatientDashboardPanel) {
            ((PatientDashboardPanel) view).setPatientName(patientName);
            ((PatientDashboardPanel) view).clearAppointments();
            ((PatientDashboardPanel) view).clearPrescriptions();
        }
        
        loadAppointments();
        loadPrescriptions();
    }
    
    private void loadAppointments() {
        // Use getByPatientId to get appointments for the exact patient ID (e.g., "P001")
        System.out.println("Loading appointments for Patient ID: " + loggedInPatientId);
        List<Appointment> appointments = appointmentRepository.getByPatientId(loggedInPatientId);
        
        for (Appointment appointment : appointments) {
            Clinician clinician = clinicianRepository.findById(appointment.getClinicianId());
            String clinicianName = clinician != null ? clinician.getFullName() : appointment.getClinicianId();
            
            String facilityName = appointment.getFacilityId();
            if (facilityRepository != null) {
                Facility facility = facilityRepository.findById(appointment.getFacilityId());
                if (facility != null) {
                    facilityName = facility.getFacilityName();
                }
            }
            
            if (view instanceof PatientDashboardFrame) {
                ((PatientDashboardFrame) view).addAppointmentRow(
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    clinicianName,
                    facilityName,
                    appointment.getStatus()
                );
            } else if (view instanceof PatientDashboardPanel) {
                ((PatientDashboardPanel) view).addAppointmentRow(
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
        // Use getByPatientId to get prescriptions for the exact patient ID (e.g., "P001")
        System.out.println("Loading prescriptions for Patient ID: " + loggedInPatientId);
        List<Prescription> prescriptions = prescriptionRepository.getByPatientId(loggedInPatientId);
        
        for (Prescription prescription : prescriptions) {
            if (view instanceof PatientDashboardFrame) {
                ((PatientDashboardFrame) view).addPrescriptionRow(
                    prescription.getMedication(),
                    prescription.getDosage(),
                    prescription.getInstructions(),
                    prescription.getPrescriptionDate()
                );
            } else if (view instanceof PatientDashboardPanel) {
                ((PatientDashboardPanel) view).addPrescriptionRow(
                    prescription.getMedication(),
                    prescription.getDosage(),
                    prescription.getInstructions(),
                    prescription.getPrescriptionDate()
                );
            }
        }
    }
}

