package view;

import model.Staff;
import javax.swing.*;
import java.awt.*;

public class StaffFormDialog extends JDialog {
    
    private JTextField staffIdField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JComboBox<String> roleComboBox;
    private JTextField departmentField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> accessLevelComboBox;
    private JTextField startDateField;
    private JButton saveButton;
    private JButton cancelButton;
    
    private boolean saved = false;
    
    // Role options (excluding GP and Nurse - those use ClinicianFormDialog)
    private static final String[] ROLES = {
        "Admin", "Receptionist", "Practice Manager", "Medical Secretary", 
        "Healthcare Assistant", "Hospital Administrator", "Ward Clerk", 
        "Porter", "Appointments Coordinator", "Medical Records Clerk", 
        "Children's Unit Coordinator"
    };
    
    // Access Level options
    private static final String[] ACCESS_LEVELS = {
        "Basic", "Standard", "Manager"
    };
    
    public StaffFormDialog(JFrame parent, String title) {
        super(parent, title, true);
        
        initializeComponents();
        setupLayout();
        
        setMinimumSize(new Dimension(500, 400));
        pack();
        setLocationRelativeTo(parent);
    }
    
    private void initializeComponents() {
        staffIdField = new JTextField(20);
        staffIdField.setEditable(false); // ID is read-only when editing
        
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        roleComboBox = new JComboBox<>(ROLES);
        departmentField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        accessLevelComboBox = new JComboBox<>(ACCESS_LEVELS);
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
        formPanel.setBorder(BorderFactory.createTitledBorder("Staff Details"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Staff ID
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Staff ID:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(staffIdField, gbc);
        
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
        // Role
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(roleComboBox, gbc);
        
        row++;
        // Department
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Department:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(departmentField, gbc);
        
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
        // Access Level
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Access Level:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(accessLevelComboBox, gbc);
        
        row++;
        // Start Date
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(startDateField, gbc);
        
        return formPanel;
    }
    
    /**
     * Returns a Staff object with the data from the form.
     * Note: staffId should be set by the controller for new staff.
     */
    public Staff getStaffData() {
        Staff staff = new Staff();
        staff.setStaffId(staffIdField.getText().trim());
        staff.setFirstName(firstNameField.getText().trim());
        staff.setLastName(lastNameField.getText().trim());
        staff.setRole((String) roleComboBox.getSelectedItem());
        staff.setDepartment(departmentField.getText().trim());
        staff.setEmail(emailField.getText().trim());
        staff.setPhoneNumber(phoneField.getText().trim());
        staff.setAccessLevel((String) accessLevelComboBox.getSelectedItem());
        staff.setStartDate(startDateField.getText().trim());
        return staff;
    }
    
    /**
     * Pre-fills the form fields with data from a Staff object.
     */
    public void setStaffData(Staff staff) {
        if (staff == null) {
            return;
        }
        
        staffIdField.setText(staff.getStaffId() != null ? staff.getStaffId() : "");
        firstNameField.setText(staff.getFirstName() != null ? staff.getFirstName() : "");
        lastNameField.setText(staff.getLastName() != null ? staff.getLastName() : "");
        
        String role = staff.getRole();
        if (role != null) {
            roleComboBox.setSelectedItem(role);
        }
        
        departmentField.setText(staff.getDepartment() != null ? staff.getDepartment() : "");
        emailField.setText(staff.getEmail() != null ? staff.getEmail() : "");
        phoneField.setText(staff.getPhoneNumber() != null ? staff.getPhoneNumber() : "");
        
        String accessLevel = staff.getAccessLevel();
        if (accessLevel != null) {
            accessLevelComboBox.setSelectedItem(accessLevel);
        }
        
        startDateField.setText(staff.getStartDate() != null ? staff.getStartDate() : "");
    }
    
    /**
     * Sets the staff ID field (for new staff, ID is generated).
     */
    public void setStaffId(String id) {
        staffIdField.setText(id);
    }
    
    /**
     * Makes the staff ID field editable (for new staff).
     */
    public void setStaffIdEditable(boolean editable) {
        staffIdField.setEditable(editable);
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


