package controller;

import model.*;
import repository.ReferralRepository;
import view.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class MedicalRecordController {
    
    private final MedicalRecordPanel view;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final ReferralRepository referralRepository;
    private final Clinician currentClinician;
    
    private Patient currentPatient = null;
    
    public MedicalRecordController(MedicalRecordPanel view,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository,
                                  PrescriptionRepository prescriptionRepository,
                                  ClinicianRepository clinicianRepository,
                                  FacilityRepository facilityRepository,
                                  ReferralRepository referralRepository,
                                  Clinician currentClinician) {
        this.view = view;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.referralRepository = referralRepository;
        this.currentClinician = currentClinician;
        
        bind();
    }
    
    // Bind all action listeners to the view components
    public void bind() {
        view.getSearchButton().addActionListener(new SearchButtonListener());
        view.getIssuePrescriptionButton().addActionListener(new IssuePrescriptionButtonListener());
        view.getGenerateReferralButton().addActionListener(new GenerateReferralButtonListener());
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
            view.clearReferrals();
            view.setIssuePrescriptionEnabled(false);
            view.setGenerateReferralEnabled(false);
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
        
        // Load referrals for this patient
        loadReferrals(foundPatient.getPatientId());
        
        // Enable action buttons
        view.setIssuePrescriptionEnabled(true);
        view.setGenerateReferralEnabled(true);
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
    
    // Load referrals from referral repository
    private void loadReferrals(String patientId) {
        view.clearReferrals();
        
        for (Referral referral : referralRepository.findByPatientId(patientId)) {
            // Get specialty from referred-to clinician or use requested investigations
            String specialty = referral.getRequestedInvestigations();
            if (specialty == null || specialty.isEmpty()) {
                Clinician referredTo = clinicianRepository.findById(referral.getReferredToClinicianId());
                specialty = referredTo != null ? referredTo.getSpeciality() : "N/A";
            }
            
            // Get facility name
            Facility facility = facilityRepository.findById(referral.getReferredToFacilityId());
            String facilityName = facility != null ? facility.getFacilityName() : referral.getReferredToFacilityId();
            
            view.addReferralRow(
                referral.getReferralDate(),
                specialty,
                facilityName,
                referral.getStatus()
            );
        }
    }
    
    // Handle generate referral button click
    private void handleGenerateReferral() {
        if (currentPatient == null) {
            JOptionPane.showMessageDialog(view, "Please search for a patient first.", "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create and show referral dialog
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        NewReferralDialog dialog = new NewReferralDialog(
            parentFrame,
            currentPatient,
            currentClinician
        );
        
        // Set context info in dialog
        dialog.setContextInfo(currentPatient.getFullName(), currentClinician.getFullName());
        
        // Handle confirm button
        dialog.getConfirmButton().addActionListener(e -> {
            String targetSpecialty = dialog.getTargetSpecialty();
            String targetFacility = dialog.getTargetFacility();
            String urgency = dialog.getUrgency();
            String clinicalSummary = dialog.getClinicalSummary();
            
            // Validate required fields
            if (targetFacility.isEmpty() || clinicalSummary.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Find a clinician with the target specialty (or use empty)
            String referredToClinicianId = "";
            for (Clinician c : clinicianRepository.getAll()) {
                if (targetSpecialty.equals(c.getSpeciality())) {
                    referredToClinicianId = c.getClinicianId();
                    break;
                }
            }
            
            // Find facility ID from name (or use empty)
            String referredToFacilityId = "";
            for (Facility f : facilityRepository.getAll()) {
                if (targetFacility.equals(f.getFacilityName())) {
                    referredToFacilityId = f.getFacilityId();
                    break;
                }
            }
            
            // Create new referral
            String referralId = referralRepository.generateNewId();
            String today = LocalDate.now().toString();
            
            Referral newReferral = new Referral(
                referralId,
                currentPatient.getPatientId(),
                currentClinician.getClinicianId(),
                referredToClinicianId,
                currentClinician.getWorkplaceId() != null ? currentClinician.getWorkplaceId() : "",
                referredToFacilityId,
                today,
                urgency,
                "Referral for " + targetSpecialty,
                clinicalSummary,
                targetSpecialty,
                "Pending",
                "", // appointmentId - empty initially
                "", // notes - empty initially
                today,
                today
            );
            
            // Add to repository
            referralRepository.addAndAppend(newReferral);
            
            // Generate referral letter file
            generateReferralLetter(newReferral);
            
            // Refresh referrals table
            loadReferrals(currentPatient.getPatientId());
            
            dialog.dispose();
            JOptionPane.showMessageDialog(view, "Referral generated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Handle cancel button
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    // Generate referral letter file
    private void generateReferralLetter(Referral referral) {
        Patient patient = patientRepository.findById(referral.getPatientId());
        Facility referringFacility = facilityRepository.findById(referral.getReferringFacilityId());
        Facility referredToFacility = facilityRepository.findById(referral.getReferredToFacilityId());
        
        if (patient == null) {
            System.err.println("Cannot generate letter: Patient not found.");
            return;
        }
        
        // Generate filename: Referral_[PatientName]_[Date].txt
        String patientName = patient.getFullName().replaceAll("[^a-zA-Z0-9]", "_");
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "Referral_" + patientName + "_" + dateStr + ".txt";
        
        // Create output directory if it doesn't exist
        File outputDir = new File("src/data/referrals");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        File letterFile = new File(outputDir, filename);
        
        try (PrintWriter writer = new PrintWriter(letterFile)) {
            // Write letter content
            writer.println("REFERRAL LETTER");
            writer.println("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            writer.println("TO: " + referral.getRequestedInvestigations() + " Department");
            writer.println("FACILITY: " + (referredToFacility != null ? referredToFacility.getFacilityName() : referral.getReferredToFacilityId()));
            writer.println("RE: " + patient.getFullName());
            writer.println("DOB: " + patient.getDateOfBirth());
            writer.println("NHS Number: " + patient.getNhsNumber());
            writer.println();
            writer.println("Dear Consultant,");
            writer.println();
            writer.println("I am referring this patient for further investigation regarding:");
            writer.println();
            writer.println(referral.getClinicalSummary());
            writer.println();
            writer.println("Urgency: " + referral.getUrgencyLevel());
            writer.println();
            writer.println("Sincerely,");
            writer.println(currentClinician.getFullName());
            if (referringFacility != null) {
                writer.println(referringFacility.getFacilityName());
            }
            
            System.out.println("Referral letter generated: " + letterFile.getAbsolutePath());
            
        } catch (Exception ex) {
            System.err.println("Failed to generate referral letter: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(view, 
                "Failed to generate referral letter file.\n" + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Action listener for issue prescription button
    private class IssuePrescriptionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleIssuePrescription();
        }
    }
    
    // Action listener for generate referral button
    private class GenerateReferralButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleGenerateReferral();
        }
    }
}

