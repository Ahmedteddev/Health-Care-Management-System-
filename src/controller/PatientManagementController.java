package controller;

import model.*;
import repository.ReferralRepository;
import view.PatientManagementPanel;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class PatientManagementController {
    
    private final PatientManagementPanel view;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ReferralRepository referralRepository;
    
    public PatientManagementController(PatientManagementPanel view,
                                      PatientRepository patientRepository,
                                      AppointmentRepository appointmentRepository,
                                      PrescriptionRepository prescriptionRepository,
                                      ReferralRepository referralRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.referralRepository = referralRepository;
        
        bind();
        loadPatients();
    }
    
    private void bind() {
        // Button listeners
        view.getAddPatientButton().addActionListener(new AddPatientListener());
        view.getDeletePatientButton().addActionListener(new DeletePatientListener());
        
        // Table selection listener
        view.getPatientTable().getSelectionModel().addListSelectionListener(new PatientSelectionListener());
        
        // Table model listener for inline editing
        view.getTableModel().addTableModelListener(new PatientTableModelListener());
    }
    
    // Load patients into table
    private void loadPatients() {
        view.setPatients(patientRepository.getAll());
    }
    
    // Add patient handler
    private class AddPatientListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Generate new patient ID
            String newId = patientRepository.generateNewId();
            String today = LocalDate.now().toString();
            
            // Create blank patient record
            Patient newPatient = new Patient(
                newId,           // patientId
                "",              // firstName
                "",              // lastName
                "",              // dateOfBirth
                "",              // nhsNumber
                "",              // gender
                "",              // phoneNumber
                "",              // email
                "",              // address
                "",              // postcode
                "",              // emergencyContactName
                "",              // emergencyContactPhone
                today,           // registrationDate
                ""               // gpSurgeryId
            );
            
            // Add to repository (saves to CSV)
            patientRepository.addAndAppend(newPatient);
            
            // Add to table
            view.addPatient(newPatient);
            
            // Select the new row
            int newRow = view.getTableModel().getRowCount() - 1;
            view.getPatientTable().setRowSelectionInterval(newRow, newRow);
            view.getPatientTable().scrollRectToVisible(
                view.getPatientTable().getCellRect(newRow, 0, true)
            );
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
                "WARNING: This action is irreversible and will delete all associated medical data, appointments, prescriptions, and referrals.\n\n" +
                "Patient: " + selectedPatient.getFullName() + " (" + patientId + ")\n\n" +
                "Proceed with deletion?",
                "Confirm Patient Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // Cascading deletion: remove related data first
                appointmentRepository.deleteByPatientId(patientId);
                prescriptionRepository.deleteByPatientId(patientId);
                referralRepository.deleteByPatientId(patientId);
                
                // Finally, delete the patient
                patientRepository.deletePatient(patientId, true); // Skip confirmation (already shown)
                
                // Refresh the view immediately
                loadPatients();
                
                JOptionPane.showMessageDialog(view, "Patient and all associated data deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // Table selection listener
    private class PatientSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedRow() >= 0;
                view.setDeleteButtonEnabled(hasSelection);
            }
        }
    }
    
    // Table model listener for inline editing
    private class PatientTableModelListener implements TableModelListener {
        @Override
        public void tableChanged(TableModelEvent e) {
            // Only handle updates (not inserts or deletes)
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                if (row >= 0 && row < view.getTableModel().getRowCount()) {
                    Patient patient = view.getTableModel().getPatientAt(row);
                    if (patient != null) {
                        // Update patient in repository
                        patientRepository.updatePatient(patient);
                        System.out.println("Patient " + patient.getPatientId() + " updated via inline editing.");
                    }
                }
            }
        }
    }
    
    // Getter for patient table (for external access if needed)
    public JTable getPatientTable() {
        return view.getPatientTable();
    }
}

