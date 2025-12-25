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

public class PatientManagementController {
    
    private final PatientManagementPanel view;
    private final PatientRepository patientRepository;
    private java.util.List<Patient> allPatientsList = new java.util.ArrayList<>(); // Store all patients for filtering
    
    public PatientManagementController(PatientManagementPanel view,
                                      PatientRepository patientRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        
        bind();
        loadPatients();
    }
    
    private void bind() {
        // Button listeners
        view.getRegisterPatientButton().addActionListener(new RegisterPatientListener());
        view.getEditPatientButton().addActionListener(new EditPatientListener());
        view.getDeletePatientButton().addActionListener(new DeletePatientListener());
        view.getSearchButton().addActionListener(new SearchListener());
        
        // Enter key in search fields also triggers search
        view.getPatientIdField().addActionListener(new SearchListener());
        view.getPatientNameField().addActionListener(new SearchListener());
        view.getNhsNumberField().addActionListener(new SearchListener());
        
        // Table selection listener
        view.getPatientTable().getSelectionModel().addListSelectionListener(new PatientSelectionListener());
    }
    
    // Load patients into table (public for external refresh)
    public void loadPatients() {
        allPatientsList.clear();
        allPatientsList.addAll(patientRepository.getAll());
        view.setPatients(allPatientsList);
    }
    
    // Filter patients by search criteria
    private void filterPatients() {
        String patientId = view.getPatientIdField().getText().trim().toLowerCase();
        String patientName = view.getPatientNameField().getText().trim().toLowerCase();
        String nhsNumber = view.getNhsNumberField().getText().trim().toLowerCase();
        
        java.util.List<Patient> filteredList = new java.util.ArrayList<>();
        
        for (Patient patient : allPatientsList) {
            boolean matches = true;
            
            // Filter by Patient ID
            if (!patientId.isEmpty()) {
                String pId = patient.getPatientId() != null ? patient.getPatientId().toLowerCase() : "";
                if (!pId.contains(patientId)) {
                    matches = false;
                }
            }
            
            // Filter by Name
            if (matches && !patientName.isEmpty()) {
                String fullName = patient.getFullName() != null ? patient.getFullName().toLowerCase() : "";
                String firstName = patient.getFirstName() != null ? patient.getFirstName().toLowerCase() : "";
                String lastName = patient.getLastName() != null ? patient.getLastName().toLowerCase() : "";
                if (!fullName.contains(patientName) && !firstName.contains(patientName) && !lastName.contains(patientName)) {
                    matches = false;
                }
            }
            
            // Filter by NHS Number
            if (matches && !nhsNumber.isEmpty()) {
                String pNhsNumber = patient.getNhsNumber() != null ? patient.getNhsNumber().toLowerCase() : "";
                if (!pNhsNumber.contains(nhsNumber)) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredList.add(patient);
            }
        }
        
        view.setPatients(filteredList);
    }
    
    // Search listener
    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterPatients();
        }
    }
    
    // Register new patient handler
    private class RegisterPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Register New Patient");
            
            // Generate new patient ID
            String newId = patientRepository.generateNewId();
            dialog.setPatientId(newId);
            dialog.setPatientIdEditable(false); // ID is read-only
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                Patient patientData = dialog.getPatientData();
                if (patientData.getFirstName().isEmpty() || patientData.getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Set additional required fields
                String today = LocalDate.now().toString();
                patientData.setRegistrationDate(today);
                if (patientData.getEmergencyContactName() == null || patientData.getEmergencyContactName().isEmpty()) {
                    patientData.setEmergencyContactName("");
                }
                if (patientData.getEmergencyContactPhone() == null || patientData.getEmergencyContactPhone().isEmpty()) {
                    patientData.setEmergencyContactPhone("");
                }
                if (patientData.getGpSurgeryId() == null || patientData.getGpSurgeryId().isEmpty()) {
                    patientData.setGpSurgeryId("");
                }
                
                // Add to repository (saves to CSV)
                patientRepository.addAndAppend(patientData);
                
                // Refresh table from repository and apply current filter
                loadPatients();
                filterPatients();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Patient registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    // Edit patient handler
    private class EditPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get the selected row's Patient ID
            String patientId = view.getSelectedPatientId();
            if (patientId == null) {
                JOptionPane.showMessageDialog(view, "Please select a patient to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Fetch the full Patient object from the PatientRepository
            Patient patient = patientRepository.findById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(view, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Open PatientFormDialog pre-filled with that data
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Edit Patient Details");
            dialog.setPatientData(patient);
            dialog.setPatientIdEditable(false); // ID is read-only when editing
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                Patient updatedPatient = dialog.getPatientData();
                if (updatedPatient.getFirstName().isEmpty() || updatedPatient.getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Preserve fields that aren't in the form
                updatedPatient.setPatientId(patientId); // Keep the same ID
                updatedPatient.setEmergencyContactName(patient.getEmergencyContactName());
                updatedPatient.setEmergencyContactPhone(patient.getEmergencyContactPhone());
                updatedPatient.setRegistrationDate(patient.getRegistrationDate());
                updatedPatient.setGpSurgeryId(patient.getGpSurgeryId());
                
                // Update the object and call updatePatient
                patientRepository.updatePatient(updatedPatient);
                
                // Refresh table from repository and apply current filter
                loadPatients();
                filterPatients();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Patient updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    // Delete patient handler
    private class DeletePatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Patient selectedPatient = view.getSelectedPatient();
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(view, "Please select a patient to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = selectedPatient.getPatientId();
            
            // Confirmation dialog
            int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to delete patient " + selectedPatient.getFullName() + " (" + patientId + ")?\n\n" +
                "This action cannot be undone.",
                "Confirm Patient Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // ONLY removes the patient from the PatientRepository (No cascading)
                Patient patient = patientRepository.findById(patientId);
                if (patient != null) {
                    patientRepository.remove(patient);
                    // Save updated list to CSV
                    patientRepository.saveAll();
                    
                    // Refresh the view immediately and apply current filter
                    loadPatients();
                    filterPatients();
                    
                    JOptionPane.showMessageDialog(view, "Patient record deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(view, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    // Table selection listener
    private class PatientSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedRow() >= 0;
                view.setEditDeleteButtonsEnabled(hasSelection);
            }
        }
    }
    
    // Getter for patient table (for external access if needed)
    public JTable getPatientTable() {
        return view.getPatientTable();
    }
}



