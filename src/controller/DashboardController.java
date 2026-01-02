package controller;

import model.*;
import repository.PatientRepository;
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
                mainView.dispose();
                
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
    
    // handles what buttons each role can see - had to figure out the permissions for each role
    public void applyPermissions(String role) {
        mainView.getDashboardButton().setVisible(false);
        mainView.getAppointmentsButton().setVisible(false);
        mainView.getMedicalRecordsButton().setVisible(false);
        mainView.getManageStaffButton().setVisible(false);
        mainView.getManagePatientsButton().setVisible(false);
        
        clearButtonSelections();
        
        if ("Developer".equalsIgnoreCase(role)) {
            // developer gets access to everything for testing
            mainView.getDashboardButton().setVisible(true);
            mainView.getAppointmentsButton().setVisible(true);
            mainView.getMedicalRecordsButton().setVisible(true);
            mainView.getManageStaffButton().setVisible(true);
            mainView.getManagePatientsButton().setVisible(true);
        } else if ("GP".equalsIgnoreCase(role) || "Specialist".equalsIgnoreCase(role)) {
            mainView.getAppointmentsButton().setVisible(true);
            mainView.getMedicalRecordsButton().setVisible(true);
        } else if ("Nurse".equalsIgnoreCase(role)) {
            mainView.getMedicalRecordsButton().setVisible(true);
        } else if ("Admin".equalsIgnoreCase(role) || role.toLowerCase().contains("admin")) {
            mainView.getManageStaffButton().setVisible(true);
            mainView.getManagePatientsButton().setVisible(true);
        } else if ("Receptionist".equalsIgnoreCase(role)) {
            mainView.getManagePatientsButton().setVisible(true);
            mainView.getAppointmentsButton().setVisible(true);
        }
        
        mainView.getDashboardButton().setVisible(true);
        
        String defaultCard = determineDefaultCard(role);
        mainView.showCard(defaultCard);
        refreshDefaultCard(defaultCard);
    }
    
    // decides which page to show first when user logs in
    private String determineDefaultCard(String role) {
        if ("Nurse".equalsIgnoreCase(role)) {
            return "Medical Records";
        } else if ("Admin".equalsIgnoreCase(role) || role.toLowerCase().contains("admin")) {
            return "Manage Staff";
        } else if ("Receptionist".equalsIgnoreCase(role)) {
            return "Manage Patients";
        } else {
            return "Dashboard";
        }
    }
    
    private void refreshDefaultCard(String cardName) {
        if ("Dashboard".equals(cardName)) {
            showTodayAppointments();
        } else if ("Manage Staff".equals(cardName) && staffManagementController != null) {
            staffManagementController.loadStaffTable();
        } else if ("Manage Patients".equals(cardName) && patientManagementController != null) {
            patientManagementController.loadPatients();
        }
    }
    
    private void clearButtonSelections() {
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
            if (staffManagementController != null) {
                staffManagementController.loadStaffTable();
            }
        });
        mainView.getManagePatientsButton().addActionListener(e -> {
            mainView.showCard("Manage Patients");
            if (patientManagementController != null) {
                patientManagementController.loadPatients();
            }
        });
        
        mainView.showCard("Dashboard");
    }
    
    private void setupDashboard() {
        showTodayAppointments();
        
        dashboardPanel.getBtnSearch().addActionListener(e -> filterDashboardAppointments());
        dashboardPanel.getTxtSearchID().addActionListener(e -> filterDashboardAppointments());
        dashboardPanel.getTxtDateFilter().addActionListener(e -> filterDashboardAppointments());
        dashboardPanel.getBtnShowToday().addActionListener(e -> showTodayAppointments());
        dashboardPanel.getBtnUpdateStatus().addActionListener(e -> updateStatus());
        dashboardPanel.getBtnReschedule().addActionListener(e -> rescheduleAppointment());
        dashboardPanel.getRefreshButton().addActionListener(e -> showTodayAppointments());
        
        dashboardPanel.getAppointmentsTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = dashboardPanel.getAppointmentsTable().getSelectedRow() >= 0;
                dashboardPanel.setUpdateStatusButtonEnabled(hasSelection);
                dashboardPanel.setRescheduleButtonEnabled(hasSelection);
            }
        });
    }
    
    private void setupMedicalRecords() {
        medicalRecordController = new MedicalRecordController(
            medicalRecordPanel,
            patientRepository,
            appointmentRepository,
            prescriptionRepository,
            clinicianRepository,
            facilityRepository,
            clinician
        );
    }
    
    private void setupStaffManagement() {
        staffManagementController = new StaffManagementController(
            staffManagementPanel,
            staffRepository,
            clinicianRepository
        );
    }
    
    private void setupPatientManagement() {
        patientManagementController = new PatientManagementController(
            patientManagementPanel,
            patientRepository
        );
    }
    
    private void showTodayAppointments() {
        String today = "2025-12-26";
        dashboardPanel.getTxtDateFilter().setText(today);
        filterDashboardAppointments();
    }
    
    private void filterDashboardAppointments() {
        dashboardPanel.clearAppointments();
        
        String clinicianId = clinician.getClinicianId();
        if (clinicianId == null) {
            clinicianId = clinician.getId();
        }
        
        String searchID = dashboardPanel.getTxtSearchID().getText().trim().toLowerCase();
        String filterDate = dashboardPanel.getTxtDateFilter().getText().trim();
        
        if (filterDate.isEmpty()) {
            filterDate = "2025-12-26";
            dashboardPanel.getTxtDateFilter().setText(filterDate);
        }
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (!clinicianId.equals(appointment.getClinicianId())) {
                continue;
            }
            
            if (!filterDate.equals(appointment.getAppointmentDate())) {
                continue;
            }
            
            if (!searchID.isEmpty() && !appointment.getId().toLowerCase().contains(searchID)) {
                continue;
            }
            
            Patient patient = patientRepository.findById(appointment.getPatientId());
            String patientName = patient != null ? patient.getFullName() : "Unknown";
            
            Clinician apptClinician = clinicianRepository.findById(appointment.getClinicianId());
            String clinicianName = apptClinician != null ? apptClinician.getFullName() : appointment.getClinicianId();
            
            String facilityName = appointment.getFacilityId();
            if (facilityRepository != null) {
                Facility facility = facilityRepository.findById(appointment.getFacilityId());
                if (facility != null) {
                    facilityName = facility.getFacilityName();
                }
            }
            
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
        
        dashboardPanel.setUpdateStatusButtonEnabled(false);
        dashboardPanel.setRescheduleButtonEnabled(false);
    }
    
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
            
            appointmentRepository.updateAppointment(appointment);
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
            
            appointment.setAppointmentDate(newDate);
            appointment.setAppointmentTime(newTime);
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            appointmentRepository.updateAppointment(appointment);
            filterDashboardAppointments();
            
            javax.swing.JOptionPane.showMessageDialog(dashboardPanel, 
                "Appointment rescheduled successfully!", 
                "Success", 
                javax.swing.JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void loadDashboardAppointments() {
        showTodayAppointments();
    }
    
    public AppointmentPanel getAppointmentPanel() {
        return appointmentPanel;
    }
}

