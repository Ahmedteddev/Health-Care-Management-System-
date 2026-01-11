package controller;

import model.*;
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
        
        // Added Listeners for Edit and Delete
        view.getEditPrescriptionButton().addActionListener(e -> handleEditPrescription());
        view.getDeletePrescriptionButton().addActionListener(e -> handleDeletePrescription());
        
        view.getGenerateReferralButton().addActionListener(new GenerateReferralButtonListener());
        view.getBtnPatientNote().addActionListener(e -> handleViewEditClinicalNote());
    }
    
    private void handleSearch() {
        String patientId = view.getPatientId();
        String patientName = view.getPatientName();
        String nhsNumber = view.getNhsNumber();
        
        Patient foundPatient = null;
        
        if (!patientId.isEmpty()) {
            foundPatient = patientRepository.findById(patientId);
        } else if (!patientName.isEmpty()) {
            for (Patient p : patientRepository.findAll()) {
                if (p.getFullName().toLowerCase().contains(patientName.toLowerCase())) {
                    foundPatient = p;
                    break;
                }
            }
        } else if (!nhsNumber.isEmpty()) {
            for (Patient p : patientRepository.findAll()) {
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
            view.setPatientNoteEnabled(false);
            view.setCurrentPatientId(null);
            return;
        }
        
        currentPatient = foundPatient;
        view.setCurrentPatientId(foundPatient.getPatientId());
        
        //Removed Blood Type argument
        view.updateSummary(
            foundPatient.getFullName(),
            foundPatient.getDateOfBirth(),
            foundPatient.getGender()
        );
        
        loadEncounters(foundPatient.getPatientId());
        loadMedications(foundPatient.getPatientId());
        loadReferrals(foundPatient.getPatientId());
        
        view.setIssuePrescriptionEnabled(true);
        view.setGenerateReferralEnabled(true);
        view.setPatientNoteEnabled(true);
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
                // FIXED: Added ID as first argument
                view.addMedicationRow(
                    prescription.getId(),
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
            String medication = dialog.getMedication();
            String dosage = dialog.getDosage();
            String frequency = dialog.getFrequency();
            String duration = dialog.getDuration();
            String instructions = dialog.getInstructions();
            String clinicianId = dialog.getClinicianId(); 
            
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
            
            Prescription newPrescription = new Prescription(
                prescriptionId,
                currentPatient.getPatientId(),
                clinicianId,
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
            
            prescriptionRepository.addAndAppend(newPrescription);
            
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

    private void handleEditPrescription() {
        String prescriptionId = view.getSelectedPrescriptionId();
        if (prescriptionId == null) return;

        Prescription existing = prescriptionRepository.findById(prescriptionId);
        if (existing == null) return;

        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        NewPrescriptionDialog dialog = new NewPrescriptionDialog(parentFrame, currentPatient);
        
        // Fill dialog with existing data
        dialog.setMedication(existing.getMedication());
        dialog.setDosage(existing.getDosage());
        dialog.setFrequency(existing.getFrequency());
        dialog.setDuration(existing.getDurationDays());
        dialog.setInstructions(existing.getInstructions());
        dialog.setClinicianId(existing.getClinicianId());

        dialog.getConfirmButton().addActionListener(e -> {
            existing.setMedication(dialog.getMedication());
            existing.setDosage(dialog.getDosage());
            existing.setFrequency(dialog.getFrequency());
            existing.setDurationDays(dialog.getDuration());
            existing.setInstructions(dialog.getInstructions());
            
            prescriptionRepository.update(existing);
            loadMedications(currentPatient.getPatientId());
            dialog.dispose();
        });

        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void handleDeletePrescription() {
        String prescriptionId = view.getSelectedPrescriptionId();
        if (prescriptionId == null) return;

        int confirm = JOptionPane.showConfirmDialog(view, 
            "Are you sure you want to delete prescription " + prescriptionId + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            prescriptionRepository.removeById(prescriptionId);
            loadMedications(currentPatient.getPatientId());
        }
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
            String urgency = dialog.getUrgency();
            String referralReason = dialog.getReferralReason();
            String clinicalSummary = dialog.getClinicalSummary();
            String requestedInvestigations = dialog.getRequestedInvestigations();
            String referringClinicianId = dialog.getReferringClinicianId();
            String referredToClinicianId = dialog.getReferredToClinicianId();
            
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
            
            Referral newReferral = new Referral(
                referralId,
                currentPatient.getPatientId(),
                referringClinicianId,
                referredToClinicianId,
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
            
            referralRepo.addAndAppend(newReferral);
            
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
    
    private void handleViewEditClinicalNote() {
        String currentPatientId = view.getCurrentPatientId();
        
        if (currentPatientId == null || currentPatientId.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "Please search for a patient first.", 
                "No Patient Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        java.util.List<Appointment> patientAppointments = appointmentRepository.getByPatientId(currentPatientId);
        
        if (patientAppointments == null || patientAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(view, 
                "No appointments found for this patient yet.", 
                "No Appointments", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        Appointment latestRecord = null;
        String latestDate = "";
        
        for (Appointment appointment : patientAppointments) {
            String appointmentDate = appointment.getAppointmentDate();
            if (appointmentDate != null && !appointmentDate.isEmpty()) {
                if (latestDate.isEmpty() || appointmentDate.compareTo(latestDate) > 0) {
                    latestDate = appointmentDate;
                    latestRecord = appointment;
                }
            }
        }
        
        if (latestRecord == null && !patientAppointments.isEmpty()) {
            latestRecord = patientAppointments.get(0);
        }
        
        if (latestRecord == null) {
            JOptionPane.showMessageDialog(view, 
                "No appointments found for this patient yet.", 
                "No Appointments", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String patientNote = latestRecord.getNotes();
        if (patientNote == null) {
            patientNote = "";
        }
        
        JTextArea noteArea = new JTextArea(10, 40);
        noteArea.setText(patientNote);
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setCaretPosition(0); 
        
        JScrollPane scrollPane = new JScrollPane(noteArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        int result = JOptionPane.showConfirmDialog(
            view,
            scrollPane,
            "Edit Clinical Note for Patient " + currentPatientId,
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String updatedNote = noteArea.getText();
            latestRecord.setNotes(updatedNote);
            
            String today = java.time.LocalDate.now().toString();
            latestRecord.setLastModified(today);
            
            appointmentRepository.updateAppointment(latestRecord);
            
            JOptionPane.showMessageDialog(null, 
                "Clinical note saved to the patient's record!", 
                "Note Saved", 
                JOptionPane.INFORMATION_MESSAGE);
            
            loadEncounters(currentPatientId);
        }
    }
}