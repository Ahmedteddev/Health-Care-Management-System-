package view;

import model.Patient;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;

public class PatientFormDialog extends JDialog {
    
    private JTextField patientIdField, firstNameField, lastNameField, dobField;
    private JTextField nhsNumberField, phoneField, emailField, addressField;
    private JTextField postcodeField, emergencyNameField, emergencyPhoneField, gpSurgeryIdField;
    private JComboBox<String> genderComboBox;
    private String registrationDate; // Hidden field to preserve the original reg date
    
    private JButton saveButton, cancelButton;
    private boolean saved = false;
    
    private static final String[] GENDERS = {"M", "F", "Other"};
    
    public PatientFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        initializeComponents();
        setupLayout();
        setMinimumSize(new Dimension(500, 650)); 
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
        emergencyNameField = new JTextField(20);
        emergencyPhoneField = new JTextField(20);
        gpSurgeryIdField = new JTextField(20);
        
        // Default registration date to today
        registrationDate = LocalDate.now().toString();
        
        saveButton = new JButton("Save");
        cancelButton = new JButton("Cancel");
    }
    
    private void setupLayout() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder("Patient Details"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int r = 0;
        addRow(formPanel, "Patient ID:", patientIdField, gbc, r++);
        addRow(formPanel, "First Name:", firstNameField, gbc, r++);
        addRow(formPanel, "Last Name:", lastNameField, gbc, r++);
        addRow(formPanel, "DOB (YYYY-MM-DD):", dobField, gbc, r++);
        addRow(formPanel, "NHS Number:", nhsNumberField, gbc, r++);
        addRow(formPanel, "Gender:", genderComboBox, gbc, r++);
        addRow(formPanel, "Phone:", phoneField, gbc, r++);
        addRow(formPanel, "Email:", emailField, gbc, r++);
        addRow(formPanel, "Address:", addressField, gbc, r++);
        addRow(formPanel, "Postcode:", postcodeField, gbc, r++);
        addRow(formPanel, "Emergency Contact:", emergencyNameField, gbc, r++);
        addRow(formPanel, "Emergency Phone:", emergencyPhoneField, gbc, r++);
        addRow(formPanel, "GP Surgery ID:", gpSurgeryIdField, gbc, r++);
        
        mainPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }

    private void addRow(JPanel p, String text, JComponent c, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0;
        p.add(new JLabel(text), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        p.add(c, gbc);
    }
    
    public Patient getPatientData() {
        // This ensures the Patient object is fully populated for the 14-column CSV
        Patient p = new Patient();
        p.setPatientId(patientIdField.getText().trim());
        p.setFirstName(firstNameField.getText().trim());
        p.setLastName(lastNameField.getText().trim());
        p.setDateOfBirth(dobField.getText().trim());
        p.setNhsNumber(nhsNumberField.getText().trim());
        p.setGender((String) genderComboBox.getSelectedItem());
        p.setPhoneNumber(phoneField.getText().trim());
        p.setEmail(emailField.getText().trim());
        p.setAddress(addressField.getText().trim());
        p.setPostcode(postcodeField.getText().trim());
        p.setEmergencyContactName(emergencyNameField.getText().trim());
        p.setEmergencyContactPhone(emergencyPhoneField.getText().trim());
        p.setGpSurgeryId(gpSurgeryIdField.getText().trim());
        p.setRegistrationDate(registrationDate); 
        return p;
    }
    
    public void setPatientData(Patient p) {
        if (p == null) return;
        patientIdField.setText(p.getPatientId());
        firstNameField.setText(p.getFirstName());
        lastNameField.setText(p.getLastName());
        dobField.setText(p.getDateOfBirth());
        nhsNumberField.setText(p.getNhsNumber());
        genderComboBox.setSelectedItem(p.getGender());
        phoneField.setText(p.getPhoneNumber());
        emailField.setText(p.getEmail());
        addressField.setText(p.getAddress());
        postcodeField.setText(p.getPostcode());
        emergencyNameField.setText(p.getEmergencyContactName());
        emergencyPhoneField.setText(p.getEmergencyContactPhone());
        gpSurgeryIdField.setText(p.getGpSurgeryId());
        this.registrationDate = p.getRegistrationDate(); // Keep the original date on edit
    }
    
    public void setPatientId(String id) { patientIdField.setText(id); }
    public JButton getSaveButton() { return saveButton; }
    public JButton getCancelButton() { return cancelButton; }
}