package controller;

import model.*;
import repository.PatientRepository;
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
    private final ReferralRepository referralRepo;
    
    private Patient currentPatient = null;
    
    public MedicalRecordController(MedicalRecordPanel view,
                                  PatientRepository patientRepository,
                                  AppointmentRepository appointmentRepository,
                                  PrescriptionRepository prescriptionRepository,
                                  ClinicianRepository clinicianRepository,
                                  FacilityRepository facilityRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.referralRepo = ReferralRepository.getInstance("src/data/referrals.csv");
        
        bind();
    }
    
    public void bind() {
        view.getSearchButton().addActionListener(new SearchButtonListener());
        view.getIssuePrescriptionButton().addActionListener(new IssuePrescriptionButtonListener());
        view.getGenerateReferralButton().addActionListener(new GenerateReferralButtonListener());
    }
    
    private void handleSearch() {
        String patientId = view.getPatientId();
        String patientName = view.getPatientName();
        String nhsNumber = view.getNhsNumber();
        
        Patient foundPatient = null;
        
        if (!patientId.isEmpty()) {
            foundPatient = patientRepository.findById(patientId);
        } else if (!patientName.isEmpty()) {
            for (Patient p : patientRepository.getAll()) {
                if (p.getFullName().toLowerCase().contains(patientName.toLowerCase())) {
                    foundPatient = p;
                    break;
                }
            }
        } else if (!nhsNumber.isEmpty()) {
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
        
        currentPatient = foundPatient;
        view.setCurrentPatientId(foundPatient.getPatientId());
        
        view.updateSummary(
            foundPatient.getFullName(),
            foundPatient.getDateOfBirth(),
            foundPatient.getGender(),
            "N/A"
        );
        
        loadEncounters(foundPatient.getPatientId());
        loadMedications(foundPatient.getPatientId());
        loadReferrals(foundPatient.getPatientId());
        
        view.setIssuePrescriptionEnabled(true);
        view.setGenerateReferralEnabled(true);
    }
    
    private void loadEncounters(String patientId) {
        view.clearEncounters();
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (patientId.equals(appointment.getPatientId())) {
                Clinician clinician = clinicianRepository.findById(appointment.getClinicianId());
                String clinicianName = clinician != null ? clinician.getFullName() : appointment.getClinicianId();
                
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
    
    private void handleIssuePrescription() {
        if (currentPatient == null) {
            JOptionPane.showMessageDialog(view, "Please search for a patient first.", "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        NewPrescriptionDialog dialog = new NewPrescriptionDialog(
            parentFrame,
            currentPatient
        );
        
        dialog.getConfirmButton().addActionListener(e -> {
            // Pull all fields from dialog including the manual clinicianId
            String medication = dialog.getMedication();
            String dosage = dialog.getDosage();
            String frequency = dialog.getFrequency();
            String duration = dialog.getDuration();
            String instructions = dialog.getInstructions();
            String clinicianId = dialog.getClinicianId(); // Manually entered ID from text field
            
            if (medication.isEmpty() || dosage.isEmpty() || frequency.isEmpty() || duration.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (clinicianId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in Clinician ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String prescriptionId = prescriptionRepository.generateNewId();
            String today = java.time.LocalDate.now().toString();
            
            // Create Prescription using the manually entered clinicianId from the dialog
            Prescription newPrescription = new Prescription(
                prescriptionId,
                currentPatient.getPatientId(),
                clinicianId, // Using ID from user input in text field
                "",
                today,
                medication,
                dosage,
                frequency,
                duration,
                "",
                instructions,
                "",
                "Issued",
                today,
                ""
            );
            
            // Pass all fields to addAndAppend
            prescriptionRepository.addAndAppend(newPrescription);
            
            // Call generatePrescriptionFile using the manually entered ID
            // Look up clinician name for display purposes, but use the manually entered ID
            Clinician clinician = clinicianRepository.findById(clinicianId);
            String clinicianName = clinician != null ? clinician.getFullName() : clinicianId;
            prescriptionRepository.generatePrescriptionFile(newPrescription, clinicianName, clinicianId);
            
            loadMedications(currentPatient.getPatientId());
            
            dialog.dispose();
            System.out.println("[Success]: Prescription issued successfully!");
        });
        
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    private class SearchButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleSearch();
        }
    }
    
    private void loadReferrals(String patientId) {
        view.clearReferrals();
        
        for (Referral referral : referralRepo.findByPatientId(patientId)) {
            String specialty = referral.getRequestedInvestigations();
            if (specialty == null || specialty.isEmpty()) {
                Clinician referredTo = clinicianRepository.findById(referral.getReferredToClinicianId());
                specialty = referredTo != null ? referredTo.getSpeciality() : "N/A";
            }
            
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
    
    private void handleGenerateReferral() {
        if (currentPatient == null) {
            JOptionPane.showMessageDialog(view, "Please search for a patient first.", "No Patient Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        NewReferralDialog dialog = new NewReferralDialog(
            parentFrame,
            currentPatient
        );
        
        dialog.getConfirmButton().addActionListener(e -> {
            // Pull all fields from dialog including referring_clinician_id and referred_to_clinician_id
            String urgency = dialog.getUrgency();
            String referralReason = dialog.getReferralReason();
            String clinicalSummary = dialog.getClinicalSummary();
            String requestedInvestigations = dialog.getRequestedInvestigations();
            String referringClinicianId = dialog.getReferringClinicianId(); // Manually entered ID from text field
            String referredToClinicianId = dialog.getReferredToClinicianId(); // Manually entered ID from text field
            
            if (clinicalSummary.isEmpty() || referralReason.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all required fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (referringClinicianId.isEmpty() || referredToClinicianId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in both Referring Clinician ID and Referred To Clinician ID.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String referralId = referralRepo.generateNewId();
            String today = LocalDate.now().toString();
            
            // Create Referral using the manually entered clinician IDs from the dialog
            Referral newReferral = new Referral(
                referralId,
                currentPatient.getPatientId(),
                referringClinicianId, // Using ID from user input in text field
                referredToClinicianId, // Using ID from user input in text field
                "",
                "",
                today,
                urgency,
                referralReason,
                clinicalSummary,
                requestedInvestigations,
                "Pending",
                "",
                "",
                today,
                today
            );
            
            // Pass all fields including clinician IDs to addAndAppend
            referralRepo.addAndAppend(newReferral);
            
            // Get referring clinician name for letter (for display purposes only)
            Clinician referringClinician = clinicianRepository.findById(referringClinicianId);
            String referringClinicianName = referringClinician != null ? referringClinician.getFullName() : referringClinicianId;
            generateReferralLetter(newReferral, referringClinicianName);
            loadReferrals(currentPatient.getPatientId());
            
            dialog.dispose();
            System.out.println("[Success]: Referral generated successfully!");
        });
        
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        
        dialog.setVisible(true);
    }
    
    // generates a text file for the referral letter - saves it to the referrals folder
    private void generateReferralLetter(Referral referral, String practitionerName) {
        Patient patient = patientRepository.findById(referral.getPatientId());
        Facility referredToFacility = facilityRepository.findById(referral.getReferredToFacilityId());
        
        if (patient == null) {
            System.err.println("Cannot generate letter: Patient not found.");
            return;
        }
        
        String patientName = patient.getFullName().replaceAll("[^a-zA-Z0-9]", "_");
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String filename = "Referral_" + patientName + "_" + dateStr + ".txt";
        
        File outputDir = new File("src/data/referrals");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }
        
        File letterFile = new File(outputDir, filename);
        
        try (PrintWriter writer = new PrintWriter(letterFile)) {
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
            writer.println(practitionerName != null ? practitionerName : "");
            
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
    
    private class IssuePrescriptionButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleIssuePrescription();
        }
    }
    
    private class GenerateReferralButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            handleGenerateReferral();
        }
    }
}

