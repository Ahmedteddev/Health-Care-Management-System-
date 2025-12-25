package view;

import model.Clinician;
import model.Patient;
import javax.swing.*;
import java.awt.*;

public class NewPrescriptionDialog extends JDialog {
    
    private JTextField medicationField;
    private JTextField dosageField;
    private JTextField frequencyField;
    private JTextField durationField;
    private JTextArea instructionsArea;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel patientLabel;
    private JLabel clinicianLabel;
    
    private boolean confirmed = false;
    
    public NewPrescriptionDialog(JFrame parent, Patient patient, Clinician clinician) {
        super(parent, "Issue New Prescription", true);
        
        initializeComponents(patient, clinician);
        setupLayout();
        
        setMinimumSize(new Dimension(500, 400));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents(Patient patient, Clinician clinician) {
        // Form fields
        medicationField = new JTextField(25);
        dosageField = new JTextField(25);
        frequencyField = new JTextField(25);
        durationField = new JTextField(25);
        instructionsArea = new JTextArea(5, 25);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        
        // Buttons
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top: Read-only patient and clinician info
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Center: Form fields
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Bottom: Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBorder(BorderFactory.createTitledBorder("Context"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Create labels that will be updated by controller
        patientLabel = new JLabel("Patient: [Will be set]");
        clinicianLabel = new JLabel("Clinician: [Will be set]");
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(patientLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(clinicianLabel, gbc);
        
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
        
        // Medication Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Medication Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(medicationField, gbc);
        
        row++;
        // Dosage
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Dosage:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(dosageField, gbc);
        
        row++;
        // Frequency
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Frequency:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(frequencyField, gbc);
        
        row++;
        // Duration
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Duration (days):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(durationField, gbc);
        
        row++;
        // Instructions
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Instructions:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane instructionsScroll = new JScrollPane(instructionsArea);
        formPanel.add(instructionsScroll, gbc);
        
        return formPanel;
    }
    
    // Method to set patient and clinician info
    public void setContextInfo(String patientName, String clinicianName) {
        if (patientLabel != null) {
            patientLabel.setText("Patient: " + patientName);
        }
        if (clinicianLabel != null) {
            clinicianLabel.setText("Clinician: " + clinicianName);
        }
    }
    
    // Getters for form data
    public String getMedication() {
        return medicationField.getText().trim();
    }
    
    public String getDosage() {
        return dosageField.getText().trim();
    }
    
    public String getFrequency() {
        return frequencyField.getText().trim();
    }
    
    public String getDuration() {
        return durationField.getText().trim();
    }
    
    public String getInstructions() {
        return instructionsArea.getText().trim();
    }
    
    // Button getters
    public JButton getConfirmButton() {
        return confirmButton;
    }
    
    public JButton getCancelButton() {
        return cancelButton;
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
    
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}

