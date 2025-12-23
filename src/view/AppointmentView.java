package view;

import controller.AppointmentController;
import model.Appointment;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentView extends JPanel {

    private AppointmentController controller;

    private JTable table;
    private DefaultTableModel model;

    // Form components
    private JTextField txtId, txtDate, txtTime, txtDuration, txtType;
    private JTextField txtReason, txtCreatedDate, txtLastModified;

    private JComboBox<String> cbStatus;          // 🔥 STATUS DROPDOWN
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbClinicianId;
    private JComboBox<String> cbFacilityId;

    private JTextArea txtNotes;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AppointmentView() {
        setLayout(new BorderLayout(10, 10));

        // ============================================================
        // TABLE
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Clinician", "Facility",
                        "Date", "Time", "Duration (min)", "Type",
                        "Status", "Reason", "Notes", "Created", "Last Modified"
                }, 0
        );

        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.SOUTH);


        // ============================================================
        // FORM
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 6, 6, 6);
        gc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(); 
        txtId.setEditable(false);

        cbPatientId = new JComboBox<>();
        cbClinicianId = new JComboBox<>();
        cbFacilityId = new JComboBox<>();

        txtDate = new JTextField();
        txtTime = new JTextField();
        txtDuration = new JTextField();
        txtType = new JTextField();
        txtReason = new JTextField();

        // 🔥 STATUS DROPDOWN ADDED
        cbStatus = new JComboBox<>(new String[]{
                "SCHEDULED",
                "RESCHEDULED",
                "CANCELLED",
                "COMPLETED",
                "NO-SHOW"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtNotes = new JTextArea(3, 15);
        txtNotes.setLineWrap(true);
        txtNotes.setWrapStyleWord(true);

        txtCreatedDate = new JTextField();
        txtLastModified = new JTextField();

        int row = 0;
        addFieldPair(form, gc, row++, "Appointment ID:", txtId, "Patient ID:", cbPatientId);
        addFieldPair(form, gc, row++, "Clinician ID:", cbClinicianId, "Facility ID:", cbFacilityId);
        addFieldPair(form, gc, row++, "Appointment Date:", txtDate, "Time (HH:mm):", txtTime);
        addFieldPair(form, gc, row++, "Duration (min):", txtDuration, "Appointment Type:", txtType);

        // 🔥 Status dropdown included here
        addFieldPair(form, gc, row++, "Status:", cbStatus, "Reason for Visit:", txtReason);

        addFieldPair(form, gc, row++, "Created Date:", txtCreatedDate, "Last Modified:", txtLastModified);

        // Notes row
        gc.gridx = 0; gc.gridy = row; gc.gridwidth = 1;
        form.add(new JLabel("Notes:"), gc);

        gc.gridx = 1; gc.gridy = row; gc.gridwidth = 3;
        form.add(new JScrollPane(txtNotes), gc);

        add(form, BorderLayout.CENTER);


        // ============================================================
        // BUTTONS
        // ============================================================
        JButton btnAdd = new JButton("Add Appointment");
        JButton btnDelete = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> addAppointment());
        btnDelete.addActionListener(e -> deleteAppointment());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttons.add(btnAdd);
        buttons.add(btnDelete);

        add(buttons, BorderLayout.NORTH);
    }


    // Helper method for form layout
    private void addFieldPair(JPanel panel, GridBagConstraints gc, int row,
                              String l1, JComponent f1,
                              String l2, JComponent f2) {

        gc.gridwidth = 1;

        gc.gridx = 0; gc.gridy = row;
        panel.add(new JLabel(l1), gc);

        gc.gridx = 1;
        panel.add(f1, gc);

        gc.gridx = 2;
        panel.add(new JLabel(l2), gc);

        gc.gridx = 3;
        panel.add(f2, gc);
    }


    // ============================================================
    // Dropdown loading
    // ============================================================
    public void loadDropdowns(List<String> patients, List<String> clinicians, List<String> facilities) {
        cbPatientId.removeAllItems();
        cbClinicianId.removeAllItems();
        cbFacilityId.removeAllItems();

        for (String s : patients) cbPatientId.addItem(s);
        for (String s : clinicians) cbClinicianId.addItem(s);
        for (String s : facilities) cbFacilityId.addItem(s);

        txtId.setText(controller.generateId());
        txtCreatedDate.setText(LocalDate.now().format(fmt));
        txtLastModified.setText(LocalDate.now().format(fmt));
    }


    // ============================================================
    // TABLE VIEW UPDATE
    // ============================================================
    public void showAppointments(List<Appointment> list) {
        model.setRowCount(0);

        for (Appointment a : list) {
            model.addRow(new Object[]{
                    a.getId(),
                    a.getPatientId(),
                    a.getClinicianId(),
                    a.getFacilityId(),
                    a.getAppointmentDate(),
                    a.getAppointmentTime(),
                    a.getDurationMinutes(),
                    a.getAppointmentType(),
                    a.getStatus(),
                    a.getReasonForVisit(),
                    a.getNotes(),
                    a.getCreatedDate(),
                    a.getLastModified()
            });
        }
    }


    // ============================================================
    // ADD APPOINTMENT
    // ============================================================
    private void addAppointment() {

        Appointment a = new Appointment(
                txtId.getText(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbFacilityId.getSelectedItem(),
                txtDate.getText(),
                txtTime.getText(),
                txtDuration.getText(),
                txtType.getText(),
                (String) cbStatus.getSelectedItem(),   // 🔥 STATUS DROPDOWN USED
                txtReason.getText(),
                txtNotes.getText(),
                txtCreatedDate.getText(),
                txtLastModified.getText()
        );

        controller.addAppointment(a);

        // Regenerate ID & timestamps
        txtId.setText(controller.generateId());
        txtLastModified.setText(LocalDate.now().format(fmt));
    }


    // ============================================================
    // DELETE
    // ============================================================
    private void deleteAppointment() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an appointment to delete.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        controller.deleteById(id);
    }

    public void setController(AppointmentController controller) {
        this.controller = controller;
    }
}
