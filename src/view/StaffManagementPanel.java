package view;

import model.Staff;
import model.Clinician;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    
    // Top table for Administrative Staff (base Staff class)
    private JTable topStaffTable;
    private DefaultTableModel topStaffTableModel;
    
    // Bottom table for Clinicians (GP, Nurse, Specialist)
    private JTable bottomClinicianTable;
    private DefaultTableModel bottomClinicianTableModel;
    
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton searchButton;
    private JButton addAdminStaffButton;
    private JButton addClinicianButton;
    private JButton editStaffButton;
    private JButton removeStaffButton;
    
    // Table column names for Administrative Staff
    private static final String[] ADMIN_STAFF_COLUMN_NAMES = {
        "Staff ID", "First Name", "Last Name", "Role", "Department", "Email", "Phone"
    };
    
    // Table column names for Clinicians
    private static final String[] CLINICIAN_COLUMN_NAMES = {
        "Clinician ID", "First Name", "Last Name", "Title", "Speciality", "Email", "Phone", "Type"
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
        // Create non-editable table model for Administrative Staff (top table)
        topStaffTableModel = new DefaultTableModel(ADMIN_STAFF_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        topStaffTable = new JTable(topStaffTableModel);
        topStaffTable.setRowHeight(25);
        topStaffTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        topStaffTable.getTableHeader().setReorderingAllowed(false);
        
        // Create non-editable table model for Clinicians (bottom table)
        bottomClinicianTableModel = new DefaultTableModel(CLINICIAN_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        bottomClinicianTable = new JTable(bottomClinicianTableModel);
        bottomClinicianTable.setRowHeight(25);
        bottomClinicianTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bottomClinicianTable.getTableHeader().setReorderingAllowed(false);
        
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
        
        // Main content panel (Center) - use GridLayout with spacing, similar to Patient Dashboard
        JPanel mainContentPanel = new JPanel(new java.awt.GridLayout(2, 1, 15, 15));
        
        // Top table: Administrative Staff
        JPanel adminStaffPanel = new JPanel(new java.awt.BorderLayout(10, 10));
        adminStaffPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Administrative Staff"));
        JScrollPane topStaffScroll = new JScrollPane(topStaffTable);
        topStaffScroll.setPreferredSize(new java.awt.Dimension(0, 300)); // Set preferred height
        adminStaffPanel.add(topStaffScroll, java.awt.BorderLayout.CENTER);
        mainContentPanel.add(adminStaffPanel);
        
        // Bottom table: Clinicians
        JPanel clinicianPanel = new JPanel(new java.awt.BorderLayout(10, 10));
        clinicianPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Clinicians"));
        JScrollPane bottomClinicianScroll = new JScrollPane(bottomClinicianTable);
        bottomClinicianScroll.setPreferredSize(new java.awt.Dimension(0, 300)); // Set preferred height
        clinicianPanel.add(bottomClinicianScroll, java.awt.BorderLayout.CENTER);
        mainContentPanel.add(clinicianPanel);
        
        add(mainContentPanel, java.awt.BorderLayout.CENTER);
        
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
    
    // Public methods to set Administrative Staff (top table)
    public void setAdminStaff(List<Staff> adminStaffList) {
        topStaffTableModel.setRowCount(0);
        
        for (Staff staff : adminStaffList) {
            topStaffTableModel.addRow(new Object[]{
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
    
    // Public methods to set Clinicians (bottom table)
    public void setClinicians(List<Clinician> clinicianList) {
        bottomClinicianTableModel.setRowCount(0);
        
        for (Clinician clinician : clinicianList) {
            // Determine the type of clinician (GP, Nurse, Specialist, or generic Clinician)
            String clinicianType = "Clinician";
            if (clinician instanceof model.GP) {
                clinicianType = "GP";
            } else if (clinician instanceof model.Nurse) {
                clinicianType = "Nurse";
            } else if (clinician instanceof model.Specialist) {
                clinicianType = "Specialist";
            }
            
            bottomClinicianTableModel.addRow(new Object[]{
                clinician.getClinicianId() != null ? clinician.getClinicianId() : clinician.getId(),
                clinician.getFirstName(),
                clinician.getLastName(),
                clinician.getTitle(),
                clinician.getSpeciality(),
                clinician.getEmail(),
                clinician.getPhoneNumber(),
                clinicianType
            });
        }
    }
    
    public int getSelectedRow() {
        // Check which table has selection
        if (topStaffTable.getSelectedRow() >= 0) {
            return topStaffTable.getSelectedRow();
        }
        return bottomClinicianTable.getSelectedRow();
    }
    
    public String getSelectedStaffId() {
        // Check top table first
        int row = topStaffTable.getSelectedRow();
        if (row >= 0) {
            return (String) topStaffTableModel.getValueAt(row, 0);
        }
        // Check bottom table
        row = bottomClinicianTable.getSelectedRow();
        if (row >= 0) {
            return (String) bottomClinicianTableModel.getValueAt(row, 0);
        }
        return null;
    }
    
    public void refreshTables() {
        topStaffTableModel.fireTableDataChanged();
        bottomClinicianTableModel.fireTableDataChanged();
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
        // Check top table first
        int row = topStaffTable.getSelectedRow();
        if (row >= 0) {
            return (String) topStaffTableModel.getValueAt(row, 3); // Role is column 3
        }
        // Check bottom table - get type from column 7
        row = bottomClinicianTable.getSelectedRow();
        if (row >= 0) {
            return (String) bottomClinicianTableModel.getValueAt(row, 7); // Type is column 7
        }
        return null;
    }
    
    // Check if selected item is from clinician table
    public boolean isSelectedFromClinicianTable() {
        return bottomClinicianTable.getSelectedRow() >= 0;
    }
    
    // Enable/disable buttons based on selection
    public void setEditRemoveButtonsEnabled(boolean enabled) {
        editStaffButton.setEnabled(enabled);
        removeStaffButton.setEnabled(enabled);
    }
    
    // Get staff tables
    public JTable getTopStaffTable() {
        return topStaffTable;
    }
    
    public JTable getBottomClinicianTable() {
        return bottomClinicianTable;
    }
    
    // Legacy method for backward compatibility
    public JTable getStaffTable() {
        return topStaffTable;
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

