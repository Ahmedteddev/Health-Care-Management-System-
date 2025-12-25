package view;

import model.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton searchButton;
    private JButton addAdminStaffButton;
    private JButton addClinicianButton;
    private JButton editStaffButton;
    private JButton removeStaffButton;
    
    // Table column names
    private static final String[] COLUMN_NAMES = {
        "Staff ID", "First Name", "Last Name", "Role", "Department", "Email", "Phone"
    };
    
    // All roles from staff.csv and clinicians.csv
    private static final String[] ROLE_FILTERS = {
        "All", "GP", "Consultant", "Nurse", "Practice Manager", "Receptionist", 
        "Medical Secretary", "Healthcare Assistant", "Hospital Administrator", 
        "Ward Clerk", "Porter", "Appointments Coordinator", "Medical Records Clerk", 
        "Children's Unit Coordinator"
    };
    
    public StaffManagementPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Create non-editable table model
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        staffTable = new JTable(tableModel);
        staffTable.setRowHeight(25);
        staffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        staffTable.getTableHeader().setReorderingAllowed(false);
        
        // Search components
        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(ROLE_FILTERS);
        searchButton = new JButton("Search");
        
        addAdminStaffButton = new JButton("Add Admin/Staff");
        addClinicianButton = new JButton("Add Clinician");
        editStaffButton = new JButton("Edit Selected");
        removeStaffButton = new JButton("Remove Staff");
        
        editStaffButton.setEnabled(false);
        removeStaffButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new java.awt.BorderLayout(15, 15));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with heading and search/filter
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // Heading
        JPanel headingPanel = createHeadingPanel();
        topPanel.add(headingPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // Search and Filter Panel
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel);
        
        add(topPanel, java.awt.BorderLayout.NORTH);
        
        // Table with scroll pane (Center)
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Staff & Clinicians"));
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Button panel (South)
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(addAdminStaffButton);
        buttonPanel.add(addClinicianButton);
        buttonPanel.add(editStaffButton);
        buttonPanel.add(removeStaffButton);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
    }
    
    private JPanel createHeadingPanel() {
        JPanel headingPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 0, 5));
        JLabel headingLabel = new JLabel("Staff Management");
        headingLabel.setFont(new java.awt.Font(java.awt.Font.SANS_SERIF, java.awt.Font.BOLD, 18));
        headingPanel.add(headingLabel);
        return headingPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Search & Filter"));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by Role:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    // Public methods
    public void setStaff(List<Staff> staffList) {
        tableModel.setRowCount(0);
        
        for (Staff staff : staffList) {
            tableModel.addRow(new Object[]{
                staff.getStaffId(),
                staff.getFirstName(),
                staff.getLastName(),
                staff.getRole(),
                staff.getDepartment(),
                staff.getEmail(),
                staff.getPhoneNumber()
            });
        }
    }
    
    public int getSelectedRow() {
        return staffTable.getSelectedRow();
    }
    
    public String getSelectedStaffId() {
        int row = staffTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }
    
    // Button getters
    public JButton getAddAdminStaffButton() {
        return addAdminStaffButton;
    }
    
    public JButton getAddClinicianButton() {
        return addClinicianButton;
    }
    
    public JButton getEditStaffButton() {
        return editStaffButton;
    }
    
    public JButton getRemoveStaffButton() {
        return removeStaffButton;
    }
    
    // Get role from selected row
    public String getSelectedRole() {
        int row = staffTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 3); // Role is column 3
        }
        return null;
    }
    
    // Enable/disable buttons based on selection
    public void setEditRemoveButtonsEnabled(boolean enabled) {
        editStaffButton.setEnabled(enabled);
        removeStaffButton.setEnabled(enabled);
    }
    
    // Get staff table
    public JTable getStaffTable() {
        return staffTable;
    }
    
    // Search and filter getters
    public JTextField getSearchField() {
        return searchField;
    }
    
    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
}

