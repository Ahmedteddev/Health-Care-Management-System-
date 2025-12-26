package controller;

import model.*;
import view.AppointmentPanel;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AppointmentController {
    
    private final AppointmentPanel view;
    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final FacilityRepository facilityRepository;
    
    private List<Appointment> allAppointments = new ArrayList<>();
    
    public AppointmentController(AppointmentPanel view,
                                AppointmentRepository appointmentRepository,
                                PatientRepository patientRepository,
                                ClinicianRepository clinicianRepository,
                                FacilityRepository facilityRepository) {
        this.view = view;
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.facilityRepository = facilityRepository;
        
        bind();
        // Default state: show all appointments (master list)
        refreshAppointmentsTable();
    }
    
    private void bind() {
        // Load all appointments
        loadAllAppointments();
        
        // Search button listener
        view.getSearchButton().addActionListener(e -> filterAppointments());
        
        // Enter key in search field also triggers search
        view.getSearchField().addActionListener(e -> filterAppointments());
        
        // Book New button listener
        view.getBookNewButton().addActionListener(e -> bookNewAppointment());
        
        // Reschedule button listener
        view.getRescheduleButton().addActionListener(e -> rescheduleAppointment());
        
        // Edit button listener (if needed)
        view.getEditButton().addActionListener(e -> editAppointment());
        
        // Cancel button listener (if needed)
        view.getCancelButton().addActionListener(e -> cancelAppointment());
        
        // Enable/disable buttons based on selection
        view.getTable().getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean hasSelection = view.getSelectedRow() >= 0;
                view.setRescheduleButtonEnabled(hasSelection);
                view.setEditButtonEnabled(hasSelection);
                view.setCancelButtonEnabled(hasSelection);
            }
        });
    }
    
    // Load all appointments from repository
    private void loadAllAppointments() {
        allAppointments.clear();
        allAppointments.addAll(appointmentRepository.getAll());
    }
    
    // Filter appointments based on search criteria (simple master list search)
    private void filterAppointments() {
        String searchText = view.getSearchField().getText().toLowerCase().trim();
        String filterType = (String) view.getFilterComboBox().getSelectedItem();
        
        view.clearTable();
        loadAllAppointments();
        
        for (Appointment appointment : allAppointments) {
            boolean matches = true;
            
            // Filter by search criteria if specified
            if (!searchText.isEmpty()) {
                boolean searchMatches = false;
                
                if ("Patient ID".equals(filterType)) {
                    searchMatches = appointment.getPatientId().toLowerCase().contains(searchText);
                } else if ("Patient Name".equals(filterType)) {
                    Patient patient = patientRepository.findById(appointment.getPatientId());
                    if (patient != null) {
                        searchMatches = patient.getFullName().toLowerCase().contains(searchText);
                    }
                } else if ("Clinician ID".equals(filterType)) {
                    searchMatches = appointment.getClinicianId().toLowerCase().contains(searchText);
                } else if ("Clinician Name".equals(filterType)) {
                    Clinician apptClinician = clinicianRepository.findById(appointment.getClinicianId());
                    if (apptClinician != null) {
                        searchMatches = apptClinician.getFullName().toLowerCase().contains(searchText);
                    }
                }
                
                matches = searchMatches;
            }
            
            if (matches) {
                addAppointmentRow(appointment);
            }
        }
        
        // Disable buttons after refresh
        view.setRescheduleButtonEnabled(false);
        view.setEditButtonEnabled(false);
        view.setCancelButtonEnabled(false);
    }
    
    // Helper method to add appointment row to table
    private void addAppointmentRow(Appointment appointment) {
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
        view.addRow(new Object[]{
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
    
    // Refresh appointments table - shows all appointments
    public void refreshAppointmentsTable() {
        view.clearTable();
        loadAllAppointments();
        
        for (Appointment appointment : allAppointments) {
            addAppointmentRow(appointment);
        }
        
        // Disable buttons after refresh
        view.setRescheduleButtonEnabled(false);
        view.setEditButtonEnabled(false);
        view.setCancelButtonEnabled(false);
    }
    
    // Reschedule appointment - only changes date and time
    private void rescheduleAppointment() {
        String appointmentId = view.getSelectedAppointmentId();
        if (appointmentId == null) {
            JOptionPane.showMessageDialog(view, "Please select an appointment to reschedule.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(view, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Open JOptionPane asking for new date and time
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JTextField dateField = new JTextField(appointment.getAppointmentDate(), 15);
        JTextField timeField = new JTextField(appointment.getAppointmentTime(), 15);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        panel.add(new JLabel("New Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(dateField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        panel.add(new JLabel("New Time (HH:MM):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        panel.add(timeField, gbc);
        
        int result = JOptionPane.showConfirmDialog(
            view,
            panel,
            "Reschedule Appointment",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            String newDate = dateField.getText().trim();
            String newTime = timeField.getText().trim();
            
            if (newDate.isEmpty() || newTime.isEmpty()) {
                JOptionPane.showMessageDialog(view, "Date and time are required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Update the Appointment object
            appointment.setAppointmentDate(newDate);
            appointment.setAppointmentTime(newTime);
            appointment.setLastModified(LocalDate.now().toString());
            
            // Call AppointmentRepository.getInstance().updateAppointment(app)
            appointmentRepository.updateAppointment(appointment);
            
            // Refresh the table view
            filterAppointments();
            
            JOptionPane.showMessageDialog(view, "Appointment rescheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Book new appointment dialog
    private void bookNewAppointment() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(view), "Book New Appointment", true);
        
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
                LocalDate.now().toString(),
                LocalDate.now().toString()
            );
            
            appointmentRepository.add(newAppt);
            refreshAppointmentsTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment booked successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setMinimumSize(new Dimension(450, 350));
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }
    
    // Edit appointment - allows editing all fields
    private void editAppointment() {
        String appointmentId = view.getSelectedAppointmentId();
        if (appointmentId == null) {
            JOptionPane.showMessageDialog(view, "Please select an appointment to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Appointment appointment = appointmentRepository.findById(appointmentId);
        if (appointment == null) {
            JOptionPane.showMessageDialog(view, "Appointment not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(view), "Edit Appointment", true);
        
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
            appointment.setLastModified(LocalDate.now().toString());
            
            appointmentRepository.updateAppointment(appointment);
            refreshAppointmentsTable();
            dialog.dispose();
            JOptionPane.showMessageDialog(dialog, "Appointment updated successfully!");
        });
        
        cancelButton.addActionListener(e -> dialog.dispose());
        
        dialog.add(panel);
        dialog.setMinimumSize(new Dimension(450, 400));
        dialog.pack();
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);
    }
    
    // Cancel appointment with confirmation
    private void cancelAppointment() {
        String appointmentId = view.getSelectedAppointmentId();
        if (appointmentId == null) {
            JOptionPane.showMessageDialog(view, "Please select an appointment to cancel.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            view,
            "Are you sure you want to cancel this appointment?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            appointmentRepository.delete(appointmentId);
            refreshAppointmentsTable();
            JOptionPane.showMessageDialog(view, "Appointment cancelled successfully!");
        }
    }
    
    // Public method to refresh appointments table (for external use)
    public void refreshAppointments() {
        refreshAppointmentsTable();
    }
}

