package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.awt.*;

public class GPController {
    
    private final GPDashboard mainView;
    private final DashboardPanel dashboardPanel;
    private final AppointmentPanel appointmentPanel;
    private final MedicalRecordPanel medicalRecordPanel;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final Clinician clinician;
    private MedicalRecordController medicalRecordController;
    
    public GPController(GPDashboard mainView,
                       AppointmentRepository appointmentRepository,
                       PatientRepository patientRepository,
                       ClinicianRepository clinicianRepository,
                       FacilityRepository facilityRepository,
                       PrescriptionRepository prescriptionRepository) {
        this.mainView = mainView;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.clinician = mainView.getClinician();
        
        this.dashboardPanel = new DashboardPanel();
        this.appointmentPanel = new AppointmentPanel();
        this.medicalRecordPanel = new MedicalRecordPanel();
        
        setupNavigation();
        setupDashboard();
        setupAppointments();
        setupMedicalRecords();
    }
    
    private void setupNavigation() {
        mainView.addCard("Dashboard", dashboardPanel);
        mainView.addCard("Appointments", appointmentPanel);
        mainView.addCard("Medical Records", medicalRecordPanel);
        
        mainView.getDashboardButton().addActionListener(e -> {
            mainView.showCard("Dashboard");
            loadDashboardAppointments();
        });
        mainView.getAppointmentsButton().addActionListener(e -> {
            mainView.showCard("Appointments");
            refreshAppointmentsTable();
        });
        mainView.getMedicalRecordsButton().addActionListener(e -> {
            mainView.showCard("Medical Records");
        });
        
        mainView.showCard("Dashboard");
    }
    
    private void setupDashboard() {
        loadDashboardAppointments();
        
        dashboardPanel.getRefreshButton().addActionListener(e -> loadDashboardAppointments());
    }
    
    private void setupAppointments() {
        refreshAppointmentsTable();
        
        // Enable/disable buttons based on selection
        appointmentPanel.getTable().getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = appointmentPanel.getSelectedRow() >= 0;
                    appointmentPanel.setRescheduleButtonEnabled(hasSelection);
                    appointmentPanel.setEditButtonEnabled(hasSelection);
                    appointmentPanel.setCancelButtonEnabled(hasSelection);
                }
            });
        
        // Search button listener
        appointmentPanel.getSearchButton().addActionListener(e -> filterAppointments());
        
        // Enter key in search field also triggers search
        appointmentPanel.getSearchField().addActionListener(e -> filterAppointments());
        
        // CRUD button listeners
        appointmentPanel.getBookNewButton().addActionListener(e -> bookNewAppointment());
        appointmentPanel.getRescheduleButton().addActionListener(e -> rescheduleAppointment());
        appointmentPanel.getEditButton().addActionListener(e -> editAppointment());
        appointmentPanel.getCancelButton().addActionListener(e -> cancelAppointment());
    }
    
    private void setupMedicalRecords() {
        // Create medical record controller
        medicalRecordController = new MedicalRecordController(
            medicalRecordPanel,
            patientRepository,
            appointmentRepository,
            prescriptionRepository,
            clinicianRepository,
            clinician
        );
    }
    
    // Load appointments for dashboard - shows only this clinician's appointments
    private void loadDashboardAppointments() {
        dashboardPanel.clearAppointments();
        
        String clinicianId = clinician.getClinicianId();
        if (clinicianId == null) {
            clinicianId = clinician.getId();
        }
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (clinicianId.equals(appointment.getClinicianId())) {
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
        }
    }
    
    // Refresh appointments table in Appointment Panel - shows all appointments
    private void refreshAppointmentsTable() {
        appointmentPanel.clearTable();
        
        for (Appointment appointment : appointmentRepository.getAll()) {
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
            appointmentPanel.addRow(new Object[]{
                appointment.getId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                patientName,
                clinicianName,
                facilityName,
                appointment.getReasonForVisit(),
                appointment.getStatus()
            });
        }
        
        // Disable buttons after refresh
        appointmentPanel.setRescheduleButtonEnabled(false);
        appointmentPanel.setEditButtonEnabled(false);
        appointmentPanel.setCancelButtonEnabled(false);
    }
    
    // Filter appointments based on search criteria
    private void filterAppointments() {
        String searchText = appointmentPanel.getSearchField().getText().toLowerCase();
        String filterType = (String) appointmentPanel.getFilterComboBox().getSelectedItem();
        
        appointmentPanel.clearTable();
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            boolean matches = false;
            
            if (filterType.equals("Patient ID")) {
                matches = appointment.getPatientId().toLowerCase().contains(searchText);
            } else if (filterType.equals("Patient Name")) {
                Patient patient = patientRepository.findById(appointment.getPatientId());
                if (patient != null) {
                    matches = patient.getFullName().toLowerCase().contains(searchText);
                }
            } else if (filterType.equals("Clinician ID")) {
                matches = appointment.getClinicianId().toLowerCase().contains(searchText);
            } else if (filterType.equals("Clinician Name")) {
                Clinician apptClinician = clinicianRepository.findById(appointment.getClinicianId());
                if (apptClinician != null) {
                    matches = apptClinician.getFullName().toLowerCase().contains(searchText);
                }
            }
            
            // If search is empty, show all; otherwise show only matches
            if (matches || searchText.isEmpty()) {
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
                
                appointmentPanel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    patientName,
                    clinicianName,
                    facilityName,
                    appointment.getReasonForVisit(),
                    appointment.getStatus()
                });
            }
        }
    }
    
    // Book new appointment dialog
    private void bookNewAppointment() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(appointmentPanel), "Book New Appointment", true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Patient dropdown
        JComboBox<String> patientCombo = new JComboBox<>();
        for (Patient p : patientRepository.getAll()) {
            patientCombo.addItem(p.getPatientId() + " - " + p.getFullName());
        }
        
        // Clinician dropdown
        JComboBox<String> clinicianCombo = new JComboBox<>();
        for (Clinician c : clinicianRepository.getAll()) {
            clinicianCombo.addItem(c.getClinicianId() + " - " + c.getFullName());
        }
        
        // Facility dropdown
        JComboBox<String> facilityCombo = new JComboBox<>();
        if (facilityRepository != null) {
            for (Facility f : facilityRepository.getAll()) {
                facilityCombo.addItem(f.getFacilityId() + " - " + f.getFacilityName());
            }
        }
        
        // Form fields
        JTextField dateField = new JTextField(20);
        JTextField timeField = new JTextField(20);
        JTextField reasonField = new JTextField(25);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled", "No Show"});
        
        // Layout form fields using GridBagLayout
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(patientCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Clinician:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(clinicianCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Facility:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(facilityCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(timeField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(reasonField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(statusCombo, gbc);
        
        // Buttons
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        // Save button action
        saveButton.addActionListener(e -> {
            String patientId = ((String) patientCombo.getSelectedItem()).split(" - ")[0];
            String clinicianId = ((String) clinicianCombo.getSelectedItem()).split(" - ")[0];
            String facilityId = ((String) facilityCombo.getSelectedItem()).split(" - ")[0];
            
            Appointment newAppt = new Appointment(
                appointmentRepository.generateNewId(),
                patientId,
                clinicianId,
                facilityId,
                dateField.getText(),
                timeField.getText(),
                "15",
                "Routine Consultation",
                (String) statusCombo.getSelectedItem(),
                reasonField.getText(),
                "",
                java.time.LocalDate.now().toString(),
                java.time.LocalDate.now().toString()
            );
            
            appointmentRepository.add(newAppt);
            refreshAppointmentsTable();
            loadDashboardAppointments();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setMinimumSize(new Dimension(450, 350));
        dialog.pack();
        dialog.setLocationRelativeTo(appointmentPanel);
        dialog.setVisible(true);
    }
    
    // Reschedule appointment - only changes date and time
    private void rescheduleAppointment() {
        String appointmentId = appointmentPanel.getSelectedAppointmentId();
        if (appointmentId == null) return;
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) return;
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(appointmentPanel), "Reschedule Appointment", true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField dateField = new JTextField(appointment.getAppointmentDate(), 20);
        JTextField timeField = new JTextField(appointment.getAppointmentTime(), 20);
        
        int row = 0;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(timeField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            appointment.setAppointmentDate(dateField.getText());
            appointment.setAppointmentTime(timeField.getText());
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            appointmentRepository.update(appointment);
            refreshAppointmentsTable();
            loadDashboardAppointments();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment rescheduled successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setMinimumSize(new Dimension(400, 200));
        dialog.pack();
        dialog.setLocationRelativeTo(appointmentPanel);
        dialog.setVisible(true);
    }
    
    // Edit appointment - allows editing all fields
    private void editAppointment() {
        String appointmentId = appointmentPanel.getSelectedAppointmentId();
        if (appointmentId == null) return;
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) return;
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(appointmentPanel), "Edit Appointment", true);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Patient dropdown
        JComboBox<String> patientCombo = new JComboBox<>();
        String currentPatientId = appointment.getPatientId();
        for (Patient p : patientRepository.getAll()) {
            patientCombo.addItem(p.getPatientId() + " - " + p.getFullName());
            if (p.getPatientId().equals(currentPatientId)) {
                patientCombo.setSelectedItem(p.getPatientId() + " - " + p.getFullName());
            }
        }
        
        // Clinician dropdown
        JComboBox<String> clinicianCombo = new JComboBox<>();
        String currentClinicianId = appointment.getClinicianId();
        for (Clinician c : clinicianRepository.getAll()) {
            clinicianCombo.addItem(c.getClinicianId() + " - " + c.getFullName());
            if (c.getClinicianId().equals(currentClinicianId)) {
                clinicianCombo.setSelectedItem(c.getClinicianId() + " - " + c.getFullName());
            }
        }
        
        // Facility dropdown
        JComboBox<String> facilityCombo = new JComboBox<>();
        String currentFacilityId = appointment.getFacilityId();
        if (facilityRepository != null) {
            for (Facility f : facilityRepository.getAll()) {
                facilityCombo.addItem(f.getFacilityId() + " - " + f.getFacilityName());
                if (f.getFacilityId().equals(currentFacilityId)) {
                    facilityCombo.setSelectedItem(f.getFacilityId() + " - " + f.getFacilityName());
                }
            }
        }
        
        // Form fields pre-filled with current values
        JTextField dateField = new JTextField(appointment.getAppointmentDate(), 20);
        JTextField timeField = new JTextField(appointment.getAppointmentTime(), 20);
        JTextField reasonField = new JTextField(appointment.getReasonForVisit(), 25);
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Scheduled", "Completed", "Cancelled", "No Show"});
        statusCombo.setSelectedItem(appointment.getStatus());
        
        // Layout form fields
        int row = 0;
        
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(patientCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Clinician:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(clinicianCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Facility:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(facilityCombo, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(timeField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(reasonField, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        panel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(statusCombo, gbc);
        
        // Buttons
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2; gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        // Save button action
        saveButton.addActionListener(e -> {
            String patientId = ((String) patientCombo.getSelectedItem()).split(" - ")[0];
            String clinicianId = ((String) clinicianCombo.getSelectedItem()).split(" - ")[0];
            String facilityId = ((String) facilityCombo.getSelectedItem()).split(" - ")[0];
            
            appointment.setPatientId(patientId);
            appointment.setClinicianId(clinicianId);
            appointment.setFacilityId(facilityId);
            appointment.setAppointmentDate(dateField.getText());
            appointment.setAppointmentTime(timeField.getText());
            appointment.setReasonForVisit(reasonField.getText());
            appointment.setStatus((String) statusCombo.getSelectedItem());
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            appointmentRepository.update(appointment);
            refreshAppointmentsTable();
            loadDashboardAppointments();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setMinimumSize(new Dimension(450, 400));
        dialog.pack();
        dialog.setLocationRelativeTo(appointmentPanel);
        dialog.setVisible(true);
    }
    
    // Cancel appointment with confirmation
    private void cancelAppointment() {
        String appointmentId = appointmentPanel.getSelectedAppointmentId();
        if (appointmentId == null) return;
        
        int confirm = JOptionPane.showConfirmDialog(
            appointmentPanel,
            "Are you sure you want to cancel this appointment?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentRepository.delete(appointmentId);
            refreshAppointmentsTable();
            loadDashboardAppointments();
            JOptionPane.showMessageDialog(appointmentPanel, "Appointment cancelled successfully!");
        }
    }
}
