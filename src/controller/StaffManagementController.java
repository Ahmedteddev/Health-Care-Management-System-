package controller;

import model.Staff;
import repository.StaffRepository;
import view.StaffFormDialog;
import view.StaffManagementPanel;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class StaffManagementController {
    
    private final StaffManagementPanel view;
    private final StaffRepository staffRepository;
    
    public StaffManagementController(StaffManagementPanel view,
                                    StaffRepository staffRepository) {
        this.view = view;
        this.staffRepository = staffRepository;
        
        bind();
        loadStaffTable();
    }
    
    private void bind() {
        // Button listeners
        view.getAddStaffButton().addActionListener(new AddStaffListener());
        view.getEditStaffButton().addActionListener(new EditStaffListener());
        view.getRemoveStaffButton().addActionListener(new RemoveStaffListener());
        
        // Table selection listener
        view.getStaffTable().getSelectionModel().addListSelectionListener(new StaffSelectionListener());
    }
    
    // Load staff table (public for external refresh)
    public void loadStaffTable() {
        view.setStaff(staffRepository.getAllStaff());
    }
    
    // Add staff handler
    private class AddStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Add New Staff Member");
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                if (dialog.getStaffData().getFirstName().isEmpty() || 
                    dialog.getStaffData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Generate new staff ID
                String newId = generateStaffId();
                
                // Get staff data from dialog
                Staff newStaff = dialog.getStaffData();
                newStaff.setStaffId(newId);
                
                // Set default values for required fields
                if (newStaff.getFacilityId() == null || newStaff.getFacilityId().isEmpty()) {
                    newStaff.setFacilityId("");
                }
                if (newStaff.getEmploymentStatus() == null || newStaff.getEmploymentStatus().isEmpty()) {
                    newStaff.setEmploymentStatus("Full-time");
                }
                if (newStaff.getStartDate() == null || newStaff.getStartDate().isEmpty()) {
                    newStaff.setStartDate(LocalDate.now().toString());
                }
                if (newStaff.getLineManager() == null || newStaff.getLineManager().isEmpty()) {
                    newStaff.setLineManager("");
                }
                if (newStaff.getAccessLevel() == null || newStaff.getAccessLevel().isEmpty()) {
                    newStaff.setAccessLevel("Standard");
                }
                
                // Add to repository (saves to CSV)
                staffRepository.addStaff(newStaff);
                
                // Refresh table
                loadStaffTable();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Staff member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    // Edit staff handler
    private class EditStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String staffId = view.getSelectedStaffId();
            if (staffId == null) {
                JOptionPane.showMessageDialog(view, "Please select a staff member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Fetch the Staff object from the Repository
            Staff staff = staffRepository.findStaffById(staffId);
            if (staff == null) {
                JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Open StaffFormDialog pre-filled with that data
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Edit Staff Member");
            dialog.setStaffData(staff);
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                if (dialog.getStaffData().getFirstName().isEmpty() || 
                    dialog.getStaffData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get updated staff data from dialog
                Staff updatedStaff = dialog.getStaffData();
                updatedStaff.setStaffId(staffId); // Keep the same ID
                
                // Preserve other fields that aren't in the form
                updatedStaff.setFacilityId(staff.getFacilityId());
                updatedStaff.setEmploymentStatus(staff.getEmploymentStatus());
                updatedStaff.setStartDate(staff.getStartDate());
                updatedStaff.setLineManager(staff.getLineManager());
                updatedStaff.setAccessLevel(staff.getAccessLevel());
                
                // Update the object and call updateStaff
                staffRepository.updateStaff(updatedStaff);
                
                // Refresh table
                loadStaffTable();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Staff member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    // Remove staff handler
    private class RemoveStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String staffId = view.getSelectedStaffId();
            if (staffId == null) {
                JOptionPane.showMessageDialog(view, "Please select a staff member to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Staff staff = staffRepository.findStaffById(staffId);
            if (staff == null) {
                JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Standard confirmation dialog
            int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to remove " + staff.getFirstName() + " " + staff.getLastName() + "?",
                "Confirm Staff Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // Remove from repository
                staffRepository.removeStaff(staff);
                
                // Refresh table
                loadStaffTable();
                
                JOptionPane.showMessageDialog(view, "Staff member removed successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // Table selection listener
    private class StaffSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedRow() >= 0;
                view.setEditRemoveButtonsEnabled(hasSelection);
            }
        }
    }
    
    // Helper method to generate staff ID
    private String generateStaffId() {
        int max = 0;
        for (Staff s : staffRepository.getAllStaff()) {
            String id = s.getStaffId();
            if (id != null && id.startsWith("ST")) {
                try {
                    int num = Integer.parseInt(id.substring(2));
                    if (num > max) max = num;
                } catch (Exception ignore) {}
            }
        }
        return String.format("ST%03d", max + 1);
    }
    
    // Getter for staff table (for external access if needed)
    public JTable getStaffTable() {
        return view.getStaffTable();
    }
}

