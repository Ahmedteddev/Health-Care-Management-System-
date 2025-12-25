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
    
    // Tabbed pane with tables
    private JTabbedPane tabbedPane;
    private JTable encountersTable;
    private DefaultTableModel encountersTableModel;
    private JTable medicationsTable;
    private DefaultTableModel medicationsTableModel;
    
    // Action button
    private JButton issuePrescriptionButton;
    
    // Table column names
    private static final String[] ENCOUNTERS_COLUMNS = {
        "Date", "Clinician", "Reason", "Notes"
    };
    
    private static final String[] MEDICATIONS_COLUMNS = {
        "Medication Name", "Dosage", "Status"
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
        
        // Tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Action button
        issuePrescriptionButton = new JButton("Issue New Prescription");
        issuePrescriptionButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top: Search panel
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Middle: Summary panel
        JPanel summaryPanel = createSummaryPanel();
        add(summaryPanel, BorderLayout.CENTER);
        
        // Bottom: Tabbed pane and action button
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        
        // Tabbed pane with tables
        JScrollPane encountersScrollPane = new JScrollPane(encountersTable);
        JScrollPane medicationsScrollPane = new JScrollPane(medicationsTable);
        
        tabbedPane.addTab("Encounters", encountersScrollPane);
        tabbedPane.addTab("Medications", medicationsScrollPane);
        
        bottomPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Action button panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.add(issuePrescriptionButton);
        bottomPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Patient Search"));
        
        searchPanel.add(new JLabel("Patient ID:"));
        searchPanel.add(patientIdField);
        searchPanel.add(new JLabel("Name:"));
        searchPanel.add(patientNameField);
        searchPanel.add(new JLabel("NHS Number:"));
        searchPanel.add(nhsNumberField);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Patient Summary"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Patient Name
        gbc.gridx = 0; gbc.gridy = 0;
        summaryPanel.add(new JLabel("Patient Name:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(patientNameLabel, gbc);
        
        // Date of Birth
        gbc.gridx = 0; gbc.gridy = 1;
        summaryPanel.add(new JLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(dobLabel, gbc);
        
        // Gender
        gbc.gridx = 0; gbc.gridy = 2;
        summaryPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(genderLabel, gbc);
        
        // Blood Type (placeholder - not in CSV, showing N/A)
        gbc.gridx = 0; gbc.gridy = 3;
        summaryPanel.add(new JLabel("Blood Type:"), gbc);
        gbc.gridx = 1;
        summaryPanel.add(bloodTypeLabel, gbc);
        
        return summaryPanel;
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
    
    // Get current patient ID (for prescription dialog)
    private String currentPatientId = null;
    
    public void setCurrentPatientId(String patientId) {
        this.currentPatientId = patientId;
    }
    
    public String getCurrentPatientId() {
        return currentPatientId;
    }
}

