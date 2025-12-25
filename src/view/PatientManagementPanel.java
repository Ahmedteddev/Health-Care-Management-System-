package view;

import model.Patient;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class PatientManagementPanel extends JPanel {
    
    private JTable patientTable;
    private PatientTableModel tableModel;
    private JTextField patientIdField;
    private JTextField patientNameField;
    private JTextField nhsNumberField;
    private JButton searchButton;
    private JButton registerPatientButton;
    private JButton editPatientButton;
    private JButton deletePatientButton;
    
    // Table column names
    private static final String[] COLUMN_NAMES = {
        "ID", "First Name", "Last Name", "DOB", "NHS Number", "Gender", "Contact"
    };
    
    public PatientManagementPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        tableModel = new PatientTableModel();
        patientTable = new JTable(tableModel);
        patientTable.setRowHeight(25);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.getTableHeader().setReorderingAllowed(false);
        
        // Search fields
        patientIdField = new JTextField(15);
        patientNameField = new JTextField(15);
        nhsNumberField = new JTextField(15);
        searchButton = new JButton("Search");
        
        registerPatientButton = new JButton("Register New");
        editPatientButton = new JButton("Edit");
        deletePatientButton = new JButton("Delete");
        
        editPatientButton.setEnabled(false);
        deletePatientButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with heading and search
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // Heading
        JPanel headingPanel = createHeadingPanel();
        topPanel.add(headingPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // Search panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel);
        
        add(topPanel, java.awt.BorderLayout.NORTH);
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(patientTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Patients"));
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(registerPatientButton);
        buttonPanel.add(editPatientButton);
        buttonPanel.add(deletePatientButton);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }
    
    private JPanel createHeadingPanel() {
        JPanel headingPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));
        JLabel headingLabel = new JLabel("Patient Management");
        headingLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
        headingPanel.add(headingLabel);
        return headingPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Patient Search"));
        
        searchPanel.add(new JLabel("Patient ID:"));
        searchPanel.add(patientIdField);
        searchPanel.add(new JLabel("Name:"));
        searchPanel.add(patientNameField);
        searchPanel.add(new JLabel("NHS Number:"));
        searchPanel.add(nhsNumberField);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    // Custom TableModel for inline editing
    public class PatientTableModel extends AbstractTableModel {
        private List<Patient> patients;
        
        public PatientTableModel() {
            this.patients = new java.util.ArrayList<>();
        }
        
        public void setPatients(List<Patient> patients) {
            this.patients = patients != null ? new java.util.ArrayList<>(patients) : new java.util.ArrayList<>();
            fireTableDataChanged();
        }
        
        @Override
        public int getRowCount() {
            return patients.size();
        }
        
        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }
        
        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= patients.size()) {
                return "";
            }
            
            Patient patient = patients.get(rowIndex);
            switch (columnIndex) {
                case 0: return patient.getPatientId();
                case 1: return patient.getFirstName();
                case 2: return patient.getLastName();
                case 3: return patient.getDateOfBirth();
                case 4: return patient.getNhsNumber();
                case 5: return patient.getGender();
                case 6: return patient.getPhoneNumber() != null ? patient.getPhoneNumber() : patient.getEmail();
                default: return "";
            }
        }
        
        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (rowIndex >= patients.size()) {
                return;
            }
            
            Patient patient = patients.get(rowIndex);
            String value = aValue != null ? aValue.toString() : "";
            
            switch (columnIndex) {
                case 0: 
                    // ID is not editable
                    break;
                case 1: 
                    patient.setFirstName(value);
                    break;
                case 2: 
                    patient.setLastName(value);
                    break;
                case 3: 
                    patient.setDateOfBirth(value);
                    break;
                case 4: 
                    patient.setNhsNumber(value);
                    break;
                case 5: 
                    patient.setGender(value);
                    break;
                case 6: 
                    // Contact - update phone number if it looks like a phone, otherwise email
                    if (value.contains("@")) {
                        patient.setEmail(value);
                    } else {
                        patient.setPhoneNumber(value);
                    }
                    break;
            }
            
            fireTableCellUpdated(rowIndex, columnIndex);
        }
        
        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Make all cells non-editable (disable inline editing)
            return false;
        }
        
        public Patient getPatientAt(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < patients.size()) {
                return patients.get(rowIndex);
            }
            return null;
        }
        
        public void addPatient(Patient patient) {
            patients.add(patient);
            fireTableRowsInserted(patients.size() - 1, patients.size() - 1);
        }
        
        public void removePatient(int rowIndex) {
            if (rowIndex >= 0 && rowIndex < patients.size()) {
                patients.remove(rowIndex);
                fireTableRowsDeleted(rowIndex, rowIndex);
            }
        }
    }
    
    // Public methods
    public void setPatients(List<Patient> patients) {
        tableModel.setPatients(patients);
    }
    
    public int getSelectedRow() {
        return patientTable.getSelectedRow();
    }
    
    public Patient getSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            return tableModel.getPatientAt(row);
        }
        return null;
    }
    
    public String getSelectedPatientId() {
        Patient patient = getSelectedPatient();
        return patient != null ? patient.getPatientId() : null;
    }
    
    public void addPatient(Patient patient) {
        tableModel.addPatient(patient);
    }
    
    public void removeSelectedPatient() {
        int row = patientTable.getSelectedRow();
        if (row >= 0) {
            tableModel.removePatient(row);
        }
    }
    
    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }
    
    // Button getters
    public JButton getRegisterPatientButton() {
        return registerPatientButton;
    }
    
    public JButton getEditPatientButton() {
        return editPatientButton;
    }
    
    public JButton getDeletePatientButton() {
        return deletePatientButton;
    }
    
    // Enable/disable edit and delete buttons
    public void setEditDeleteButtonsEnabled(boolean enabled) {
        editPatientButton.setEnabled(enabled);
        deletePatientButton.setEnabled(enabled);
    }
    
    // Get table model for listener attachment
    public PatientTableModel getTableModel() {
        return tableModel;
    }
    
    // Get patient table
    public JTable getPatientTable() {
        return patientTable;
    }
    
    // Search field getters
    public JTextField getPatientIdField() {
        return patientIdField;
    }
    
    public JTextField getPatientNameField() {
        return patientNameField;
    }
    
    public JTextField getNhsNumberField() {
        return nhsNumberField;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
}

