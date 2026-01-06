package controller;

import model.*;
import view.PatientFormDialog;
import view.PatientManagementPanel;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PatientManagementController {
    
    private final PatientManagementPanel view;
    private final PatientRepository patientRepository;
    private List<Patient> allPatientsList = new ArrayList<>();
    
    public PatientManagementController(PatientManagementPanel view,
                                      PatientRepository patientRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        
        bind();
        loadPatients();
    }
    
    private void bind() {
        // Wire up buttons
        view.getSearchButton().addActionListener(e -> handleSearch());
        view.getRegisterPatientButton().addActionListener(e -> handleRegisterPatient());
        view.getEditPatientButton().addActionListener(e -> handleEditPatient());
        view.getDeletePatientButton().addActionListener(new DeletePatientListener());
        
        // Search on Enter key
        view.getPatientIdField().addActionListener(e -> handleSearch());
        view.getPatientNameField().addActionListener(e -> handleSearch());
        view.getNhsNumberField().addActionListener(e -> handleSearch());
        
        // Table selection listener
        view.getPatientTable().getSelectionModel().addListSelectionListener(new PatientSelectionListener());
    }
    
    public void loadPatients() {
        // Fetch fresh data from repository
        this.allPatientsList = patientRepository.getAll();
        view.setPatients(allPatientsList);
    }
    
    private void handleSearch() {
        String searchText = view.getPatientIdField().getText().trim();
        String patientName = view.getPatientNameField().getText().trim();
        String nhsNumber = view.getNhsNumberField().getText().trim();
        
        List<Patient> filteredList = new ArrayList<>();
        
        for (Patient patient : allPatientsList) {
            boolean matches = true;
            
            // ID/Name Search
            if (!searchText.isEmpty()) {
                String pId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
                String fullName = (patient.getFirstName() + " " + patient.getLastName()).toLowerCase();
                
                if (!pId.equalsIgnoreCase(searchText) && !fullName.contains(searchText.toLowerCase())) {
                    matches = false;
                }
            }
            
            // NHS Number Search
            if (matches && !nhsNumber.isEmpty()) {
                String pNhs = patient.getNhsNumber() != null ? patient.getNhsNumber().trim() : "";
                if (!pNhs.contains(nhsNumber)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredList.add(patient);
            }
        }
        view.updateTable(filteredList);
    }
    
    private void handleRegisterPatient() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Register New Patient");
        
        // Generate the ID automatically
        String newId = patientRepository.generateNewId();
        dialog.setPatientId(newId);
        dialog.setPatientIdEditable(false);
        
        dialog.getSaveButton().addActionListener(ev -> {
            Patient patientData = dialog.getPatientData();
            
            // Validate Required Fields
            if (patientData.getFirstName().trim().isEmpty() || patientData.getLastName().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Force the ID and registration date
            patientData.setPatientId(newId);
            patientData.setRegistrationDate(LocalDate.now().toString());
            
            // Ensure no null values are passed to the CSV logic
            if (patientData.getEmergencyContactName() == null) patientData.setEmergencyContactName("");
            if (patientData.getEmergencyContactPhone() == null) patientData.setEmergencyContactPhone("");
            if (patientData.getGpSurgeryId() == null) patientData.setGpSurgeryId("");

            // SAVE to repository (this handles adding to list AND appending to CSV)
            patientRepository.add(patientData);
            
            // Confirm and Refresh
            dialog.setSaved(true);
            loadPatients(); // Update internal list
            handleSearch(); // Refresh table view
            
            JOptionPane.showMessageDialog(null, "Registration Successful!\nPatient ID: " + newId);
            dialog.dispose();
        });
        
        dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
        dialog.setVisible(true);
    }
    
    private void handleEditPatient() {
        String selectedId = view.getSelectedPatientId();
        if (selectedId == null) {
            JOptionPane.showMessageDialog(view, "Please select a patient to edit.");
            return;
        }
        
        Patient patient = patientRepository.findById(selectedId);
        if (patient == null) return;
        
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Edit Patient");
        dialog.setPatientData(patient);
        dialog.setPatientIdEditable(false);
        
        dialog.getSaveButton().addActionListener(ev -> {
            Patient updated = dialog.getPatientData();
            updated.setPatientId(patient.getPatientId()); // Keep original ID
            
            patientRepository.updatePatient(updated);
            
            loadPatients();
            handleSearch();
            dialog.dispose();
            JOptionPane.showMessageDialog(null, "Patient record updated.");
        });
        
        dialog.setVisible(true);
    }

    private class DeletePatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Patient selected = view.getSelectedPatient();
            if (selected == null) return;
            
            int confirm = JOptionPane.showConfirmDialog(view, "Delete " + selected.getFullName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                patientRepository.remove(selected);
                patientRepository.saveAll(); // Overwrites CSV without the deleted patient
                loadPatients();
                handleSearch();
            }
        }
    }
    
    private class PatientSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                view.setEditDeleteButtonsEnabled(view.getSelectedRow() >= 0);
            }
        }
    }
}