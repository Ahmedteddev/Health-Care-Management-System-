package controller;

import model.*;
import repository.PatientRepository;
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
        view.getRegisterPatientButton().addActionListener(new RegisterPatientListener());
        view.getEditPatientButton().addActionListener(new EditPatientListener());
        view.getDeletePatientButton().addActionListener(new DeletePatientListener());
        view.getSearchButton().addActionListener(new SearchListener());
        
        view.getPatientIdField().addActionListener(new SearchListener());
        view.getPatientNameField().addActionListener(new SearchListener());
        view.getNhsNumberField().addActionListener(new SearchListener());
        
        view.getPatientTable().getSelectionModel().addListSelectionListener(new PatientSelectionListener());
    }
    
    public void loadPatients() {
        allPatientsList.clear();
        allPatientsList.addAll(patientRepository.getAll());
        view.setPatients(allPatientsList);
    }
    
    private void filterPatients() {
        String patientId = view.getPatientIdField().getText().trim().toLowerCase();
        String patientName = view.getPatientNameField().getText().trim().toLowerCase();
        String nhsNumber = view.getNhsNumberField().getText().trim().toLowerCase();
        
        java.util.List<Patient> filteredList = new java.util.ArrayList<>();
        
        for (Patient patient : allPatientsList) {
            boolean matches = true;
            
            if (!patientId.isEmpty()) {
                String pId = patient.getPatientId() != null ? patient.getPatientId().toLowerCase() : "";
                if (!pId.contains(patientId)) {
                    matches = false;
                }
            }
            
            if (matches && !patientName.isEmpty()) {
                String fullName = patient.getFullName() != null ? patient.getFullName().toLowerCase() : "";
                String firstName = patient.getFirstName() != null ? patient.getFirstName().toLowerCase() : "";
                String lastName = patient.getLastName() != null ? patient.getLastName().toLowerCase() : "";
                if (!fullName.contains(patientName) && !firstName.contains(patientName) && !lastName.contains(patientName)) {
                    matches = false;
                }
            }
            
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
    
    private class SearchListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterPatients();
        }
    }
    
    private class RegisterPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Register New Patient");
            
            String newId = patientRepository.generateNewId();
            dialog.setPatientId(newId);
            dialog.setPatientIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                Patient patientData = dialog.getPatientData();
                if (patientData.getFirstName().isEmpty() || patientData.getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
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
                
                patientRepository.addAndAppend(patientData);
                loadPatients();
                filterPatients();
                
                dialog.dispose();
                System.out.println("[Success]: Patient registered successfully!");
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    private class EditPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String patientId = view.getSelectedPatientId();
            if (patientId == null) {
                JOptionPane.showMessageDialog(view, "Please select a patient to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Patient patient = patientRepository.findById(patientId);
            if (patient == null) {
                JOptionPane.showMessageDialog(view, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            PatientFormDialog dialog = new PatientFormDialog(parentFrame, "Edit Patient Details");
            dialog.setPatientData(patient);
            dialog.setPatientIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                Patient updatedPatient = dialog.getPatientData();
                if (updatedPatient.getFirstName().isEmpty() || updatedPatient.getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                updatedPatient.setPatientId(patientId);
                updatedPatient.setEmergencyContactName(patient.getEmergencyContactName());
                updatedPatient.setEmergencyContactPhone(patient.getEmergencyContactPhone());
                updatedPatient.setRegistrationDate(patient.getRegistrationDate());
                updatedPatient.setGpSurgeryId(patient.getGpSurgeryId());
                
                patientRepository.updatePatient(updatedPatient);
                loadPatients();
                filterPatients();
                
                dialog.dispose();
                System.out.println("[Success]: Patient updated successfully!");
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
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
                    filterPatients();
                    
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




