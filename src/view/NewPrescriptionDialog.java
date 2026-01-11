package view;

import model.Patient;
import javax.swing.*;
import java.awt.*;

public class NewPrescriptionDialog extends JDialog {
    
    private JTextField medicationField;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextField durationField;
    private JTextArea instructionsArea;
    private JTextField clinicianIdField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel patientLabel;
    
    private boolean confirmed = false;
    
    public NewPrescriptionDialog(JFrame parent, Patient patient) {
        super(parent, "Issue/Edit Prescription", true);
        
        initializeComponents();
        setupLayout(patient);
        
        setMinimumSize(new Dimension(500, 400));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        medicationField = new JTextField(25);
        dosageField = new JTextField(25);
        frequencyField = new JTextField(25);
        durationField = new JTextField(25);
        instructionsArea = new JTextArea(5, 25);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        clinicianIdField = new JTextField(25);
        
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout(Patient patient) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel infoPanel = createInfoPanel(patient);
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInfoPanel(Patient patient) {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Context"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        patientLabel = new JLabel("Patient: " + (patient != null ? patient.getFullName() : ""));
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(patientLabel, gbc);
        
        return infoPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Prescription Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Medication Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(medicationField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Dosage:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(dosageField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(frequencyField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Duration (Days):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(durationField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Instructions:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        formPanel.add(instructionsScroll, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Clinician ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(clinicianIdField, gbc);
        
        return formPanel;
    }
    
    // --- GETTERS ---
    public String getMedication() { return medicationField.getText().trim(); }
    public String getDosage() { return dosageField.getText().trim(); }
    public String getFrequency() { return frequencyField.getText().trim(); }
    public String getDuration() { return durationField.getText().trim(); }
    public String getInstructions() { return instructionsArea.getText().trim(); }
    public String getClinicianId() { return clinicianIdField.getText().trim(); }
    public JButton getConfirmButton() { return confirmButton; }
    public JButton getCancelButton() { return cancelButton; }
    public boolean isConfirmed() { return confirmed; }

    // --- SETTERS (REQUIRED FOR EDITING) ---
    public void setMedication(String val) { medicationField.setText(val); }
    public void setDosage(String val) { dosageField.setText(val); }
    public void setFrequency(String val) { frequencyField.setText(val); }
    public void setDuration(String val) { durationField.setText(val); }
    public void setInstructions(String val) { instructionsArea.setText(val); }
    public void setClinicianId(String val) { clinicianIdField.setText(val); }
    public void setConfirmed(boolean confirmed) { this.confirmed = confirmed; }
}