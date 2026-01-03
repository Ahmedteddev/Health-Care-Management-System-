package controller;

import model.*;
import view.PatientDashboardPanel;
import java.util.List;

public class PatientDashboardController {
    
    // This stores the ID of the patient who is logged in (like "P001")
    private final String loggedInPatientId;
    // This is the panel that shows the patient's dashboard on the screen
    private final PatientDashboardPanel dashboardPanel;
    // These repositories let us get data about appointments, prescriptions, etc.
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    
    // Constructor - this sets everything up when the controller is created
    // It needs the panel to display on, all the repositories to get data from, and the patient's ID
    public PatientDashboardController(PatientDashboardPanel dashboardPanel,
                                     AppointmentRepository appointmentRepository,
                                     PrescriptionRepository prescriptionRepository,
                                     PatientRepository patientRepository,
                                     ClinicianRepository clinicianRepository,
                                     FacilityRepository facilityRepository,
                                     String loggedInPatientId) {
        this.dashboardPanel = dashboardPanel;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.loggedInPatientId = loggedInPatientId;
        
        // Load all the data and show it on the screen
        refreshData();
    }
    
    // This method loads all the patient's information and displays it
    public void refreshData() {
        // First, get the patient's information using their ID
        Patient currentPatient = patientRepository.findById(loggedInPatientId);
        // Get their full name, or if we can't find them, just show "Patient [ID]"
        String patientFullName = currentPatient != null ? currentPatient.getFullName() : "Patient " + loggedInPatientId;
        
        // Update the welcome message with their name
        dashboardPanel.setPatientName(patientFullName);
        // Clear the tables so we can add fresh data
        dashboardPanel.clearAppointments();
        dashboardPanel.clearPrescriptions();
        
        // Now load and display their appointments and prescriptions
        loadAppointments();
        loadPrescriptions();
    }
    
    // This method gets all the appointments for this patient and shows them in the table
    private void loadAppointments() {
        // Get all appointments that belong to this patient using their ID (like "P001")
        List<Appointment> patientAppointments = appointmentRepository.getByPatientId(loggedInPatientId);
        
        // Go through each appointment and add it to the table
        for (Appointment currentAppointment : patientAppointments) {
            // Try to find the clinician who is doing this appointment
            Clinician appointmentClinician = clinicianRepository.findById(currentAppointment.getClinicianId());
            // Get the clinician's name, or if we can't find them, just show their ID
            String clinicianFullName = appointmentClinician != null ? appointmentClinician.getFullName() : currentAppointment.getClinicianId();
            
            // Get the facility name
            String facilityName = currentAppointment.getFacilityId();
            // Try to look up the facility to get its proper name
            if (facilityRepository != null) {
                Facility appointmentFacility = facilityRepository.findById(currentAppointment.getFacilityId());
                if (appointmentFacility != null) {
                    facilityName = appointmentFacility.getFacilityName();
                }
            }
            
            // Add this appointment as a row in the appointments table
            dashboardPanel.addAppointmentRow(
                currentAppointment.getAppointmentDate(),
                currentAppointment.getAppointmentTime(),
                clinicianFullName,
                facilityName,
                currentAppointment.getStatus()
            );
        }
    }
    
    // This method gets all the prescriptions for this patient and shows them in the table
    private void loadPrescriptions() {
        // Get all prescriptions that belong to this patient using their ID (like "P001")
        List<Prescription> patientPrescriptions = prescriptionRepository.getByPatientId(loggedInPatientId);
        
        // Go through each prescription and add it to the table
        for (Prescription currentPrescription : patientPrescriptions) {
            // Add this prescription as a row in the prescriptions table
            dashboardPanel.addPrescriptionRow(
                currentPrescription.getMedication(),
                currentPrescription.getDosage(),
                currentPrescription.getInstructions(),
                currentPrescription.getPrescriptionDate()
            );
        }
    }
}

