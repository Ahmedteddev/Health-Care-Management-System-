package view;

import model.Clinician;
import javax.swing.*;
import java.awt.*;

public class ClinicianFormDialog extends JDialog {
    
    private JTextField clinicianIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField titleField;
    private JTextField specialityField;
    private JTextField gmcNumberField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField workplaceIdField;
    private JComboBox<String> workplaceTypeComboBox;
    private JTextField startDateField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean saved = false;
    
    // Workplace Type options
    private static final String[] WORKPLACE_TYPES = {
        "GP Surgery", "Hospital", "Clinic"
    };
    
    public ClinicianFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        
        initializeComponents();
        setupLayout();
        
        setMinimumSize(new Dimension(500, 450));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        clinicianIdField = new JTextField(20);
        clinicianIdField.setEditable(false); // ID is read-only when editing
        
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        titleField = new JTextField(20);
        specialityField = new JTextField(20);
        gmcNumberField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        workplaceIdField = new JTextField(20);
        workplaceTypeComboBox = new JComboBox<>(WORKPLACE_TYPES);
        startDateField = new JTextField(20);
        
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Clinician Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Clinician ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Clinician ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(clinicianIdField, gbc);
        
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
        // Title
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Title:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(titleField, gbc);
        
        row++;
        // Specialty
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Specialty:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(specialityField, gbc);
        
        row++;
        // GMC/NMC Number
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("GMC/NMC Number:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(gmcNumberField, gbc);
        
        row++;
        // Email
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(emailField, gbc);
        
        row++;
        // Phone
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Phone:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(phoneField, gbc);
        
        row++;
        // Workplace ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Workplace ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(workplaceIdField, gbc);
        
        row++;
        // Workplace Type
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Workplace Type:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(workplaceTypeComboBox, gbc);
        
        row++;
        // Start Date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(startDateField, gbc);
        
        return formPanel;
    }
    
    // Get the clinician data from the form
    public Clinician getClinicianData() {
        Clinician clinician = new Clinician();
        clinician.setClinicianId(clinicianIdField.getText().trim());
        clinician.setFirstName(firstNameField.getText().trim());
        clinician.setLastName(lastNameField.getText().trim());
        clinician.setTitle(titleField.getText().trim());
        clinician.setSpeciality(specialityField.getText().trim());
        clinician.setGmcNumber(gmcNumberField.getText().trim());
        clinician.setEmail(emailField.getText().trim());
        clinician.setPhoneNumber(phoneField.getText().trim());
        clinician.setWorkplaceId(workplaceIdField.getText().trim());
        clinician.setWorkplaceType((String) workplaceTypeComboBox.getSelectedItem());
        clinician.setStartDate(startDateField.getText().trim());
        return clinician;
    }
    
    // Fill the form with clinician data
    public void setClinicianData(Clinician clinician) {
        if (clinician == null) {
            return;
        }
        
        clinicianIdField.setText(clinician.getClinicianId() != null ? clinician.getClinicianId() : "");
        firstNameField.setText(clinician.getFirstName() != null ? clinician.getFirstName() : "");
        lastNameField.setText(clinician.getLastName() != null ? clinician.getLastName() : "");
        titleField.setText(clinician.getTitle() != null ? clinician.getTitle() : "");
        specialityField.setText(clinician.getSpeciality() != null ? clinician.getSpeciality() : "");
        gmcNumberField.setText(clinician.getGmcNumber() != null ? clinician.getGmcNumber() : "");
        emailField.setText(clinician.getEmail() != null ? clinician.getEmail() : "");
        phoneField.setText(clinician.getPhoneNumber() != null ? clinician.getPhoneNumber() : "");
        workplaceIdField.setText(clinician.getWorkplaceId() != null ? clinician.getWorkplaceId() : "");
        
        String workplaceType = clinician.getWorkplaceType();
        if (workplaceType != null) {
            workplaceTypeComboBox.setSelectedItem(workplaceType);
        }
        
        startDateField.setText(clinician.getStartDate() != null ? clinician.getStartDate() : "");
    }
    
    public void setClinicianId(String id) {
        clinicianIdField.setText(id);
    }
    
    // Make the clinician ID field editable
    public void setClinicianIdEditable(boolean editable) {
        clinicianIdField.setEditable(editable);
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
}

