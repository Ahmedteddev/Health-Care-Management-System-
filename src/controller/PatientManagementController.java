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
    private java.util.List<Patient> allPatientsList = new java.util.ArrayList<>();
    
    public PatientManagementController(PatientManagementPanel view,
                                      PatientRepository patientRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        
        bind();
        loadPatients();
    }
    
    private void bind() {
        // Wire up all listeners in the constructor
        view.getSearchButton().addActionListener(e -> handleSearch());
        view.getRegisterPatientButton().addActionListener(e -> handleRegisterPatient());
        view.getEditPatientButton().addActionListener(e -> handleEditPatient());
        view.getDeletePatientButton().addActionListener(new DeletePatientListener());
        
        // Search on Enter key in text fields
        view.getPatientIdField().addActionListener(e -> handleSearch());
        view.getPatientNameField().addActionListener(e -> handleSearch());
        view.getNhsNumberField().addActionListener(e -> handleSearch());
        
        // Enable/disable edit button based on selection
        view.getPatientTable().getSelectionModel().addListSelectionListener(new PatientSelectionListener());
    }
    
    public void loadPatients() {
        allPatientsList.clear();
        allPatientsList.addAll(patientRepository.getAll());
        view.setPatients(allPatientsList);
    }
    
    /**
     * Search Logic: Implement handleSearch() to filter the PatientRepository list.
     * Match the search text against patientId (e.g., "P001") and fullName.
     */
    private void handleSearch() {
        // Get the search text (e.g., "P001")
        String searchText = view.getPatientIdField().getText().trim();
        String patientName = view.getPatientNameField().getText().trim();
        String nhsNumber = view.getNhsNumberField().getText().trim();
        
        System.out.println("Searching for Patient ID: " + searchText);
        
        java.util.List<Patient> filteredList = new java.util.ArrayList<>();
        
        // Filter the PatientRepository list
        for (Patient patient : allPatientsList) {
            boolean matches = true;
            
            // Filter by exact Patient ID match (e.g., "P001") using equalsIgnoreCase
            if (!searchText.isEmpty()) {
                String pId = patient.getPatientId() != null ? patient.getPatientId().trim() : "";
                // Check for exact ID match (case-insensitive) or name match
                if (!pId.equalsIgnoreCase(searchText)) {
                    // If not exact ID match, check if the search text matches the name
                    String fullName = patient.getFullName() != null ? patient.getFullName() : "";
                    String firstName = patient.getFirstName() != null ? patient.getFirstName() : "";
                    String lastName = patient.getLastName() != null ? patient.getLastName() : "";
                    String searchLower = searchText.toLowerCase();
                    if (!fullName.toLowerCase().contains(searchLower) && 
                        !firstName.toLowerCase().contains(searchLower) && 
                        !lastName.toLowerCase().contains(searchLower)) {
                        matches = false;
                    }
                }
            }
            
            // Additional filter by patient name field
            if (matches && !patientName.isEmpty()) {
                String fullName = patient.getFullName() != null ? patient.getFullName().toLowerCase() : "";
                String firstName = patient.getFirstName() != null ? patient.getFirstName().toLowerCase() : "";
                String lastName = patient.getLastName() != null ? patient.getLastName().toLowerCase() : "";
                String nameLower = patientName.toLowerCase();
                if (!fullName.contains(nameLower) && !firstName.contains(nameLower) && !lastName.contains(nameLower)) {
                    matches = false;
                }
            }
            
            // Additional filter by NHS number
            if (matches && !nhsNumber.isEmpty()) {
                String pNhsNumber = patient.getNhsNumber() != null ? patient.getNhsNumber().toLowerCase() : "";
                if (!pNhsNumber.contains(nhsNumber.toLowerCase())) {
                    matches = false;
                }
            }
            
            if (matches) {
                filteredList.add(patient);
            }
        }
        
        // Use view.updateTable() to show results
        view.updateTable(filteredList);
    }
    
    /**
     * Register Logic: Open NewPatientDialog (PatientFormDialog).
     * Ensure the dialog doesn't require a Clinician object.
     * If dialog.isConfirmed(), get the new Patient object, call repository.addAndSave(patient),
     * and immediately call refreshTable() in the view.
     */
    private void handleRegisterPatient() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Register New Patient");
        
        // Generate new ID in P001 format
        String newId = patientRepository.generateNewId();
        dialog.setPatientId(newId);
        dialog.setPatientIdEditable(false);
        
        dialog.getSaveButton().addActionListener(ev -> {
            Patient patientData = dialog.getPatientData();
            
            // Validation
            if (patientData.getFirstName().isEmpty() || patientData.getLastName().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Set required fields
            String today = LocalDate.now().toString();
            patientData.setRegistrationDate(today);
            patientData.setPatientId(newId); // Ensure P001 format is maintained
            
            // Set optional fields to empty string if null
            if (patientData.getEmergencyContactName() == null || patientData.getEmergencyContactName().isEmpty()) {
                patientData.setEmergencyContactName("");
            }
            if (patientData.getEmergencyContactPhone() == null || patientData.getEmergencyContactPhone().isEmpty()) {
                patientData.setEmergencyContactPhone("");
            }
            if (patientData.getGpSurgeryId() == null || patientData.getGpSurgeryId().isEmpty()) {
                patientData.setGpSurgeryId("");
            }
            
            // Call repository.addAndSave() (or addAndAppend which saves to CSV)
            patientRepository.addAndAppend(patientData);
            
            // Mark dialog as confirmed
            dialog.setSaved(true);
            
            // Immediately call refreshTable() in the view
            loadPatients();
            view.refreshTable();
            
            dialog.dispose();
            System.out.println("[Success]: Patient registered successfully with ID: " + newId);
        });
        
        dialog.getCancelButton().addActionListener(ev -> {
            dialog.setSaved(false);
            dialog.dispose();
        });
        
        dialog.setVisible(true);
        
        // Check if dialog was confirmed after it closes
        if (dialog.isConfirmed()) {
            // Data already saved in the save button listener above
        }
    }
    
    /**
     * Edit Logic: Get the selected row from view.getSelectedPatientId().
     * Open an EditPatientDialog (PatientFormDialog) pre-filled with that patient's data.
     * On save, update the repository and refresh the UI table.
     */
    private void handleEditPatient() {
        // Get the selected row from view.getSelectedPatientId()
        String selectedId = view.getSelectedPatientId();
        if (selectedId == null || selectedId.trim().isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please select a patient to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Use trim() on ID lookup to prevent issues with hidden spaces
        final String patientId = selectedId.trim();
        Patient patient = patientRepository.findById(patientId);
        if (patient == null) {
            JOptionPane.showMessageDialog(view, "Patient not found with ID: " + patientId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open an EditPatientDialog pre-filled with that patient's data
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Edit Patient Details");
        dialog.setPatientData(patient);
        dialog.setPatientIdEditable(false); // ID cannot be changed
        
        dialog.getSaveButton().addActionListener(ev -> {
            Patient updatedPatient = dialog.getPatientData();
            
            // Validation
            if (updatedPatient.getFirstName().isEmpty() || updatedPatient.getLastName().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Ensure P001 format is strictly maintained - use original patientId
            updatedPatient.setPatientId(patientId);
            
            // Preserve fields that shouldn't change
            updatedPatient.setEmergencyContactName(patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "");
            updatedPatient.setEmergencyContactPhone(patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "");
            updatedPatient.setRegistrationDate(patient.getRegistrationDate() != null ? patient.getRegistrationDate() : "");
            updatedPatient.setGpSurgeryId(patient.getGpSurgeryId() != null ? patient.getGpSurgeryId() : "");
            
            // On save, update the repository
            patientRepository.updatePatient(updatedPatient);
            
            // Mark dialog as confirmed
            dialog.setSaved(true);
            
            // Refresh the UI table
            loadPatients();
            view.refreshTable();
            
            dialog.dispose();
            System.out.println("[Success]: Patient updated successfully! ID: " + patientId);
        });
        
        dialog.getCancelButton().addActionListener(ev -> {
            dialog.setSaved(false);
            dialog.dispose();
        });
        
        dialog.setVisible(true);
    }
    
    private class DeletePatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            Patient selectedPatient = view.getSelectedPatient();
            if (selectedPatient == null) {
                JOptionPane.showMessageDialog(view, "Please select a patient to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String patientId = selectedPatient.getPatientId();
            
            int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to delete patient " + selectedPatient.getFullName() + " (" + patientId + ")?\n\n" +
                "This action cannot be undone.",
                "Confirm Patient Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                Patient patient = patientRepository.findById(patientId);
                if (patient != null) {
                    patientRepository.remove(patient);
                    patientRepository.saveAll();
                    loadPatients();
                    handleSearch(); // Use handleSearch() instead of filterPatients()
                    
                    System.out.println("[Success]: Patient record deleted successfully.");
                } else {
                    JOptionPane.showMessageDialog(view, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private class PatientSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedRow() >= 0;
                view.setEditDeleteButtonsEnabled(hasSelection);
            }
        }
    }
    
    public JTable getPatientTable() {
        return view.getPatientTable();
    }
}




