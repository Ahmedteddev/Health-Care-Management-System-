package controller;

import model.*;
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
    
    private List<Staff> allStaffList = new ArrayList<>();
    private List<Staff> adminStaffList = new ArrayList<>();
    private List<Clinician> clinicianList = new ArrayList<>();
    
    private void bind() {
        view.getAddAdminStaffButton().addActionListener(new AddStaffListener());
        view.getAddClinicianButton().addActionListener(new AddClinicianListener());
        view.getEditStaffButton().addActionListener(new EditStaffListener());
        view.getRemoveStaffButton().addActionListener(new RemoveStaffListener());
        view.getSearchButton().addActionListener(new SearchFilterListener());
        
        view.getSearchField().addActionListener(new SearchFilterListener());
        
        // Add listeners to both tables
        view.getTopStaffTable().getSelectionModel().addListSelectionListener(new StaffSelectionListener());
        view.getBottomClinicianTable().getSelectionModel().addListSelectionListener(new StaffSelectionListener());
    }
    
    // Loads staff into two separate tables
    // Using 'instanceof' to figure out which table the person belongs to
    public void loadStaffTable() {
        allStaffList.clear();
        adminStaffList.clear();
        clinicianList.clear();
        
        // Get all staff from repository
        allStaffList.addAll(staffRepository.getAllStaff());
        allStaffList.addAll(clinicianRepository.getAll());
        
        // Loop through the staff list and separate them
        for (Staff staff : allStaffList) {
            // If an object is an instanceof Clinician, send it to the bottom table
            if (staff instanceof Clinician) {
                clinicianList.add((Clinician) staff);
            } else {
                // Otherwise, send it to the top table (Administrative Staff)
                adminStaffList.add(staff);
            }
        }
        
        // Update both tables
        view.setAdminStaff(adminStaffList);
        view.setClinicians(clinicianList);
    }
    
    private void filterStaff() {
        String searchTerm = view.getSearchField().getText().toLowerCase().trim();
        String selectedRole = (String) view.getFilterComboBox().getSelectedItem();
        
        List<Staff> filteredAdminStaffList = new ArrayList<>();
        List<Clinician> filteredClinicianList = new ArrayList<>();
        
        // Filter Administrative Staff
        for (Staff staff : adminStaffList) {
            boolean roleMatches = false;
            if ("All".equals(selectedRole)) {
                roleMatches = true;
            } else {
                String staffRole = staff.getRole();
                if (staffRole != null && selectedRole.equals(staffRole)) {
                    roleMatches = true;
                }
            }
            
            if (!roleMatches) {
                continue;
            }
            
            if (searchTerm.isEmpty()) {
                filteredAdminStaffList.add(staff);
            } else {
                String staffId = staff.getStaffId() != null ? staff.getStaffId().toLowerCase() : "";
                String firstName = staff.getFirstName() != null ? staff.getFirstName().toLowerCase() : "";
                String lastName = staff.getLastName() != null ? staff.getLastName().toLowerCase() : "";
                String email = staff.getEmail() != null ? staff.getEmail().toLowerCase() : "";
                
                if (staffId.contains(searchTerm) || 
                    firstName.contains(searchTerm) || 
                    lastName.contains(searchTerm) ||
                    email.contains(searchTerm)) {
                    filteredAdminStaffList.add(staff);
                }
            }
        }
        
        // Filter Clinicians
        for (Clinician clinician : clinicianList) {
            boolean roleMatches = false;
            if ("All".equals(selectedRole)) {
                roleMatches = true;
            } else {
                // Check if clinician type matches
                String clinicianType = "Clinician";
                if (clinician instanceof model.GP) {
                    clinicianType = "GP";
                } else if (clinician instanceof model.Nurse) {
                    clinicianType = "Nurse";
                } else if (clinician instanceof model.Specialist) {
                    clinicianType = "Consultant";
                }
                
                if (selectedRole.equals(clinicianType) || 
                    (selectedRole.equals("Nurse") && clinicianType.equals("Nurse")) ||
                    (selectedRole.equals("Consultant") && clinicianType.equals("Consultant"))) {
                    roleMatches = true;
                }
            }
            
            if (!roleMatches) {
                continue;
            }
            
            if (searchTerm.isEmpty()) {
                filteredClinicianList.add(clinician);
            } else {
                String clinicianId = clinician.getClinicianId() != null ? clinician.getClinicianId().toLowerCase() : "";
                String firstName = clinician.getFirstName() != null ? clinician.getFirstName().toLowerCase() : "";
                String lastName = clinician.getLastName() != null ? clinician.getLastName().toLowerCase() : "";
                String email = clinician.getEmail() != null ? clinician.getEmail().toLowerCase() : "";
                
                if (clinicianId.contains(searchTerm) || 
                    firstName.contains(searchTerm) || 
                    lastName.contains(searchTerm) ||
                    email.contains(searchTerm)) {
                    filteredClinicianList.add(clinician);
                }
            }
        }
        
        // Update both tables with filtered results
        view.setAdminStaff(filteredAdminStaffList);
        view.setClinicians(filteredClinicianList);
    }
    
    private class SearchFilterListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            filterStaff();
        }
    }
    
    private class AddStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Add New Admin/Staff Member");
            
            String newId = generateStaffId();
            dialog.setStaffId(newId);
            dialog.setStaffIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                if (dialog.getStaffData().getFirstName().isEmpty() || 
                    dialog.getStaffData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Staff newStaff = dialog.getStaffData();
                newStaff.setStaffId(newId);
                
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
                
                staffRepository.addStaff(newStaff);
                loadStaffTable();
                filterStaff();
                
                dialog.dispose();
                System.out.println("[Success]: Staff member added successfully!");
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    private class AddClinicianListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
            ClinicianFormDialog dialog = new ClinicianFormDialog(parentFrame, "Add New Clinician");
            
            String newId = generateClinicianId();
            dialog.setClinicianId(newId);
            dialog.setClinicianIdEditable(false);
            
            dialog.getSaveButton().addActionListener(ev -> {
                if (dialog.getClinicianData().getFirstName().isEmpty() || 
                    dialog.getClinicianData().getLastName().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Clinician newClinician = dialog.getClinicianData();
                newClinician.setClinicianId(newId);
                
                if (newClinician.getEmploymentStatus() == null || newClinician.getEmploymentStatus().isEmpty()) {
                    newClinician.setEmploymentStatus("Full-time");
                }
                if (newClinician.getStartDate() == null || newClinician.getStartDate().isEmpty()) {
                    newClinician.setStartDate(LocalDate.now().toString());
                }
                
                // clinicians need to be added to both repositories since they extend Staff
                //staffRepository.addStaff(newClinician);
                clinicianRepository.addAndAppend(newClinician);
                
                loadStaffTable();
                filterStaff();
                
                dialog.dispose();
                System.out.println("[Success]: Clinician added successfully!");
            });
            
            dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
            dialog.setVisible(true);
        }
    }
    
    private class EditStaffListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String staffId = view.getSelectedStaffId();
            if (staffId == null) {
                JOptionPane.showMessageDialog(view, "Please select a staff member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String role = view.getSelectedRole();
            boolean isFromClinicianTable = view.isSelectedFromClinicianTable();
            
            // clinicians (GP/Nurse/Specialist) need special handling since they're in both repositories
            // Using 'instanceof' to figure out which table the person belongs to
            if (isFromClinicianTable || "GP".equals(role) || "Nurse".equals(role) || "Specialist".equals(role) || role != null && (role.contains("Consultant") || role.contains("Nurse"))) {
                Staff staff = staffRepository.findStaffById(staffId);
                Clinician foundClinician = clinicianRepository.findById(staffId);
                
                if (foundClinician == null && staff instanceof Clinician) {
                    foundClinician = (Clinician) staff;
                }
                
                if (foundClinician == null) {
                    foundClinician = clinicianRepository.findById(staffId);
                }
                
                final Clinician clinician = foundClinician;
                
                if (clinician == null) {
                    JOptionPane.showMessageDialog(view, "Clinician not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
                ClinicianFormDialog dialog = new ClinicianFormDialog(parentFrame, "Edit Clinician");
                dialog.setClinicianData(clinician);
                dialog.setClinicianIdEditable(false);
                
                dialog.getSaveButton().addActionListener(ev -> {
                    if (dialog.getClinicianData().getFirstName().isEmpty() || 
                        dialog.getClinicianData().getLastName().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Clinician updatedClinician = dialog.getClinicianData();
                    updatedClinician.setClinicianId(staffId);
                    
                    updatedClinician.setEmploymentStatus(clinician.getEmploymentStatus());
                    if (updatedClinician.getStartDate() == null || updatedClinician.getStartDate().isEmpty()) {
                        updatedClinician.setStartDate(clinician.getStartDate());
                    }
                    
                    staffRepository.updateStaff(updatedClinician);
                    clinicianRepository.updateClinician(updatedClinician);
                    
                    loadStaffTable();
                    filterStaff();
                    
                    dialog.dispose();
                    System.out.println("[Success]: Clinician updated successfully!");
                });
                
                dialog.getCancelButton().addActionListener(ev -> dialog.dispose());
                dialog.setVisible(true);
            } else {
                Staff staff = staffRepository.findStaffById(staffId);
                if (staff == null) {
                    JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(view);
                StaffFormDialog dialog = new StaffFormDialog(parentFrame, "Edit Staff Member");
                dialog.setStaffData(staff);
                dialog.setStaffIdEditable(false);
                
                dialog.getSaveButton().addActionListener(ev -> {
                    if (dialog.getStaffData().getFirstName().isEmpty() || 
                        dialog.getStaffData().getLastName().isEmpty()) {
                        JOptionPane.showMessageDialog(dialog, "First name and last name are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    Staff updatedStaff = dialog.getStaffData();
                    updatedStaff.setStaffId(staffId);
                    
                    updatedStaff.setFacilityId(staff.getFacilityId());
                    updatedStaff.setEmploymentStatus(staff.getEmploymentStatus());
                    if (updatedStaff.getStartDate() == null || updatedStaff.getStartDate().isEmpty()) {
                        updatedStaff.setStartDate(staff.getStartDate());
                    }
                    updatedStaff.setLineManager(staff.getLineManager());
                    
                    staffRepository.updateStaff(updatedStaff);
                    loadStaffTable();
                    filterStaff();
                    
                    dialog.dispose();
                    System.out.println("[Success]: Staff member updated successfully!");
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
            
            String role = view.getSelectedRole();
            boolean isFromClinicianTable = view.isSelectedFromClinicianTable();
            Staff staff = staffRepository.findStaffById(staffId);
            Clinician clinician = clinicianRepository.findById(staffId);
            
            if (staff == null && clinician == null) {
                JOptionPane.showMessageDialog(view, "Staff member not found.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String name = staff != null ? staff.getFirstName() + " " + staff.getLastName() : 
                          clinician != null ? clinician.getFirstName() + " " + clinician.getLastName() : "Unknown";
            
            int result = JOptionPane.showConfirmDialog(
                view,
                "Are you sure you want to remove " + name + "?",
                "Confirm Staff Removal",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // need to delete from both repositories if it's a clinician
                // Using 'instanceof' to figure out which table the person belongs to
                if (isFromClinicianTable || "GP".equals(role) || "Nurse".equals(role) || "Specialist".equals(role) || role != null && (role.contains("Consultant") || role.contains("Nurse"))) {
                    if (clinician != null) {
                        clinicianRepository.remove(clinician);
                        clinicianRepository.saveAll();
                    }
                    if (staff != null) {
                        staffRepository.removeStaff(staff);
                    }
                } else {
                    if (staff != null) {
                        staffRepository.removeStaff(staff);
                    }
                }
                
                loadStaffTable();
                filterStaff();
                
                System.out.println("[Success]: Staff member removed successfully.");
            }
        }
    }
    
    private class StaffSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                // Check if either table has a selection
                boolean hasSelection = view.getTopStaffTable().getSelectedRow() >= 0 || 
                                      view.getBottomClinicianTable().getSelectedRow() >= 0;
                view.setEditRemoveButtonsEnabled(hasSelection);
                
                // Clear selection from the other table when one is selected
                if (view.getTopStaffTable().getSelectedRow() >= 0) {
                    view.getBottomClinicianTable().clearSelection();
                }
                if (view.getBottomClinicianTable().getSelectedRow() >= 0) {
                    view.getTopStaffTable().clearSelection();
                }
            }
        }
    }
    
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
    
    private String generateClinicianId() {
        return clinicianRepository.generateNewId();
    }
    
    public JTable getStaffTable() {
        return view.getTopStaffTable();
    }
}

