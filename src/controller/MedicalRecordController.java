package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MedicalRecordController {
    
    private final MedicalRecordPanel view;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ClinicianRepository clinicianRepository;
    private final Clinician currentClinician;
    
    private Patient currentPatient = null;
    
    public MedicalRecordController(MedicalRecordPanel view,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository,
                                  PrescriptionRepository prescriptionRepository,
                                  ClinicianRepository clinicianRepository,
                                  Clinician currentClinician) {
        this.view = view;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.clinicianRepository = clinicianRepository;
        this.currentClinician = currentClinician;
        
        bind();
    }
    
    // Bind all action listeners to the view components
    public void bind() {
        view.getSearchButton().addActionListener(new SearchButtonListener());
        view.getIssuePrescriptionButton().addActionListener(new IssuePrescriptionButtonListener());
    }
    
    // Handle search button click
    private void handleSearch() {
        String patientId = view.getPatientId();
        String patientName = view.getPatientName();
        String nhsNumber = view.getNhsNumber();
        
        Patient foundPatient = null;
        
        // Search by Patient ID first
        if (!patientId.isEmpty()) {
            foundPatient = patientRepository.findById(patientId);
        }
        // If not found, search by name
        else if (!patientName.isEmpty()) {
            for (Patient p : patientRepository.getAll()) {
                if (p.getFullName().toLowerCase().contains(patientName.toLowerCase())) {
                    foundPatient = p;
                    break;
                }
            }
        }
        // If not found, search by NHS number
        else if (!nhsNumber.isEmpty()) {
            for (Patient p : patientRepository.getAll()) {
                if (nhsNumber.equals(p.getNhsNumber())) {
                    foundPatient = p;
                    break;
                }
            }
        }
        
        if (foundPatient == null) {
            JOptionPane.showMessageDialog(view, "Patient not found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
            view.clearSummary();
            view.clearEncounters();
            view.clearMedications();
            view.setIssuePrescriptionEnabled(false);
            view.setCurrentPatientId(null);
            return;
        }
        
        // Update current patient
        currentPatient = foundPatient;
        view.setCurrentPatientId(foundPatient.getPatientId());
        
        // Update summary panel
        view.updateSummary(
            foundPatient.getFullName(),
            foundPatient.getDateOfBirth(),
            foundPatient.getGender(),
            "N/A" // Blood type not in CSV
        );
        
        // Load encounters (appointments) for this patient
        loadEncounters(foundPatient.getPatientId());
        
        // Load medications (prescriptions) for this patient
        loadMedications(foundPatient.getPatientId());
        
        // Enable issue prescription button
        view.setIssuePrescriptionEnabled(true);
    }
    
    // Load encounters from appointments repository
    private void loadEncounters(String patientId) {
        view.clearEncounters();
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (patientId.equals(appointment.getPatientId())) {
                // Get clinician name
                Clinician clinician = clinicianRepository.findById(appointment.getClinicianId());
                String clinicianName = clinician != null ? clinician.getFullName() : appointment.getClinicianId();
                
                // Get notes (can be empty)
                String notes = appointment.getNotes() != null ? appointment.getNotes() : "";
                
                view.addEncounterRow(
                    appointment.getAppointmentDate(),
                    clinicianName,
                    appointment.getReasonForVisit(),
                    notes
                );
            }
        }
    }
    
    // Load medications from prescriptions repository
    private void loadMedications(String patientId) {
        view.clearMedications();
        
        for (Prescription prescription : prescriptionRepository.getAll()) {
            if (patientId.equals(prescription.getPatientId())) {
                view.addMedicationRow(
                    prescription.getMedication(),
                    prescription.getDosage(),
                    prescription.getStatus()
                );
            }
        }
    }
    
    // Handle issue prescription button click
    private void handleIssuePrescription() {
        if (currentPatient == null) {
            JOptionPane.showMessageDialog(view, "Please search for a patient first.", "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create and show prescription dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        NewPrescriptionDialog dialog = new NewPrescriptionDialog(
            parentFrame,
            currentPatient,
            currentClinician
        );
        
        // Set context info in dialog
        dialog.setContextInfo(currentPatient.getFullName(), currentClinician.getFullName());
        
        // Handle confirm button
        dialog.getConfirmButton().addActionListener(e -> {
            String medication = dialog.getMedication();
            String dosage = dialog.getDosage();
            String frequency = dialog.getFrequency();
            String duration = dialog.getDuration();
            String instructions = dialog.getInstructions();
            
            // Validate required fields
            if (medication.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || duration.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create new prescription
            String prescriptionId = prescriptionRepository.generateNewId();
            String today = java.time.LocalDate.now().toString();
            
            Prescription newPrescription = new Prescription(
                prescriptionId,
                currentPatient.getPatientId(),
                currentClinician.getClinicianId(),
                "", // appointmentId - can be empty
                today,
                medication,
                dosage,
                frequency,
                duration,
                "", // quantity - can be calculated later
                instructions,
                "", // pharmacyName - can be set later
                "Issued",
                today,
                "" // collectionDate - empty initially
            );
            
            // Add to repository
            prescriptionRepository.addAndAppend(newPrescription);
            
            // Refresh medications table
            loadMedications(currentPatient.getPatientId());
            
            dialog.dispose();
            JOptionPane.showMessageDialog(view, "Prescription issued successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Handle cancel button
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    // Action listener for search button
    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleSearch();
        }
    }
    
    // Action listener for issue prescription button
    private class IssuePrescriptionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleIssuePrescription();
        }
    }
}

