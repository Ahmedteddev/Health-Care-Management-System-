package controller;

import model.*;
import model.PatientRepository; // Ensure this import is correct
import view.PatientFormDialog;
import view.PatientManagementPanel;
import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.List;

public class PatientManagementController {
    
    private final PatientManagementPanel view;
    private final PatientRepository patientRepository;
    
    public PatientManagementController(PatientManagementPanel view, PatientRepository patientRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        
        bind();
        refreshUI(); // Initial load
    }
    
    private void bind() {
        view.getSearchButton().addActionListener(e -> handleSearch());
        view.getRegisterButton().addActionListener(e -> handleRegisterPatient());
        view.getEditButton().addActionListener(e -> handleEditPatient());
        view.getDeleteButton().addActionListener(e -> handleDeletePatient());
        
        // Selection listener to enable/disable buttons
        view.getPatientTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedPatientId() != null;
                view.setEditDeleteButtonsEnabled(hasSelection);
            }
        });
    }

    private void refreshUI() {
        List<Patient> allPatients = patientRepository.findAll();
        view.updateTable(allPatients);
        view.setEditDeleteButtonsEnabled(false);
    }

    private void handleSearch() {
        String id = view.getPatientIdField().getText().trim();
        String name = view.getPatientNameField().getText().trim();
        String nhs = view.getNhsNumberField().getText().trim();
        
        // Using the search method we added to the Repository
        List<Patient> results = patientRepository.search(id, name, nhs);
        view.updateTable(results);
    }

    private void handleRegisterPatient() {
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parent, "Register New Patient");
        
        // Generate a simple ID based on list size or timestamp
        String newId = "P" + (patientRepository.findAll().size() + 1001);
        dialog.setPatientId(newId);
        
        dialog.getSaveButton().addActionListener(e -> {
            Patient p = dialog.getPatientData();
            p.setPatientId(newId);
            
            patientRepository.add(p);
            refreshUI(); 
            dialog.dispose();
            JOptionPane.showMessageDialog(view, "Patient registered successfully.");
        });
        
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void handleEditPatient() {
        String id = view.getSelectedPatientId();
        if (id == null) return;
        
        Patient original = patientRepository.findById(id);
        if (original == null) return;
        
        JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(view);
        PatientFormDialog dialog = new PatientFormDialog(parent, "Edit Patient");
        dialog.setPatientData(original);
        
        dialog.getSaveButton().addActionListener(e -> {
            Patient updated = dialog.getPatientData();
            updated.setPatientId(id); 
            
            patientRepository.update(updated); // Matched to Repository.update()
            refreshUI();
            dialog.dispose();
            JOptionPane.showMessageDialog(view, "Patient updated successfully.");
        });
        
        dialog.getCancelButton().addActionListener(e -> dialog.dispose());
        dialog.setVisible(true);
    }

    private void handleDeletePatient() {
        String id = view.getSelectedPatientId();
        if (id == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(view, 
            "Are you sure you want to delete patient " + id + "?", 
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            patientRepository.delete(id); // Matched to Repository.delete()
            refreshUI();
            JOptionPane.showMessageDialog(view, "Patient deleted.");
        }
    }
}