package view;

import controller.PrescriptionController;
import model.Prescription;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class PrescriptionView extends JPanel {

    private PrescriptionController controller;

    private JTable table;
    private DefaultTableModel model;

    private JLabel lblId;

    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbClinicianId;
    private JComboBox<String> cbDrug;
    private JComboBox<String> cbPharmacy;
    private JComboBox<String> cbStatus;         // 🔥 STATUS DROPDOWN
    private JComboBox<String> cbAppointmentId;  // 🔥 APPOINTMENT DROPDOWN

    private JTextField txtPrescDate;
    private JTextField txtDosage;
    private JTextField txtFrequency;
    private JTextField txtDuration;
    private JTextField txtQuantity;
    private JTextField txtIssueDate;
    private JTextField txtCollectionDate;

    private JTextArea txtInstructions;

    private static final String DATE_PATTERN = "yyyy-MM-dd";
    private final SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

    public PrescriptionView() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ============================================================
        // TABLE
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID", "Patient", "Clinician", "Appt",
                        "Presc Date", "Drug", "Dosage", "Freq",
                        "Duration", "Qty", "Instructions",
                        "Pharmacy", "Status", "Issue", "Collected"
                }, 0
        );
        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        // ============================================================
        // FORM
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.5;

        lblId = new JLabel("RX001");

        cbPatientId    = new JComboBox<>();
        cbClinicianId  = new JComboBox<>();
        cbDrug         = new JComboBox<>();
        cbPharmacy     = new JComboBox<>();
        cbAppointmentId = new JComboBox<>();

        cbStatus = new JComboBox<>(new String[]{
                "PENDING",
                "ISSUED",
                "COLLECTED",
                "CANCELLED",
                "REJECTED"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtPrescDate      = new JTextField();
        txtDosage         = new JTextField();
        txtFrequency      = new JTextField();
        txtDuration       = new JTextField();
        txtQuantity       = new JTextField();
        txtIssueDate      = new JTextField();
        txtCollectionDate = new JTextField();

        txtInstructions = new JTextArea(3, 20);
        txtInstructions.setLineWrap(true);
        txtInstructions.setWrapStyleWord(true);

        int row = 0;
        addPair(form, gc, row++, "Prescription ID:", lblId,
                "Patient ID:", cbPatientId);

        addPair(form, gc, row++, "Clinician ID:", cbClinicianId,
                "Appointment ID:", cbAppointmentId);

        addPair(form, gc, row++, "Prescription Date (yyyy-MM-dd):", txtPrescDate,
                "Drug:", cbDrug);

        addPair(form, gc, row++, "Dosage:", txtDosage,
                "Frequency:", txtFrequency);

        addPair(form, gc, row++, "Duration (days):", txtDuration,
                "Quantity:", txtQuantity);

        addPair(form, gc, row++, "Pharmacy:", cbPharmacy,
                "Status:", cbStatus);

        addPair(form, gc, row++, "Issue Date (yyyy-MM-dd):", txtIssueDate,
                "Collection Date (yyyy-MM-dd):", txtCollectionDate);

        // Instructions field
        gc.gridy = row;
        gc.gridx = 0;
        gc.gridwidth = 1;
        form.add(new JLabel("Instructions:"), gc);

        gc.gridx = 1;
        gc.gridwidth = 3;
        form.add(new JScrollPane(txtInstructions), gc);

        add(form, BorderLayout.CENTER);

        // ============================================================
        // BUTTONS
        // ============================================================
        JButton btnAdd    = new JButton("Add");
        JButton btnUpdate = new JButton("Update Selected");
        JButton btnDelete = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> onAdd());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.add(btnAdd);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        add(btnPanel, BorderLayout.NORTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRowIntoForm();
        });
    }

    private void addPair(JPanel panel, GridBagConstraints gc, int row,
                         String label1, JComponent field1,
                         String label2, JComponent field2) {

        gc.gridy = row;

        gc.gridx = 0;
        panel.add(new JLabel(label1), gc);
        gc.gridx = 1;
        panel.add(field1, gc);

        gc.gridx = 2;
        panel.add(new JLabel(label2), gc);
        gc.gridx = 3;
        panel.add(field2, gc);
    }

    // ============================================================
    // Controller Hooks
    // ============================================================
    public void setController(PrescriptionController controller) {
        this.controller = controller;
    }

    public void populateDropdowns(List<String> patientIds,
                                  List<String> clinicianIds,
                                  List<String> drugs,
                                  List<String> pharmacies,
                                  List<String> appointmentIds) {

        cbPatientId.removeAllItems();
        for (String id : patientIds) cbPatientId.addItem(id);

        cbClinicianId.removeAllItems();
        for (String id : clinicianIds) cbClinicianId.addItem(id);

        cbDrug.removeAllItems();
        for (String d : drugs) cbDrug.addItem(d);

        cbPharmacy.removeAllItems();
        for (String ph : pharmacies) cbPharmacy.addItem(ph);

        cbAppointmentId.removeAllItems();
        for (String ap : appointmentIds) cbAppointmentId.addItem(ap);
    }

    public void setNextId(String id) {
        lblId.setText(id);
    }

    public void showPrescriptions(List<Prescription> list) {
        model.setRowCount(0);
        for (Prescription p : list) {
            model.addRow(new Object[]{
                    p.getId(),
                    p.getPatientId(),
                    p.getClinicianId(),
                    p.getAppointmentId(),
                    p.getPrescriptionDate(),
                    p.getMedication(),
                    p.getDosage(),
                    p.getFrequency(),
                    p.getDurationDays(),
                    p.getQuantity(),
                    p.getInstructions(),
                    p.getPharmacyName(),
                    p.getStatus(),
                    p.getIssueDate(),
                    p.getCollectionDate()
            });
        }
    }

    // ============================================================
    // ADD
    // ============================================================
    private void onAdd() {
        if (controller == null) return;

        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Prescription p = buildFromForm(lblId.getText());
        controller.addPrescription(p);
        clearFormButKeepIds();
    }

    // ============================================================
    // UPDATE
    // ============================================================
    private void onUpdate() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to update.");
            return;
        }

        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String id = lblId.getText();
        Prescription p = buildFromForm(id);
        controller.updatePrescription(p);
    }

    // ============================================================
    // DELETE
    // ============================================================
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }

        String id = model.getValueAt(row, 0).toString();
        controller.deleteById(id);
    }

    // ============================================================
    // Build Model from Form
    // ============================================================
    private Prescription buildFromForm(String id) {
        return new Prescription(
                id,
                (String) cbPatientId.getSelectedItem(),
                (String) cbClinicianId.getSelectedItem(),
                (String) cbAppointmentId.getSelectedItem(),   // 🔥 from dropdown
                txtPrescDate.getText().trim(),
                (String) cbDrug.getSelectedItem(),
                txtDosage.getText().trim(),
                txtFrequency.getText().trim(),
                txtDuration.getText().trim(),
                txtQuantity.getText().trim(),
                txtInstructions.getText().trim(),
                (String) cbPharmacy.getSelectedItem(),
                (String) cbStatus.getSelectedItem(),
                txtIssueDate.getText().trim(),
                txtCollectionDate.getText().trim()
        );
    }

    // ============================================================
    // Load selected table row → form
    // ============================================================
    private void loadSelectedRowIntoForm() {
        int row = table.getSelectedRow();
        if (row < 0) return;

        lblId.setText(model.getValueAt(row, 0).toString());
        cbPatientId.setSelectedItem(model.getValueAt(row, 1));
        cbClinicianId.setSelectedItem(model.getValueAt(row, 2));

        cbAppointmentId.setSelectedItem(model.getValueAt(row, 3));
        txtPrescDate.setText(value(row, 4));
        cbDrug.setSelectedItem(model.getValueAt(row, 5));
        txtDosage.setText(value(row, 6));
        txtFrequency.setText(value(row, 7));
        txtDuration.setText(value(row, 8));
        txtQuantity.setText(value(row, 9));
        txtInstructions.setText(value(row, 10));
        cbPharmacy.setSelectedItem(model.getValueAt(row, 11));
        cbStatus.setSelectedItem(model.getValueAt(row, 12));
        txtIssueDate.setText(value(row, 13));
        txtCollectionDate.setText(value(row, 14));
    }

    private String value(int row, int col) {
        Object v = model.getValueAt(row, col);
        return v == null ? "" : v.toString();
    }

    // ============================================================
    // VALIDATION
    // ============================================================
    private String validateForm() {
        StringBuilder sb = new StringBuilder();

        if (cbPatientId.getSelectedItem() == null)
            sb.append("- Patient ID is required.\n");

        if (cbClinicianId.getSelectedItem() == null)
            sb.append("- Clinician ID is required.\n");

        if (cbDrug.getSelectedItem() == null)
            sb.append("- Drug must be selected.\n");

        if (txtDosage.getText().trim().isEmpty())
            sb.append("- Dosage is required.\n");

        if (!txtDuration.getText().trim().isEmpty()) {
            try { Integer.parseInt(txtDuration.getText().trim()); }
            catch (NumberFormatException e) {
                sb.append("- Duration must be a number.\n");
            }
        }

        if (!txtQuantity.getText().trim().isEmpty()) {
            try { Integer.parseInt(txtQuantity.getText().trim()); }
            catch (NumberFormatException e) {
                sb.append("- Quantity must be a number.\n");
            }
        }

        checkDate(txtPrescDate.getText().trim(), "Prescription Date", sb);
        if (!txtIssueDate.getText().trim().isEmpty())
            checkDate(txtIssueDate.getText().trim(), "Issue Date", sb);
        if (!txtCollectionDate.getText().trim().isEmpty())
            checkDate(txtCollectionDate.getText().trim(), "Collection Date", sb);

        return sb.toString();
    }

    private void checkDate(String value, String label, StringBuilder sb) {
        if (value.isEmpty()) return;
        sdf.setLenient(false);
        try { sdf.parse(value); }
        catch (ParseException e) {
            sb.append("- ").append(label)
              .append(" must be in format ").append(DATE_PATTERN).append(".\n");
        }
    }

    private void clearFormButKeepIds() {
        txtPrescDate.setText("");
        txtDosage.setText("");
        txtFrequency.setText("");
        txtDuration.setText("");
        txtQuantity.setText("");
        cbStatus.setSelectedIndex(0);   // reset
        txtIssueDate.setText("");
        txtCollectionDate.setText("");
        txtInstructions.setText("");
        // keep dropdown selections and ID
    }
}
