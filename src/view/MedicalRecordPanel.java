package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class MedicalRecordPanel extends JPanel {
    
    // Search components
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField nhsNumberField;
    private JButton searchButton;
    
    // Summary labels (read-only)
    private JLabel patientNameLabel;
    private JLabel dobLabel;
    private JLabel genderLabel;
    private JLabel bloodTypeLabel;
    
    // Tables
    private JTable encountersTable;
    private DefaultTableModel encountersTableModel;
    private JTable medicationsTable;
    private DefaultTableModel medicationsTableModel;
    private JTable referralsTable;
    private DefaultTableModel referralsTableModel;
    
    // Action buttons
    private JButton issuePrescriptionButton;
    private JButton generateReferralButton;
    private JButton btnPatientNote;
    
    // Table column names
    private static final String[] ENCOUNTERS_COLUMNS = {
        "Date", "Clinician", "Reason", "Notes"
    };
    
    private static final String[] MEDICATIONS_COLUMNS = {
        "Medication Name", "Dosage", "Status"
    };
    
    private static final String[] REFERRALS_COLUMNS = {
        "Date", "Specialty", "Facility", "Status"
    };
    
    public MedicalRecordPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Search fields
        patientIdField = new JTextField(15);
        patientNameField = new JTextField(15);
        nhsNumberField = new JTextField(15);
        searchButton = new JButton("Search");
        
        // Summary labels (read-only)
        patientNameLabel = new JLabel("N/A");
        dobLabel = new JLabel("N/A");
        genderLabel = new JLabel("N/A");
        bloodTypeLabel = new JLabel("N/A");
        
        // Encounters table
        encountersTableModel = new DefaultTableModel(ENCOUNTERS_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        encountersTable = new JTable(encountersTableModel);
        encountersTable.setRowHeight(25);
        
        // Medications table
        medicationsTableModel = new DefaultTableModel(MEDICATIONS_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        medicationsTable = new JTable(medicationsTableModel);
        medicationsTable.setRowHeight(25);
        
        // Referrals table
        referralsTableModel = new DefaultTableModel(REFERRALS_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        referralsTable = new JTable(referralsTableModel);
        referralsTable.setRowHeight(25);
        
        // Action buttons
        issuePrescriptionButton = new JButton("Issue New Prescription");
        issuePrescriptionButton.setEnabled(false);
        generateReferralButton = new JButton("Generate New Referral");
        generateReferralButton.setEnabled(false);
        btnPatientNote = new JButton("View/Edit Patient Clinical Note");
        btnPatientNote.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with heading and search
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // Heading
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JLabel headingLabel = new JLabel("Patients Medical Record");
        headingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        headingPanel.add(headingLabel);
        topPanel.add(headingPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Patient Search"));
        searchPanel.add(new JLabel("Patient ID:"));
        searchPanel.add(patientIdField);
        searchPanel.add(new JLabel("Name:"));
        searchPanel.add(patientNameField);
        searchPanel.add(new JLabel("NHS Number:"));
        searchPanel.add(nhsNumberField);
        searchPanel.add(searchButton);
        topPanel.add(searchPanel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Main content panel with vertical stacked sections
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        
        // Summary panel
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Patient Summary"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(patientNameLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(dobLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(genderLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 3;
        summaryPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(bloodTypeLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        summaryPanel.add(btnPatientNote, gbc);
        // Reset constraints for future use
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        mainContent.add(summaryPanel);
        mainContent.add(Box.createVerticalStrut(15));
        
        // Block 1: Encounters
        JPanel encountersBlock = new JPanel(new BorderLayout(10, 10));
        encountersBlock.setBorder(BorderFactory.createTitledBorder("Encounters"));
        JScrollPane encountersScrollPane = new JScrollPane(encountersTable);
        encountersScrollPane.setPreferredSize(new Dimension(0, 150));
        encountersBlock.add(encountersScrollPane, BorderLayout.CENTER);
        mainContent.add(encountersBlock);
        mainContent.add(Box.createVerticalStrut(15));
        
        // Block 2: Referrals
        JPanel referralsBlock = new JPanel(new BorderLayout(10, 10));
        referralsBlock.setBorder(BorderFactory.createTitledBorder("Referrals"));
        JScrollPane referralsScrollPane = new JScrollPane(referralsTable);
        referralsScrollPane.setPreferredSize(new Dimension(0, 150));
        referralsBlock.add(referralsScrollPane, BorderLayout.CENTER);
        JPanel referralButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        referralButtonPanel.add(generateReferralButton);
        referralsBlock.add(referralButtonPanel, BorderLayout.SOUTH);
        mainContent.add(referralsBlock);
        mainContent.add(Box.createVerticalStrut(15));
        
        // Block 3: Prescriptions
        JPanel prescriptionsBlock = new JPanel(new BorderLayout(10, 10));
        prescriptionsBlock.setBorder(BorderFactory.createTitledBorder("Prescriptions"));
        JScrollPane medicationsScrollPane = new JScrollPane(medicationsTable);
        medicationsScrollPane.setPreferredSize(new Dimension(0, 150));
        prescriptionsBlock.add(medicationsScrollPane, BorderLayout.CENTER);
        JPanel prescriptionButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        prescriptionButtonPanel.add(issuePrescriptionButton);
        prescriptionsBlock.add(prescriptionButtonPanel, BorderLayout.SOUTH);
        mainContent.add(prescriptionsBlock);
        
        // Wrap main content in scroll pane
        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }
    
    // Getters for search fields
    public String getPatientId() {
        return patientIdField.getText().trim();
    }
    
    public String getPatientName() {
        return patientNameField.getText().trim();
    }
    
    public String getNhsNumber() {
        return nhsNumberField.getText().trim();
    }
    
    // Methods to update summary
    public void updateSummary(String name, String dob, String gender, String bloodType) {
        patientNameLabel.setText(name != null ? name : "N/A");
        dobLabel.setText(dob != null ? dob : "N/A");
        genderLabel.setText(gender != null ? gender : "N/A");
        bloodTypeLabel.setText(bloodType != null ? bloodType : "N/A");
    }
    
    // Methods to clear summary
    public void clearSummary() {
        updateSummary(null, null, null, null);
    }
    
    // Methods for encounters table
    public void clearEncounters() {
        encountersTableModel.setRowCount(0);
    }
    
    public void addEncounterRow(String date, String clinician, String reason, String notes) {
        encountersTableModel.addRow(new Object[]{date, clinician, reason, notes});
    }
    
    // Methods for medications table
    public void clearMedications() {
        medicationsTableModel.setRowCount(0);
    }
    
    public void addMedicationRow(String medication, String dosage, String status) {
        medicationsTableModel.addRow(new Object[]{medication, dosage, status});
    }
    
    // Button getters
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JButton getIssuePrescriptionButton() {
        return issuePrescriptionButton;
    }
    
    // Enable/disable issue prescription button
    public void setIssuePrescriptionEnabled(boolean enabled) {
        issuePrescriptionButton.setEnabled(enabled);
    }
    
    // Enable/disable generate referral button
    public void setGenerateReferralEnabled(boolean enabled) {
        generateReferralButton.setEnabled(enabled);
    }
    
    // Get current patient ID (for prescription dialog)
    private String currentPatientId = null;
    
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
    }
    
    public String getCurrentPatientId() {
        return currentPatientId;
    }
    
    // Methods for referrals table
    public void clearReferrals() {
        referralsTableModel.setRowCount(0);
    }
    
    public void addReferralRow(String date, String specialty, String facility, String status) {
        referralsTableModel.addRow(new Object[]{date, specialty, facility, status});
    }
    
    // Button getters
    public JButton getGenerateReferralButton() {
        return generateReferralButton;
    }
    
    public JButton getBtnPatientNote() {
        return btnPatientNote;
    }
    
    // Enable/disable patient note button
    public void setPatientNoteEnabled(boolean enabled) {
        btnPatientNote.setEnabled(enabled);
    }
}
