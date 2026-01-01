package controller;

import model.*;
import repository.ReferralRepository;
import repository.StaffRepository;
import view.*;

public class DashboardController {
    
    private final GPDashboard mainView;
    private final DashboardPanel dashboardPanel;
    private final AppointmentPanel appointmentPanel;
    private final MedicalRecordPanel medicalRecordPanel;
    private final StaffManagementPanel staffManagementPanel;
    private final PatientManagementPanel patientManagementPanel;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final ReferralRepository referralRepository;
    private final StaffRepository staffRepository;
    private final Clinician clinician;
    private final String userRole;
    private MedicalRecordController medicalRecordController;
    private StaffManagementController staffManagementController;
    private PatientManagementController patientManagementController;
    
    public DashboardController(GPDashboard mainView,
                              AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              ClinicianRepository clinicianRepository,
                              FacilityRepository facilityRepository,
                              PrescriptionRepository prescriptionRepository,
                              ReferralRepository referralRepository,
                              StaffRepository staffRepository,
                              String role) {
        this.mainView = mainView;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.referralRepository = referralRepository;
        this.staffRepository = staffRepository;
        this.clinician = mainView.getClinician();
        this.userRole = role;
        
        this.dashboardPanel = new DashboardPanel();
        this.appointmentPanel = new AppointmentPanel();
        this.medicalRecordPanel = new MedicalRecordPanel();
        this.staffManagementPanel = new StaffManagementPanel();
        this.patientManagementPanel = new PatientManagementPanel();
        
        setupNavigation();
        setupDashboard();
        setupMedicalRecords();
        setupStaffManagement();
        setupPatientManagement();
        setupLogout();
        
        // Apply role-based permissions
        applyPermissions(role);
    }
    
    private void setupLogout() {
        mainView.getLogoutButton().addActionListener(e -> {
            int confirm = javax.swing.JOptionPane.showConfirmDialog(
                mainView,
                "Are you sure you want to logout?",
                "Confirm Logout",
                javax.swing.JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == javax.swing.JOptionPane.YES_OPTION) {
                // Close current dashboard
                mainView.dispose();
                
                // Return to LoginView
                view.LoginView loginView = new view.LoginView();
                controller.LoginController loginController = new controller.LoginController(
                    loginView,
                    patientRepository,
                    staffRepository,
                    clinicianRepository,
                    appointmentRepository,
                    prescriptionRepository,
                    facilityRepository,
                    referralRepository
                );
                loginView.setVisible(true);
            }
        });
    }
    
    /**
     * Apply role-based permissions to show/hide buttons.
     * Developer: Show ALL buttons (Super User).
     * GP / Specialist: Show btnAppointments, btnMedicalRecords. Hide others.
     * Nurse: Show btnMedicalRecords only. Hide others.
     * Admin: Show btnStaffManagement, btnPatientManagement. Hide others.
     * Receptionist: Show btnPatientManagement, btnAppointments. Hide others.
     */
    public void applyPermissions(String role) {
        // Hide all buttons first and clear selections
        mainView.getDashboardButton().setVisible(false);
        mainView.getAppointmentsButton().setVisible(false);
        mainView.getMedicalRecordsButton().setVisible(false);
        mainView.getManageStaffButton().setVisible(false);
        mainView.getManagePatientsButton().setVisible(false);
        
        // Clear any active selections on hidden buttons
        clearButtonSelections();
        
        // Apply permissions based on role
        if ("Developer".equalsIgnoreCase(role)) {
            // Developer: Show ALL buttons (Super User)
            mainView.getDashboardButton().setVisible(true);
            mainView.getAppointmentsButton().setVisible(true);
            mainView.getMedicalRecordsButton().setVisible(true);
            mainView.getManageStaffButton().setVisible(true);
            mainView.getManagePatientsButton().setVisible(true);
        } else if ("GP".equalsIgnoreCase(role) || "Specialist".equalsIgnoreCase(role)) {
            // GP / Specialist: Show btnAppointments, btnMedicalRecords
            mainView.getAppointmentsButton().setVisible(true);
            mainView.getMedicalRecordsButton().setVisible(true);
        } else if ("Nurse".equalsIgnoreCase(role)) {
            // Nurse: Show btnMedicalRecords only
            mainView.getMedicalRecordsButton().setVisible(true);
        } else if ("Admin".equalsIgnoreCase(role) || role.toLowerCase().contains("admin")) {
            // Admin: Show btnStaffManagement, btnPatientManagement
            mainView.getManageStaffButton().setVisible(true);
            mainView.getManagePatientsButton().setVisible(true);
        } else if ("Receptionist".equalsIgnoreCase(role)) {
            // Receptionist: Show btnPatientManagement, btnAppointments
            mainView.getManagePatientsButton().setVisible(true);
            mainView.getAppointmentsButton().setVisible(true);
        }
        
        // Always show Dashboard button
        mainView.getDashboardButton().setVisible(true);
        
        // Determine default landing page based on role
        String defaultCard = determineDefaultCard(role);
        mainView.showCard(defaultCard);
        
        // Refresh data for the default card
        refreshDefaultCard(defaultCard);
    }
    
    /**
     * Determine the default landing card for each role.
     * GP/Specialist/Developer: Default to "Dashboard"
     * Nurse: Default to "Medical Records"
     * Admin: Default to "Manage Staff"
     * Receptionist: Default to "Manage Patients"
     */
    private String determineDefaultCard(String role) {
        if ("Nurse".equalsIgnoreCase(role)) {
            return "Medical Records";
        } else if ("Admin".equalsIgnoreCase(role) || role.toLowerCase().contains("admin")) {
            return "Manage Staff";
        } else if ("Receptionist".equalsIgnoreCase(role)) {
            return "Manage Patients";
        } else {
            // GP, Specialist, Developer default to Dashboard
            return "Dashboard";
        }
    }
    
    /**
     * Refresh data when showing default card based on role.
     */
    private void refreshDefaultCard(String cardName) {
        if ("Dashboard".equals(cardName)) {
            showTodayAppointments();
        } else if ("Manage Staff".equals(cardName) && staffManagementController != null) {
            staffManagementController.loadStaffTable();
        } else if ("Manage Patients".equals(cardName) && patientManagementController != null) {
            patientManagementController.loadPatients();
        }
        // Medical Records doesn't need refresh on load
    }
    
    /**
     * Clear any active selections on buttons to prevent UI glitches.
     */
    private void clearButtonSelections() {
        // Clear any button focus or selection states
        mainView.getAppointmentsButton().setSelected(false);
        mainView.getMedicalRecordsButton().setSelected(false);
        mainView.getManageStaffButton().setSelected(false);
        mainView.getManagePatientsButton().setSelected(false);
    }
    
    private void setupNavigation() {
        mainView.addCard("Dashboard", dashboardPanel);
        mainView.addCard("Appointments", appointmentPanel);
        mainView.addCard("Medical Records", medicalRecordPanel);
        mainView.addCard("Manage Staff", staffManagementPanel);
        mainView.addCard("Manage Patients", patientManagementPanel);
        
        mainView.getDashboardButton().addActionListener(e -> {
            mainView.showCard("Dashboard");
            showTodayAppointments();
        });
        mainView.getAppointmentsButton().addActionListener(e -> {
            mainView.showCard("Appointments");
        });
        mainView.getMedicalRecordsButton().addActionListener(e -> {
            mainView.showCard("Medical Records");
        });
        mainView.getManageStaffButton().addActionListener(e -> {
            mainView.showCard("Manage Staff");
            // Refresh staff table when navigating to this panel
            if (staffManagementController != null) {
                staffManagementController.loadStaffTable();
            }
        });
        mainView.getManagePatientsButton().addActionListener(e -> {
            mainView.showCard("Manage Patients");
            // Refresh patient table when navigating to this panel
            if (patientManagementController != null) {
                patientManagementController.loadPatients();
            }
        });
        
        mainView.showCard("Dashboard");
    }
    
    private void setupDashboard() {
        // On startup: filter for today's date (2025-12-26) and populate dashboard table
        showTodayAppointments();
        
        // Search button listener
        dashboardPanel.getBtnSearch().addActionListener(e -> filterDashboardAppointments());
        
        // Search ID listener (Enter key)
        dashboardPanel.getTxtSearchID().addActionListener(e -> filterDashboardAppointments());
        
        // Date filter listener (Enter key)
        dashboardPanel.getTxtDateFilter().addActionListener(e -> filterDashboardAppointments());
        
        // Show Today button listener
        dashboardPanel.getBtnShowToday().addActionListener(e -> showTodayAppointments());
        
        // Update Status button listener
        dashboardPanel.getBtnUpdateStatus().addActionListener(e -> updateStatus());
        
        // Reschedule button listener
        dashboardPanel.getBtnReschedule().addActionListener(e -> rescheduleAppointment());
        
        // Refresh button listener
        dashboardPanel.getRefreshButton().addActionListener(e -> showTodayAppointments());
        
        // Enable/disable buttons based on selection
        dashboardPanel.getAppointmentsTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = dashboardPanel.getAppointmentsTable().getSelectedRow() >= 0;
                dashboardPanel.setUpdateStatusButtonEnabled(hasSelection);
                dashboardPanel.setRescheduleButtonEnabled(hasSelection);
            }
        });
    }
    
    private void setupMedicalRecords() {
        // Create medical record controller
        medicalRecordController = new MedicalRecordController(
            medicalRecordPanel,
            patientRepository,
            appointmentRepository,
            prescriptionRepository,
            clinicianRepository,
            facilityRepository,
            referralRepository,
            clinician
        );
    }
    
    private void setupStaffManagement() {
        // Create staff management controller
        staffManagementController = new StaffManagementController(
            staffManagementPanel,
            staffRepository,
            clinicianRepository
        );
    }
    
    private void setupPatientManagement() {
        // Create patient management controller
        patientManagementController = new PatientManagementController(
            patientManagementPanel,
            patientRepository
        );
    }
    
    // Show today's appointments (2025-12-26) for this clinician
    private void showTodayAppointments() {
        String today = "2025-12-26";
        dashboardPanel.getTxtDateFilter().setText(today);
        filterDashboardAppointments();
    }
    
    // Filter appointments for dashboard based on search ID and date filter
    private void filterDashboardAppointments() {
        dashboardPanel.clearAppointments();
        
        String clinicianId = clinician.getClinicianId();
        if (clinicianId == null) {
            clinicianId = clinician.getId();
        }
        
        String searchID = dashboardPanel.getTxtSearchID().getText().trim().toLowerCase();
        String filterDate = dashboardPanel.getTxtDateFilter().getText().trim();
        
        // If no date filter, default to today
        if (filterDate.isEmpty()) {
            filterDate = "2025-12-26";
            dashboardPanel.getTxtDateFilter().setText(filterDate);
        }
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            // Filter by clinician
            if (!clinicianId.equals(appointment.getClinicianId())) {
                continue;
            }
            
            // Filter by date
            if (!filterDate.equals(appointment.getAppointmentDate())) {
                continue;
            }
            
            // Filter by search ID if specified
            if (!searchID.isEmpty() && !appointment.getId().toLowerCase().contains(searchID)) {
                continue;
            }
            
            // Get patient name
            Patient patient = patientRepository.findById(appointment.getPatientId());
            String patientName = patient != null ? patient.getFullName() : "Unknown";
            
            // Get clinician name
            Clinician apptClinician = clinicianRepository.findById(appointment.getClinicianId());
            String clinicianName = apptClinician != null ? apptClinician.getFullName() : appointment.getClinicianId();
            
            // Get facility name
            String facilityName = appointment.getFacilityId();
            if (facilityRepository != null) {
                Facility facility = facilityRepository.findById(appointment.getFacilityId());
                if (facility != null) {
                    facilityName = facility.getFacilityName();
                }
            }
            
            // Add row: ID, Date, Time, Patient Name, Clinician Name, Facility, Reason, Status
            dashboardPanel.addAppointmentRow(
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                patientName,
                clinicianName,
                facilityName,
                appointment.getReasonForVisit(),
                appointment.getStatus()
            );
        }
        
        // Disable buttons after refresh
        dashboardPanel.setUpdateStatusButtonEnabled(false);
        dashboardPanel.setRescheduleButtonEnabled(false);
    }
    
    // Update status handler
    private void updateStatus() {
        String appointmentId = dashboardPanel.getSelectedAppointmentId();
        if (appointmentId == null) {
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Please select an appointment to update status.", 
                "No Selection", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Appointment not found.", 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show dialog to select new status
        String[] statusOptions = {"Scheduled", "Completed", "Cancelled", "No Show"};
        String currentStatus = appointment.getStatus();
        
        String newStatus = (String) javax.swing.JOptionPane.showInputDialog(
            dashboardPanel,
            "Current Status: " + currentStatus + "\n\nSelect new status:",
            "Update Appointment Status",
            javax.swing.JOptionPane.QUESTION_MESSAGE,
            null,
            statusOptions,
            currentStatus
        );
        
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            appointment.setStatus(newStatus);
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            // Update appointment in repository
            appointmentRepository.updateAppointment(appointment);
            
            // Refresh the dashboard table view
            filterDashboardAppointments();
            
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Appointment status updated to: " + newStatus, 
                "Success", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Reschedule appointment - opens JOptionPane for New Date/Time
    private void rescheduleAppointment() {
        String appointmentId = dashboardPanel.getSelectedAppointmentId();
        if (appointmentId == null) {
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Please select an appointment to reschedule.", 
                "No Selection", 
                javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Appointment not found.", 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open JOptionPane asking for new date and time
        javax.swing.JPanel panel = new javax.swing.JPanel(new java.awt.GridBagLayout());
        java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
        gbc.insets = new java.awt.Insets(5, 5, 5, 5);
        gbc.anchor = java.awt.GridBagConstraints.WEST;
        gbc.fill = java.awt.GridBagConstraints.HORIZONTAL;
        
        javax.swing.JTextField dateField = new javax.swing.JTextField(appointment.getAppointmentDate(), 15);
        javax.swing.JTextField timeField = new javax.swing.JTextField(appointment.getAppointmentTime(), 15);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("New Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new javax.swing.JLabel("New Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(timeField, gbc);
        
        int result = javax.swing.JOptionPane.showConfirmDialog(
            dashboardPanel,
            panel,
            "Reschedule Appointment",
            javax.swing.JOptionPane.OK_CANCEL_OPTION,
            javax.swing.JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == javax.swing.JOptionPane.OK_OPTION) {
            String newDate = dateField.getText().trim();
            String newTime = timeField.getText().trim();
            
            if (newDate.isEmpty() || newTime.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                    "Date and time are required.", 
                    "Validation Error", 
                    javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update the Appointment object
            appointment.setAppointmentDate(newDate);
            appointment.setAppointmentTime(newTime);
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            // Update in repository (updates CSV)
            appointmentRepository.updateAppointment(appointment);
            
            // Refresh the dashboard table view
            filterDashboardAppointments();
            
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Appointment rescheduled successfully!", 
                "Success", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Load appointments for dashboard - shows only this clinician's appointments (legacy method for refresh)
    private void loadDashboardAppointments() {
        showTodayAppointments();
    }
    
    // Getter for appointment panel (for AppointmentController)
    public AppointmentPanel getAppointmentPanel() {
        return appointmentPanel;
    }
}

