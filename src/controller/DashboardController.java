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
                              StaffRepository staffRepository) {
        this.mainView = mainView;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.referralRepository = referralRepository;
        this.staffRepository = staffRepository;
        this.clinician = mainView.getClinician();
        
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

