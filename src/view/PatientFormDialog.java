package view;

import model.Patient;
import javax.swing.*;
import java.awt.*;

public class PatientFormDialog extends JDialog {
    
    private JTextField patientIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField dobField;
    private JTextField nhsNumberField;
    private JComboBox<String> genderComboBox;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField postcodeField;
    private JTextField gpSurgeryIdField; 
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean saved = false;
    
    // Gender options
    private static final String[] GENDERS = {
        "M", "F", "Other"
    };
    
    public PatientFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        
        initializeComponents();
        setupLayout();
        
        // Increased height slightly to accommodate the new row
        setMinimumSize(new Dimension(500, 550)); 
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        patientIdField = new JTextField(20);
        patientIdField.setEditable(false); 
        
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        dobField = new JTextField(20);
        nhsNumberField = new JTextField(20);
        genderComboBox = new JComboBox<>(GENDERS);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        postcodeField = new JTextField(20);
        gpSurgeryIdField = new JTextField(20); // INITIALIZING THE NEW FIELD
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = createFormPanel();
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER); // Added scroll pane just in case
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Helper method to add rows quickly to the GridBagLayout
        addRow(formPanel, "Patient ID:", patientIdField, gbc, row++);
        addRow(formPanel, "First Name:", firstNameField, gbc, row++);
        addRow(formPanel, "Last Name:", lastNameField, gbc, row++);
        addRow(formPanel, "Date of Birth (YYYY-MM-DD):", dobField, gbc, row++);
        addRow(formPanel, "NHS Number:", nhsNumberField, gbc, row++);
        addRow(formPanel, "Gender:", genderComboBox, gbc, row++);
        addRow(formPanel, "Phone:", phoneField, gbc, row++);
        addRow(formPanel, "Email:", emailField, gbc, row++);
        addRow(formPanel, "Address:", addressField, gbc, row++);
        addRow(formPanel, "Postcode:", postcodeField, gbc, row++);
        
        // ADDING THE NEW FACILITY FIELD TO THE LAYOUT
        addRow(formPanel, "GP Surgery / Facility ID:", gpSurgeryIdField, gbc, row++);
        
        return formPanel;
    }

    // Utility to keep the code clean and ensure the field actually appears
    private void addRow(JPanel panel, String labelText, JComponent component, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0; gbc.weightx = 0;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(component, gbc);
    }
    
    public Patient getPatientData() {
        Patient patient = new Patient();
        patient.setPatientId(patientIdField.getText().trim());
        patient.setFirstName(firstNameField.getText().trim());
        patient.setLastName(lastNameField.getText().trim());
        patient.setDateOfBirth(dobField.getText().trim());
        patient.setNhsNumber(nhsNumberField.getText().trim());
        patient.setGender((String) genderComboBox.getSelectedItem());
        patient.setPhoneNumber(phoneField.getText().trim());
        patient.setEmail(emailField.getText().trim());
        patient.setAddress(addressField.getText().trim());
        patient.setPostcode(postcodeField.getText().trim());
        
        // MAPPING THE NEW FIELD TO THE PATIENT OBJECT
        patient.setGpSurgeryId(gpSurgeryIdField.getText().trim()); 
        return patient;
    }
    
    public void setPatientData(Patient patient) {
        if (patient == null) return;
        
        patientIdField.setText(patient.getPatientId());
        firstNameField.setText(patient.getFirstName());
        lastNameField.setText(patient.getLastName());
        dobField.setText(patient.getDateOfBirth());
        nhsNumberField.setText(patient.getNhsNumber());
        genderComboBox.setSelectedItem(patient.getGender());
        phoneField.setText(patient.getPhoneNumber());
        emailField.setText(patient.getEmail());
        addressField.setText(patient.getAddress());
        postcodeField.setText(patient.getPostcode());
        
        // SETTING THE VALUE IN THE UI WHEN EDITING
        gpSurgeryIdField.setText(patient.getGpSurgeryId() != null ? patient.getGpSurgeryId() : "");
    }
    
    public void setPatientId(String id) {
        patientIdField.setText(id);
    }
    
    public void setPatientIdEditable(boolean editable) {
        patientIdField.setEditable(editable);
    }
    
    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
    public boolean isSaved() { return saved; }
    public void setSaved(boolean saved) { this.saved = saved; }
}