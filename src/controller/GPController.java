package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.awt.*;

public class GPController {
    
    private final GPDashboard mainView;
    private final DashboardPanel dashboardPanel;
    private final AppointmentPanel appointmentPanel;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    private final Clinician clinician;
    
    public GPController(GPDashboard mainView,
                       AppointmentRepository appointmentRepository,
                       PatientRepository patientRepository,
                       ClinicianRepository clinicianRepository,
                       FacilityRepository facilityRepository) {
        this.mainView = mainView;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        this.clinician = mainView.getClinician();
        
        this.dashboardPanel = new DashboardPanel();
        this.appointmentPanel = new AppointmentPanel();
        
        setupNavigation();
        setupDashboard();
        setupAppointments();
    }
    
    private void setupNavigation() {
        mainView.addCard("Dashboard", dashboardPanel);
        mainView.addCard("Appointments", appointmentPanel);
        mainView.addCard("Medical Records", new JPanel());
        
        mainView.getDashboardButton().addActionListener(e -> mainView.showCard("Dashboard"));
        mainView.getAppointmentsButton().addActionListener(e -> {
            mainView.showCard("Appointments");
            refreshAppointmentsTable();
        });
        mainView.getMedicalRecordsButton().addActionListener(e -> mainView.showCard("Medical Records"));
        
        mainView.showCard("Dashboard");
    }
    
    private void setupDashboard() {
        loadDashboardAppointments();
        
        dashboardPanel.getAppointmentsTable().getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    String patientId = dashboardPanel.getSelectedPatientId();
                    if (patientId != null) {
                        dashboardPanel.clearSearchField();
                        updateHistoryTable(patientId);
                    }
                }
            });
        
        dashboardPanel.getSearchButton().addActionListener(e -> {
            String patientId = dashboardPanel.getSearchFieldText();
            if (!patientId.isEmpty()) {
                updateHistoryTable(patientId);
            }
        });
        
        dashboardPanel.getRefreshButton().addActionListener(e -> {
            loadDashboardAppointments();
            dashboardPanel.clearHistory();
            dashboardPanel.clearSearchField();
        });
    }
    
    private void setupAppointments() {
        refreshAppointmentsTable();
        
        appointmentPanel.getTable().getSelectionModel()
            .addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    boolean hasSelection = appointmentPanel.getSelectedRow() >= 0;
                    appointmentPanel.setEditButtonEnabled(hasSelection);
                    appointmentPanel.setCancelButtonEnabled(hasSelection);
                }
            });
        
        appointmentPanel.getSearchField().addActionListener(e -> filterAppointments());
        
        appointmentPanel.getBookNewButton().addActionListener(e -> bookNewAppointment());
        appointmentPanel.getEditButton().addActionListener(e -> editAppointment());
        appointmentPanel.getCancelButton().addActionListener(e -> cancelAppointment());
    }
    
    private void loadDashboardAppointments() {
        dashboardPanel.clearAppointments();
        
        String clinicianId = clinician.getClinicianId();
        if (clinicianId == null) {
            clinicianId = clinician.getId();
        }
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (clinicianId.equals(appointment.getClinicianId())) {
                Patient patient = patientRepository.findById(appointment.getPatientId());
                String patientName = patient != null ? patient.getFullName() : "Unknown";
                
                dashboardPanel.addAppointmentRow(
                    appointment.getAppointmentTime(),
                    appointment.getPatientId(),
                    patientName,
                    appointment.getReasonForVisit(),
                    appointment.getStatus()
                );
            }
        }
    }
    
    private void updateHistoryTable(String patientId) {
        dashboardPanel.clearHistory();
        
        Patient patient = patientRepository.findById(patientId);
        if (patient == null) return;
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            if (patientId.equals(appointment.getPatientId())) {
                Clinician apptClinician = clinicianRepository.findById(appointment.getClinicianId());
                String clinicianName = apptClinician != null ? apptClinician.getFullName() : appointment.getClinicianId();
                
                String facilityName = appointment.getFacilityId();
                if (facilityRepository != null) {
                    Facility facility = facilityRepository.findById(appointment.getFacilityId());
                    if (facility != null) {
                        facilityName = facility.getFacilityName();
                    }
                }
                
                dashboardPanel.addHistoryRow(
                    appointment.getAppointmentDate(),
                    clinicianName,
                    facilityName,
                    appointment.getReasonForVisit(),
                    appointment.getStatus()
                );
            }
        }
    }
    
    private void refreshAppointmentsTable() {
        appointmentPanel.clearTable();
        
        for (Appointment appointment : appointmentRepository.getAll()) {
            Patient patient = patientRepository.findById(appointment.getPatientId());
            String patientName = patient != null ? patient.getFullName() : "Unknown";
            
            appointmentPanel.addRow(new Object[]{
                appointment.getId(),
                appointment.getPatientId(),
                patientName,
                appointment.getClinicianId(),
                appointment.getAppointmentDate(),
                appointment.getAppointmentTime(),
                appointment.getReasonForVisit(),
                appointment.getStatus()
            });
        }
        
        appointmentPanel.setEditButtonEnabled(false);
        appointmentPanel.setCancelButtonEnabled(false);
    }
    
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
            }
            
            if (matches || searchText.isEmpty()) {
                Patient patient = patientRepository.findById(appointment.getPatientId());
                String patientName = patient != null ? patient.getFullName() : "Unknown";
                
                appointmentPanel.addRow(new Object[]{
                    appointment.getId(),
                    appointment.getPatientId(),
                    patientName,
                    appointment.getClinicianId(),
                    appointment.getAppointmentDate(),
                    appointment.getAppointmentTime(),
                    appointment.getReasonForVisit(),
                    appointment.getStatus()
                });
            }
        }
    }
    
    private void bookNewAppointment() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(appointmentPanel), "Book New Appointment", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(appointmentPanel);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JComboBox<String> patientCombo = new JComboBox<>();
        for (Patient p : patientRepository.getAll()) {
            patientCombo.addItem(p.getPatientId() + " - " + p.getFullName());
        }
        
        JComboBox<String> clinicianCombo = new JComboBox<>();
        for (Clinician c : clinicianRepository.getAll()) {
            clinicianCombo.addItem(c.getClinicianId() + " - " + c.getFullName());
        }
        
        JTextField dateField = new JTextField(15);
        JTextField timeField = new JTextField(15);
        JTextField reasonField = new JTextField(20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Patient:"), gbc);
        gbc.gridx = 1;
        panel.add(patientCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Clinician:"), gbc);
        gbc.gridx = 1;
        panel.add(clinicianCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(timeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        panel.add(reasonField, gbc);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            String patientId = ((String) patientCombo.getSelectedItem()).split(" - ")[0];
            String clinicianId = ((String) clinicianCombo.getSelectedItem()).split(" - ")[0];
            String facilityId = clinician.getWorkplaceId() != null ? clinician.getWorkplaceId() : "S001";
            
            Appointment newAppt = new Appointment(
                appointmentRepository.generateNewId(),
                patientId,
                clinicianId,
                facilityId,
                dateField.getText(),
                timeField.getText(),
                "15",
                "Routine Consultation",
                "Scheduled",
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
        dialog.setVisible(true);
    }
    
    private void editAppointment() {
        String appointmentId = appointmentPanel.getSelectedAppointmentId();
        if (appointmentId == null) return;
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) return;
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(appointmentPanel), "Edit Appointment", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(appointmentPanel);
        
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        JTextField dateField = new JTextField(appointment.getAppointmentDate(), 15);
        JTextField timeField = new JTextField(appointment.getAppointmentTime(), 15);
        JTextField reasonField = new JTextField(appointment.getReasonForVisit(), 20);
        
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Time (HH:MM):"), gbc);
        gbc.gridx = 1;
        panel.add(timeField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Reason:"), gbc);
        gbc.gridx = 1;
        panel.add(reasonField, gbc);
        
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        panel.add(buttonPanel, gbc);
        
        saveButton.addActionListener(e -> {
            appointment.setAppointmentDate(dateField.getText());
            appointment.setAppointmentTime(timeField.getText());
            appointment.setReasonForVisit(reasonField.getText());
            appointment.setLastModified(java.time.LocalDate.now().toString());
            
            appointmentRepository.update(appointment);
            refreshAppointmentsTable();
            loadDashboardAppointments();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setVisible(true);
    }
    
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
