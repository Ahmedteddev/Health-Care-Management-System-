package view;

import model.Patient;
import javax.swing.*;
import java.awt.*;

public class NewReferralDialog extends JDialog {
    
    private JComboBox<String> urgencyComboBox;
    private JTextField referralReasonField;
    private JTextArea clinicalSummaryArea;
    private JTextField requestedInvestigationsField;
    private JTextField referringClinicianIdField;
    private JTextField referredToClinicianIdField;
    private JButton confirmButton;
    private JButton cancelButton;
    private JLabel patientLabel;
    
    private boolean confirmed = false;
    
    // Urgency options
    private static final String[] URGENCY_LEVELS = {
        "Routine", "Urgent"
    };
    
    // Constructor: No Clinician object - only takes parent frame and patient
    public NewReferralDialog(JFrame parent, Patient patient) {
        super(parent, "Generate New Referral", true);
        
        initializeComponents();
        setupLayout(patient);
        
        setMinimumSize(new Dimension(600, 500));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        // Urgency dropdown
        urgencyComboBox = new JComboBox<>(URGENCY_LEVELS);
        urgencyComboBox.setSelectedIndex(0);
        
        // Referral reason text field
        referralReasonField = new JTextField(25);
        
        // Clinical summary text area
        clinicalSummaryArea = new JTextArea(8, 30);
        clinicalSummaryArea.setLineWrap(true);
        clinicalSummaryArea.setWrapStyleWord(true);
        
        // Requested investigations text field
        requestedInvestigationsField = new JTextField(25);
        
        // Clinician ID fields
        referringClinicianIdField = new JTextField(25);
        referredToClinicianIdField = new JTextField(25);
        
        // Buttons
        confirmButton = new JButton("Confirm");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout(Patient patient) {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top: Read-only patient info
        JPanel infoPanel = createInfoPanel(patient);
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Referral Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Urgency Level
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Urgency Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(urgencyComboBox, gbc);
        
        row++;
        // Referral Reason
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Referral Reason:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(referralReasonField, gbc);
        
        row++;
        // Clinical Summary
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Clinical Summary:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JScrollPane summaryScroll = new JScrollPane(clinicalSummaryArea);
        formPanel.add(summaryScroll, gbc);
        
        row++;
        // Requested Investigations
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0; gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Requested Investigations:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(requestedInvestigationsField, gbc);
        
        row++;
        // Referring Clinician ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Referring Clinician ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(referringClinicianIdField, gbc);
        
        row++;
        // Referred To Clinician ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Referred To Clinician ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(referredToClinicianIdField, gbc);
        
        return formPanel;
    }
    
    // Getters for form data
    public String getUrgency() {
        return (String) urgencyComboBox.getSelectedItem();
    }
    
    public String getReferralReason() {
        return referralReasonField.getText().trim();
    }
    
    public String getClinicalSummary() {
        return clinicalSummaryArea.getText().trim();
    }
    
    public String getRequestedInvestigations() {
        return requestedInvestigationsField.getText().trim();
    }
    
    // Method: Get Referring Clinician ID from text field (manually entered by user)
    public String getReferringClinicianId() {
        return referringClinicianIdField.getText().trim();
    }
    
    // Method: Get Referred To Clinician ID from text field (manually entered by user)
    public String getReferredToClinicianId() {
        return referredToClinicianIdField.getText().trim();
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
