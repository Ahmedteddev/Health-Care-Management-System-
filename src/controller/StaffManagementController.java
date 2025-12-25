package controller;

import model.Staff;
import model.Clinician;
import repository.StaffRepository;
import model.ClinicianRepository;
import view.StaffFormDialog;
import view.ClinicianFormDialog;
import view.StaffManagementPanel;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffManagementController {
    
    private final StaffManagementPanel view;
    private final StaffRepository staffRepository;
    private final ClinicianRepository clinicianRepository;
    
    public StaffManagementController(StaffManagementPanel view,
                                    StaffRepository staffRepository,
                                    ClinicianRepository clinicianRepository) {
        this.view = view;
        this.staffRepository = staffRepository;
        this.clinicianRepository = clinicianRepository;
        
        bind();
        loadStaffTable();
    }
    
    private List<Staff> allStaffList = new ArrayList<>(); // Store all staff for filtering
    
    private void bind() {
        // Button listeners
        view.getAddAdminStaffButton().addActionListener(new AddStaffListener());
        view.getAddClinicianButton().addActionListener(new AddClinicianListener());
        view.getEditStaffButton().addActionListener(new EditStaffListener());
        view.getRemoveStaffButton().addActionListener(new RemoveStaffListener());
        view.getSearchButton().addActionListener(new SearchFilterListener());
        
        // Enter key in search field also triggers search
        view.getSearchField().addActionListener(new SearchFilterListener());
        
        // Table selection listener
        view.getStaffTable().getSelectionModel().addListSelectionListener(new StaffSelectionListener());
    }
    
    // Load staff table (public for external refresh) - combines Staff and Clinicians
    public void loadStaffTable() {
        allStaffList.clear();
        // Add all regular staff
        allStaffList.addAll(staffRepository.getAllStaff());
        // Add all clinicians (they extend Staff)
        allStaffList.addAll(clinicianRepository.getAll());
        view.setStaff(allStaffList);
    }
    
    // Filter staff by role and search term
    private void filterStaff() {
        String searchTerm = view.getSearchField().getText().toLowerCase().trim();
        String selectedRole = (String) view.getFilterComboBox().getSelectedItem();
        
        List<Staff> filteredList = new ArrayList<>();
        
        for (Staff staff : allStaffList) {
            // Filter by role
            boolean roleMatches = false;
            if ("All".equals(selectedRole)) {
                roleMatches = true;
            } else {
                String staffRole = staff.getRole();
                if (staffRole != null) {
                    // Check if role matches or contains the selected role
                    if (selectedRole.equals(staffRole) || 
                        (selectedRole.equals("Nurse") && staffRole.contains("Nurse")) ||
                        (selectedRole.equals("Consultant") && staffRole.contains("Consultant"))) {
                        roleMatches = true;
                    }
                }
            }
            
            if (!roleMatches) {
                continue;
            }
            
            // Filter by search term (searches in ID, First Name, Last Name, Email)
            if (searchTerm.isEmpty()) {
                filteredList.add(staff);
            } else {
                String staffId = staff.getStaffId() != null ? staff.getStaffId().toLowerCase() : "";
                String firstName = staff.getFirstName() != null ? staff.getFirstName().toLowerCase() : "";
                String lastName = staff.getLastName() != null ? staff.getLastName().toLowerCase() : "";
                String email = staff.getEmail() != null ? staff.getEmail().toLowerCase() : "";
                
                if (staffId.contains(searchTerm) || 
                    firstName.contains(searchTerm) || 
                    lastName.contains(searchTerm) ||
                    email.contains(searchTerm)) {
                    filteredList.add(staff);
                }
            }
        }
        
        view.setStaff(filteredList);
    }
    
    // Search and filter listener
    private class SearchFilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterStaff();
        }
    }
    
    // Add staff handler (for Admin/Staff only)
    private class AddStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Add New Admin/Staff Member");
            
            // Generate new staff ID
            String newId = generateStaffId();
            dialog.setStaffId(newId);
            dialog.setStaffIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                if (dialog.getStaffData().getFirstName().isEmpty() || 
                    dialog.getStaffData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
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
                
                // Add to StaffRepository only (saves to CSV)
                staffRepository.addStaff(newStaff);
                
                // Refresh table and apply current filter
                loadStaffTable();
                filterStaff();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Staff member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    // Add clinician handler
    private class AddClinicianListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            ClinicianFormDialog dialog = new ClinicianFormDialog(parentFrame, "Add New Clinician");
            
            // Generate new clinician ID
            String newId = generateClinicianId();
            dialog.setClinicianId(newId);
            dialog.setClinicianIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                // Validate required fields
                if (dialog.getClinicianData().getFirstName().isEmpty() || 
                    dialog.getClinicianData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Get clinician data from dialog
                Clinician newClinician = dialog.getClinicianData();
                newClinician.setClinicianId(newId);
                
                // Set default values for required fields
                if (newClinician.getEmploymentStatus() == null || newClinician.getEmploymentStatus().isEmpty()) {
                    newClinician.setEmploymentStatus("Full-time");
                }
                if (newClinician.getStartDate() == null || newClinician.getStartDate().isEmpty()) {
                    newClinician.setStartDate(LocalDate.now().toString());
                }
                
                // Call StaffRepository.getInstance().addStaff(clinicianObj)
                staffRepository.addStaff(newClinician);
                
                // Call ClinicianRepository.getInstance().addClinician(clinicianObj)
                clinicianRepository.addAndAppend(newClinician);
                
                // Refresh table and apply current filter
                loadStaffTable();
                filterStaff();
                
                dialog.dispose();
                JOptionPane.showMessageDialog(view, "Clinician added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
            
            // Check the role of the selected staff
            String role = view.getSelectedRole();
            
            // If "GP" or "Nurse", open ClinicianFormDialog with data from BOTH repositories
            if ("GP".equals(role) || "Nurse".equals(role) || role != null && (role.contains("Consultant") || role.contains("Nurse"))) {
                // Fetch from both repositories
                Staff staff = staffRepository.findStaffById(staffId);
                Clinician foundClinician = clinicianRepository.findById(staffId);
                
                if (foundClinician == null && staff instanceof Clinician) {
                    foundClinician = (Clinician) staff;
                }
                
                if (foundClinician == null) {
                    // Try to find by clinician ID
                    foundClinician = clinicianRepository.findById(staffId);
                }
                
                final Clinician clinician = foundClinician; // Make final for lambda
                
                if (clinician == null) {
                    JOptionPane.showMessageDialog(view, "Clinician not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Open ClinicianFormDialog pre-filled with data from BOTH repositories
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
                ClinicianFormDialog dialog = new ClinicianFormDialog(parentFrame, "Edit Clinician");
                dialog.setClinicianData(clinician);
                dialog.setClinicianIdEditable(false);
                
                dialog.getSaveButton().addActionListener(ev -> {
                    // Validate required fields
                    if (dialog.getClinicianData().getFirstName().isEmpty() || 
                        dialog.getClinicianData().getLastName().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Get updated clinician data from dialog
                    Clinician updatedClinician = dialog.getClinicianData();
                    updatedClinician.setClinicianId(staffId); // Keep the same ID
                    
                    // Preserve other fields that aren't in the form
                    updatedClinician.setEmploymentStatus(clinician.getEmploymentStatus());
                    if (updatedClinician.getStartDate() == null || updatedClinician.getStartDate().isEmpty()) {
                        updatedClinician.setStartDate(clinician.getStartDate());
                    }
                    
                    // Update both repositories if it's a clinician
                    staffRepository.updateStaff(updatedClinician);
                    clinicianRepository.updateClinician(updatedClinician);
                    
                    // Refresh table and apply current filter
                    loadStaffTable();
                    filterStaff();
                    
                    dialog.dispose();
                    JOptionPane.showMessageDialog(view, "Clinician updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
                
                dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
                dialog.setVisible(true);
            } else {
                // Otherwise, open the standard StaffFormDialog
                Staff staff = staffRepository.findStaffById(staffId);
                if (staff == null) {
                    JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Open StaffFormDialog pre-filled with that data
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
                StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Edit Staff Member");
                dialog.setStaffData(staff);
                dialog.setStaffIdEditable(false);
                
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
                    if (updatedStaff.getStartDate() == null || updatedStaff.getStartDate().isEmpty()) {
                        updatedStaff.setStartDate(staff.getStartDate());
                    }
                    updatedStaff.setLineManager(staff.getLineManager());
                    
                    // Update the object and call updateStaff
                    staffRepository.updateStaff(updatedStaff);
                    
                    // Refresh table and apply current filter
                    loadStaffTable();
                    filterStaff();
                    
                    dialog.dispose();
                    JOptionPane.showMessageDialog(view, "Staff member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                });
                
                dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
                dialog.setVisible(true);
            }
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
            
            // Check the role
            String role = view.getSelectedRole();
            Staff staff = staffRepository.findStaffById(staffId);
            Clinician clinician = clinicianRepository.findById(staffId);
            
            if (staff == null && clinician == null) {
                JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String name = staff != null ? staff.getFirstName() + " " + staff.getLastName() : 
                          clinician != null ? clinician.getFirstName() + " " + clinician.getLastName() : "Unknown";
            
            // Standard confirmation dialog
            int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to remove " + name + "?",
                "Confirm Staff Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // If the role is "GP" or "Nurse", call delete on both StaffRepository and ClinicianRepository
                if ("GP".equals(role) || "Nurse".equals(role) || role != null && (role.contains("Consultant") || role.contains("Nurse"))) {
                    if (clinician != null) {
                        clinicianRepository.remove(clinician);
                        clinicianRepository.saveAll();
                    }
                    if (staff != null) {
                        staffRepository.removeStaff(staff);
                    }
                } else {
                    // Otherwise, just call StaffRepository.delete()
                    if (staff != null) {
                        staffRepository.removeStaff(staff);
                    }
                }
                
                // Refresh table and apply current filter
                loadStaffTable();
                filterStaff();
                
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
    
    // Helper method to generate clinician ID
    private String generateClinicianId() {
        return clinicianRepository.generateNewId();
    }
    
    // Getter for staff table (for external access if needed)
    public JTable getStaffTable() {
        return view.getStaffTable();
    }
}

