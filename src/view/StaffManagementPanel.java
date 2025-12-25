package view;

import model.Staff;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class StaffManagementPanel extends JPanel {
    
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private JButton addStaffButton;
    private JButton editStaffButton;
    private JButton removeStaffButton;
    
    // Table column names
    private static final String[] COLUMN_NAMES = {
        "Staff ID", "First Name", "Last Name", "Role", "Department", "Email", "Phone"
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
        
        addStaffButton = new JButton("Add Staff");
        editStaffButton = new JButton("Edit Selected");
        removeStaffButton = new JButton("Remove Staff");
        
        editStaffButton.setEnabled(false);
        removeStaffButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new java.awt.BorderLayout(10, 10));
        setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table with scroll pane
        JScrollPane scrollPane = new JScrollPane(staffTable);
        add(scrollPane, java.awt.BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 5));
        buttonPanel.add(addStaffButton);
        buttonPanel.add(editStaffButton);
        buttonPanel.add(removeStaffButton);
        add(buttonPanel, java.awt.BorderLayout.SOUTH);
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
    public JButton getAddStaffButton() {
        return addStaffButton;
    }
    
    public JButton getEditStaffButton() {
        return editStaffButton;
    }
    
    public JButton getRemoveStaffButton() {
        return removeStaffButton;
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
}

