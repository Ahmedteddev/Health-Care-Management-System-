package view;

import model.Clinician;
import model.Patient;
import javax.swing.*;
import java.awt.*;

public class NewReferralDialog extends JDialog {
    
    private JComboBox<String> specialtyComboBox;
    private JTextField facilityField;
    private JComboBox<String> urgencyComboBox;
    private JTextArea clinicalSummaryArea;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel patientLabel;
    private JLabel clinicianLabel;
    
    private boolean confirmed = false;
    
    // Specialty options
    private static final String[] SPECIALTIES = {
        "Cardiology", "Neurology", "Orthopedics", "Dermatology", 
        "Ophthalmology", "ENT", "Gastroenterology", "Rheumatology",
        "Psychiatry", "Oncology", "Urology", "General Surgery"
    };
    
    // Urgency options
    private static final String[] URGENCY_LEVELS = {
        "Routine", "Urgent", "Emergency"
    };
    
    public NewReferralDialog(JFrame parent, Patient patient, Clinician clinician) {
        super(parent, "Generate New Referral", true);
        
        initializeComponents(patient, clinician);
        setupLayout();
        
        setMinimumSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents(Patient patient, Clinician clinician) {
        // Specialty dropdown
        specialtyComboBox = new JComboBox<>(SPECIALTIES);
        specialtyComboBox.setSelectedIndex(0);
        
        // Facility text field
        facilityField = new JTextField(25);
        
        // Urgency dropdown
        urgencyComboBox = new JComboBox<>(URGENCY_LEVELS);
        urgencyComboBox.setSelectedIndex(0);
        
        // Clinical summary text area
        clinicalSummaryArea = new JTextArea(8, 30);
        clinicalSummaryArea.setLineWrap(true);
        clinicalSummaryArea.setWrapStyleWord(true);
        
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
        clinicianLabel = new JLabel("Referring Clinician: [Will be set]");
        
        gbc.gridx = 0; gbc.gridy = 0;
        infoPanel.add(patientLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        infoPanel.add(clinicianLabel, gbc);
        
        return infoPanel;
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Referral Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Target Specialty
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Target Specialty:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(specialtyComboBox, gbc);
        
        row++;
        // Target Facility
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Target Facility:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(facilityField, gbc);
        
        row++;
        // Urgency
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Urgency:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(urgencyComboBox, gbc);
        
        row++;
        // Clinical Summary
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Clinical Summary:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane summaryScroll = new JScrollPane(clinicalSummaryArea);
        formPanel.add(summaryScroll, gbc);
        
        return formPanel;
    }
    
    // Method to set patient and clinician info
    public void setContextInfo(String patientName, String clinicianName) {
        if (patientLabel != null) {
            patientLabel.setText("Patient: " + patientName);
        }
        if (clinicianLabel != null) {
            clinicianLabel.setText("Referring Clinician: " + clinicianName);
        }
    }
    
    // Getters for form data
    public String getTargetSpecialty() {
        return (String) specialtyComboBox.getSelectedItem();
    }
    
    public String getTargetFacility() {
        return facilityField.getText().trim();
    }
    
    public String getUrgency() {
        return (String) urgencyComboBox.getSelectedItem();
    }
    
    public String getClinicalSummary() {
        return clinicalSummaryArea.getText().trim();
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

