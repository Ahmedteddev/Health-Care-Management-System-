package controller;

import model.*;
import view.GPDashboard;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Controller for the GP Dashboard.
 * Handles interaction between the GPDashboard view and the repositories.
 * Manages appointment filtering and patient record display.
 */
public class GPController {
    
    private final GPDashboard view;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final Clinician clinician;
    
    /**
     * Constructor for GPController.
     * 
     * @param view The GPDashboard view
     * @param appointmentRepository The appointment repository
     * @param patientRepository The patient repository
     */
    public GPController(GPDashboard view, 
                       AppointmentRepository appointmentRepository,
                       PatientRepository patientRepository) {
        this.view = view;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinician = view.getClinician();
        
        setupEventHandlers();
    }
    
    /**
     * Sets up event handlers for the view components.
     */
    private void setupEventHandlers() {
        // Add selection listener to appointments table
        view.getAppointmentsTable().getSelectionModel()
            .addListSelectionListener(new AppointmentSelectionListener());
        
        // Add action listener to refresh button
        view.getRefreshButton().addActionListener(new RefreshButtonListener());
    }
    
    /**
     * Loads appointments for the logged-in clinician.
     * Filters appointments where clinician_id matches the clinician's ID.
     */
    public void loadAppointments() {
        // Clear existing appointments from the table
        view.clearAppointments();
        
        // Get all appointments from repository
        List<Appointment> allAppointments = appointmentRepository.getAll();
        
        // Filter appointments for this clinician
        String clinicianId = clinician.getClinicianId();
        if (clinicianId == null) {
            clinicianId = clinician.getId(); // Fallback to getId() if clinicianId is null
        }
        
        int appointmentCount = 0;
        for (Appointment appointment : allAppointments) {
            String appointmentClinicianId = appointment.getClinicianId();
            
            // Check if this appointment belongs to the logged-in clinician
            if (clinicianId.equals(appointmentClinicianId)) {
                // Get patient details
                Patient patient = patientRepository.findById(appointment.getPatientId());
                String patientName = "Unknown";
                if (patient != null) {
                    patientName = patient.getFullName();
                }
                
                // Add row to table: Time, Patient ID, Patient Name, Reason, Status
                view.addAppointmentRow(
                    appointment.getAppointmentTime(),  // Time
                    appointment.getPatientId(),        // Patient ID
                    patientName,                       // Patient Name
                    appointment.getReasonForVisit(),   // Reason
                    appointment.getStatus()            // Status
                );
                appointmentCount++;
            }
        }
        
        System.out.println("Loaded " + appointmentCount + " appointments for clinician " + clinicianId);
    }
    
    /**
     * Loads and displays patient medical record.
     * 
     * @param patientId The ID of the patient whose record to load
     */
    private void loadPatientRecord(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            view.updateMedicalRecord("No patient selected.");
            return;
        }
        
        // Find patient in repository
        Patient patient = patientRepository.findById(patientId);
        
        if (patient == null) {
            view.updateMedicalRecord("Patient not found: " + patientId);
            return;
        }
        
        // Build medical record text
        StringBuilder recordText = new StringBuilder();
        recordText.append("PATIENT MEDICAL RECORD\n");
        recordText.append("======================\n\n");
        
        // Patient Demographics
        recordText.append("PATIENT INFORMATION:\n");
        recordText.append("  Patient ID: ").append(patient.getPatientId()).append("\n");
        recordText.append("  Name: ").append(patient.getFullName()).append("\n");
        recordText.append("  Date of Birth: ").append(patient.getDateOfBirth()).append("\n");
        recordText.append("  Gender: ").append(patient.getGender()).append("\n");
        recordText.append("  NHS Number: ").append(patient.getNhsNumber()).append("\n");
        recordText.append("  Phone: ").append(patient.getPhoneNumber()).append("\n");
        recordText.append("  Email: ").append(patient.getEmail()).append("\n");
        recordText.append("  Address: ").append(patient.getAddress()).append("\n");
        recordText.append("  Postcode: ").append(patient.getPostcode()).append("\n");
        recordText.append("  Registration Date: ").append(patient.getRegistrationDate()).append("\n");
        recordText.append("  GP Surgery ID: ").append(patient.getGpSurgeryId()).append("\n\n");
        
        // Emergency Contact
        recordText.append("EMERGENCY CONTACT:\n");
        recordText.append("  Name: ").append(patient.getEmergencyContactName()).append("\n");
        recordText.append("  Phone: ").append(patient.getEmergencyContactPhone()).append("\n\n");
        
        // Appointment History for this patient
        recordText.append("APPOINTMENT HISTORY:\n");
        recordText.append("--------------------\n");
        
        List<Appointment> allAppointments = appointmentRepository.getAll();
        int appointmentCount = 0;
        
        for (Appointment appointment : allAppointments) {
            if (patientId.equals(appointment.getPatientId())) {
                appointmentCount++;
                recordText.append("\nAppointment #").append(appointmentCount).append(":\n");
                recordText.append("  Date: ").append(appointment.getAppointmentDate()).append("\n");
                recordText.append("  Time: ").append(appointment.getAppointmentTime()).append("\n");
                recordText.append("  Type: ").append(appointment.getAppointmentType()).append("\n");
                recordText.append("  Status: ").append(appointment.getStatus()).append("\n");
                recordText.append("  Reason: ").append(appointment.getReasonForVisit()).append("\n");
                
                if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
                    recordText.append("  Notes: ").append(appointment.getNotes()).append("\n");
                }
                
                // Check if this appointment is with the current clinician
                String appointmentClinicianId = appointment.getClinicianId();
                String currentClinicianId = clinician.getClinicianId();
                if (currentClinicianId == null) {
                    currentClinicianId = clinician.getId();
                }
                
                if (currentClinicianId.equals(appointmentClinicianId)) {
                    recordText.append("  [Your Appointment]\n");
                }
            }
        }
        
        if (appointmentCount == 0) {
            recordText.append("  No previous appointments found.\n");
        }
        
        // Update the medical record area
        view.updateMedicalRecord(recordText.toString());
    }
    
    /**
     * ListSelectionListener for the appointments table.
     * Updates the medical record area when a row is selected.
     */
    private class AppointmentSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // Get selected patient ID
                String patientId = view.getSelectedPatientId();
                
                if (patientId != null) {
                    // Load and display patient record
                    loadPatientRecord(patientId);
                } else {
                    view.updateMedicalRecord("Please select an appointment to view patient record.");
                }
            }
        }
    }
    
    /**
     * ActionListener for the refresh button.
     * Reloads appointments from the repository.
     */
    private class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Reload appointments
            loadAppointments();
            
            // Clear medical record area
            view.updateMedicalRecord("Data refreshed. Please select an appointment to view patient record.");
            
            System.out.println("Appointments refreshed for clinician: " + clinician.getFullName());
        }
    }
}

