package view;

import model.Patient;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PatientManagementPanel extends JPanel {
    
    private JTable patientTable;
    private DefaultTableModel tableModel;
    
    // Search Fields (Preserved for functionality)
    private JTextField idField, nameField, nhsField;
    
    // Buttons (Renamed to match Appointment style naming)
    private JButton searchButton;
    private JButton registerButton;
    private JButton editButton;
    private JButton deleteButton;
    
    private static final String[] COLUMN_NAMES = {
        "ID", "First", "Last", "DOB", "NHS No", "Gender", "Phone", 
        "Email", "Address", "Postcode", "E-Name", "E-Phone", "Reg Date", "GP ID"
    };
    
    public PatientManagementPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        patientTable = new JTable(tableModel);
        patientTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        patientTable.setRowHeight(25);
        patientTable.getTableHeader().setReorderingAllowed(false);
        // Allows horizontal scrolling for the 14 columns
        patientTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); 
        
        // Search Components
        idField = new JTextField(10);
        nameField = new JTextField(10);
        nhsField = new JTextField(10);
        searchButton = new JButton("Search");
        
        // Action Buttons
        registerButton = new JButton("Register New");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top Panel (Heading + Search stacked vertically)
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // 1. Heading (Style copied from AppointmentPanel)
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JLabel headingLabel = new JLabel("Patient Management");
        headingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        headingPanel.add(headingLabel);
        topPanel.add(headingPanel);
        
        topPanel.add(Box.createVerticalStrut(10));
        
        // 2. Search Panel (Consistent with AppointmentPanel Titled Border)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search Filters"));
        
        searchPanel.add(new JLabel("ID:"));
        searchPanel.add(idField);
        searchPanel.add(new JLabel("Name:"));
        searchPanel.add(nameField);
        searchPanel.add(new JLabel("NHS:"));
        searchPanel.add(nhsField);
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel);
        add(topPanel, BorderLayout.NORTH);
        
        // Central Table (Center)
        JScrollPane tableScrollPane = new JScrollPane(patientTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Patient Records"));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Action Footer (South - Right Aligned)
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.add(registerButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    public void updateTable(List<Patient> list) {
        tableModel.setRowCount(0);
        for (Patient p : list) {
            tableModel.addRow(new Object[]{
                p.getPatientId(), p.getFirstName(), p.getLastName(), p.getDateOfBirth(),
                p.getNhsNumber(), p.getGender(), p.getPhoneNumber(), p.getEmail(),
                p.getAddress(), p.getPostcode(), p.getEmergencyContactName(),
                p.getEmergencyContactPhone(), p.getRegistrationDate(), p.getGpSurgeryId()
            });
        }
    }

    public String getSelectedPatientId() {
        int row = patientTable.getSelectedRow();
        return (row >= 0) ? (String) tableModel.getValueAt(row, 0) : null;
    }

    // --- Getters for Controller ---
    public JTable getPatientTable() { return patientTable; }
    public JTextField getPatientIdField() { return idField; }
    public JTextField getPatientNameField() { return nameField; }
    public JTextField getNhsNumberField() { return nhsField; }
    public JButton getSearchButton() { return searchButton; }
    public JButton getRegisterButton() { return registerButton; }
    public JButton getEditButton() { return editButton; }
    public JButton getDeleteButton() { return deleteButton; }
    
    public void setEditDeleteButtonsEnabled(boolean enabled) {
        editButton.setEnabled(enabled);
        deleteButton.setEnabled(enabled);
    }
}