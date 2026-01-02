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
        
        setMinimumSize(new Dimension(500, 400));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        patientIdField = new JTextField(20);
        patientIdField.setEditable(false); // ID is read-only when editing
        
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        dobField = new JTextField(20);
        nhsNumberField = new JTextField(20);
        genderComboBox = new JComboBox<>(GENDERS);
        phoneField = new JTextField(20);
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        postcodeField = new JTextField(20);
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Form panel with GridBagLayout
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Button panel
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
        
        // Patient ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Patient ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(patientIdField, gbc);
        
        row++;
        // First Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("First Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(firstNameField, gbc);
        
        row++;
        // Last Name
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Last Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(lastNameField, gbc);
        
        row++;
        // Date of Birth
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(dobField, gbc);
        
        row++;
        // NHS Number
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("NHS Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(nhsNumberField, gbc);
        
        row++;
        // Gender
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Gender:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(genderComboBox, gbc);
        
        row++;
        // Phone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phoneField, gbc);
        
        row++;
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        row++;
        // Address
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(addressField, gbc);
        
        row++;
        // Postcode
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Postcode:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(postcodeField, gbc);
        
        return formPanel;
    }
    
    /**
     * Returns a Patient object with the data from the form.
     * Note: patientId should be set by the controller for new patients.
     */
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
        return patient;
    }
    
    /**
     * Pre-fills the form fields with data from a Patient object.
     */
    public void setPatientData(Patient patient) {
        if (patient == null) {
            return;
        }
        
        patientIdField.setText(patient.getPatientId() != null ? patient.getPatientId() : "");
        firstNameField.setText(patient.getFirstName() != null ? patient.getFirstName() : "");
        lastNameField.setText(patient.getLastName() != null ? patient.getLastName() : "");
        dobField.setText(patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "");
        nhsNumberField.setText(patient.getNhsNumber() != null ? patient.getNhsNumber() : "");
        
        String gender = patient.getGender();
        if (gender != null) {
            genderComboBox.setSelectedItem(gender);
        }
        
        phoneField.setText(patient.getPhoneNumber() != null ? patient.getPhoneNumber() : "");
        emailField.setText(patient.getEmail() != null ? patient.getEmail() : "");
        addressField.setText(patient.getAddress() != null ? patient.getAddress() : "");
        postcodeField.setText(patient.getPostcode() != null ? patient.getPostcode() : "");
    }
    
    /**
     * Sets the patient ID field (for new patients, ID is generated).
     */
    public void setPatientId(String id) {
        patientIdField.setText(id);
    }
    
    /**
     * Makes the patient ID field editable (for new patients).
     */
    public void setPatientIdEditable(boolean editable) {
        patientIdField.setEditable(editable);
    }
    
    // Button getters
    public JButton getSaveButton() {
        return saveButton;
    }
    
    public JButton getCancelButton() {
        return cancelButton;
    }
    
    public boolean isSaved() {
        return saved;
    }
    
    public void setSaved(boolean saved) {
        this.saved = saved;
    }
    
    /**
     * Alias for isSaved() for compatibility with controller code.
     */
    public boolean isConfirmed() {
        return saved;
    }
}



