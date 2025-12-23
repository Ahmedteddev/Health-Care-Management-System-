package view;

import controller.ReferralController;
import model.Referral;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReferralView extends JPanel {

    private ReferralController controller;
    private JTable table;
    private DefaultTableModel model;

    // Text fields
    private JTextField txtId, txtReason, txtRequestedService,
            txtCreatedDate, txtLastUpdated;

    private JTextArea txtClinicalSummary, txtNotes;

    private JFormattedTextField txtReferralDate;

    // ComboBoxes
    private JComboBox<String> cbPatientId;
    private JComboBox<String> cbRefClin, cbToClin;
    private JComboBox<String> cbRefFacility, cbToFacility;
    private JComboBox<String> cbUrgency;
    private JComboBox<String> cbAppointmentId;
    private JComboBox<String> cbStatus;     // NEW DROPDOWN

    private final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final DateTimeFormatter localDateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public ReferralView() {

        setLayout(new BorderLayout(10,10));

        // ============================================================
        // TABLE AT TOP — ALL REFERRAL FIELDS
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Patient",
                        "Ref Clin",
                        "To Clin",
                        "Ref Facility",
                        "To Facility",
                        "Date",
                        "Urgency",
                        "Reason",
                        "Clinical Summary",
                        "Requested Service",
                        "Status",
                        "Appointment",
                        "Notes",
                        "Created",
                        "Updated"
                }, 0
        );

        table = new JTable(model);
        table.setRowHeight(18);
        add(new JScrollPane(table), BorderLayout.NORTH);

        // ============================================================
        // FORM (4-COLUMN GRID)
        // ============================================================
        JPanel formPanel = new JPanel();
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setLayout(new GridLayout(0, 4, 20, 10));

        txtId = createField();
        txtReason = createField();
        txtRequestedService = createField();

        txtCreatedDate = createField(); txtCreatedDate.setEditable(false);
        txtLastUpdated = createField(); txtLastUpdated.setEditable(false);

        cbPatientId = createCombo();
        cbRefClin = createCombo();
        cbToClin = createCombo();
        cbRefFacility = createCombo();
        cbToFacility = createCombo();
        cbAppointmentId = createCombo();

        // Urgency dropdown
        cbUrgency = new JComboBox<>(new String[]{
                "Routine",
                "Urgent",
                "Non-urgent",
                "2-week wait"
        });
        cbUrgency.setFont(new Font("SansSerif", Font.PLAIN, 12));

        // NEW STATUS DROPDOWN
        cbStatus = new JComboBox<>(new String[]{
                "Pending",
                "Sent",
                "Received",
                "In Review",
                "Accepted",
                "Rejected",
                "Completed",
                "Cancelled"
        });
        cbStatus.setFont(new Font("SansSerif", Font.PLAIN, 12));

        txtReferralDate = createDateField();

        txtClinicalSummary = createArea();
        txtNotes = createArea();

        // ============================================================
        // ADD ROWS
        // ============================================================
        formPanel.add(labeled("Referral ID:", txtId));
        formPanel.add(labeled("Patient ID:", cbPatientId));
        formPanel.add(labeled("Referring Clinician ID:", cbRefClin));
        formPanel.add(labeled("Referred-To Clinician ID:", cbToClin));

        formPanel.add(labeled("Referring Facility ID:", cbRefFacility));
        formPanel.add(labeled("Referred-To Facility ID:", cbToFacility));
        formPanel.add(labeled("Referral Date (dd/MM/yyyy):", txtReferralDate));
        formPanel.add(labeled("Urgency Level:", cbUrgency));

        formPanel.add(labeled("Referral Reason:", txtReason));
        formPanel.add(labeled("Requested Service:", txtRequestedService));
        formPanel.add(labeled("Status:", cbStatus));                   // UPDATED
        formPanel.add(labeled("Appointment ID:", cbAppointmentId));

        formPanel.add(labeled("Clinical Summary:", new JScrollPane(txtClinicalSummary)));
        formPanel.add(labeled("Notes:", new JScrollPane(txtNotes)));
        formPanel.add(labeled("Created Date:", txtCreatedDate));
        formPanel.add(labeled("Last Updated:", txtLastUpdated));

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // ============================================================
        // BUTTON PANEL
        // ============================================================
        JButton btnAdd = new JButton("Create Referral");
        btnAdd.setPreferredSize(new Dimension(160, 30));
        btnAdd.addActionListener(e -> onAdd());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.add(btnAdd);

        add(buttonPanel, BorderLayout.WEST);
    }

    // ---------- Helper Creators ----------
    private JTextField createField() {
        JTextField f = new JTextField(12);
        f.setPreferredSize(new Dimension(140, 22));
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return f;
    }

    private JTextArea createArea() {
        JTextArea a = new JTextArea(2, 10);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setFont(new Font("SansSerif", Font.PLAIN, 12));
        a.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        return a;
    }

    private JComboBox<String> createCombo() {
        JComboBox<String> cb = new JComboBox<>();
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return cb;
    }

    private JFormattedTextField createDateField() {
        DateFormatter df = new DateFormatter(dateFormat);
        JFormattedTextField f = new JFormattedTextField(df);
        f.setFont(new Font("SansSerif", Font.PLAIN, 12));
        f.setValue(java.util.Date.from(LocalDate.now()
                .atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
        return f;
    }

    private JPanel labeled(String label, Component field) {
        JPanel p = new JPanel(new BorderLayout(3, 2));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    // ============================================================
    // CONTROLLER LINKING
    // ============================================================
    public void setController(ReferralController controller) {
        this.controller = controller;
        loadCombos();
        refreshAutoId();
        refreshDates();
    }

    private void loadCombos() {
        cbPatientId.removeAllItems();
        cbRefClin.removeAllItems();
        cbToClin.removeAllItems();
        cbRefFacility.removeAllItems();
        cbToFacility.removeAllItems();
        cbAppointmentId.removeAllItems();

        // patients
        for (String id : controller.getPatientIds()) {
            cbPatientId.addItem(id);
        }

        // clinicians
        for (String id : controller.getClinicianIds()) {
            cbRefClin.addItem(id);
            cbToClin.addItem(id);
        }

        // facilities
        for (String id : controller.getFacilityIds()) {
            cbRefFacility.addItem(id);
            cbToFacility.addItem(id);
        }

        // appointments
        for (String id : controller.getAppointmentIds()) {
            cbAppointmentId.addItem(id);
        }
    }

    private void refreshAutoId() {
        txtId.setText(controller.getNextReferralId());
        txtId.setEditable(false);
    }

    private void refreshDates() {
        String today = LocalDate.now().format(localDateFormatter);
        txtCreatedDate.setText(today);
        txtLastUpdated.setText(today);
    }

    // ============================================================
    // DISPLAY REFERRALS
    // ============================================================
    public void showReferrals(List<Referral> list) {
        model.setRowCount(0);
        for (Referral r : list) {
            model.addRow(new Object[]{
                    r.getId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedService(),
                    r.getStatus(),
                    r.getAppointmentId(),
                    r.getNotes(),
                    r.getCreatedDate(),
                    r.getLastUpdated()
            });
        }
    }

    // ============================================================
    // ADD REFERRAL
    // ============================================================
    private void onAdd() {

        String errors = validateForm();
        if (!errors.isEmpty()) {
            JOptionPane.showMessageDialog(this, errors,
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Referral r = new Referral(
                txtId.getText().trim(),
                (String) cbPatientId.getSelectedItem(),
                (String) cbRefClin.getSelectedItem(),
                (String) cbToClin.getSelectedItem(),
                (String) cbRefFacility.getSelectedItem(),
                (String) cbToFacility.getSelectedItem(),
                txtReferralDate.getText().trim(),
                (String) cbUrgency.getSelectedItem(),
                txtReason.getText().trim(),
                txtClinicalSummary.getText().trim(),
                txtRequestedService.getText().trim(),
                (String) cbStatus.getSelectedItem(),         // UPDATED
                (String) cbAppointmentId.getSelectedItem(),
                txtNotes.getText().trim(),
                txtCreatedDate.getText().trim(),
                LocalDate.now().format(localDateFormatter)
        );

        controller.addReferral(r);

        JOptionPane.showMessageDialog(this,
                "Referral " + r.getId() + " created successfully.");

        refreshAutoId();
        refreshDates();
        clearFormButKeepIds();
    }

    private String validateForm() {
        StringBuilder sb = new StringBuilder();

        if (cbPatientId.getSelectedItem() == null)
            sb.append("- Patient ID required\n");

        if (cbRefClin.getSelectedItem() == null)
            sb.append("- Referring clinician required\n");

        if (cbToClin.getSelectedItem() == null)
            sb.append("- Referred-to clinician required\n");

        if (cbRefFacility.getSelectedItem() == null)
            sb.append("- Referring facility required\n");

        if (cbToFacility.getSelectedItem() == null)
            sb.append("- Referred-to facility required\n");

        if (txtReferralDate.getText().trim().isEmpty())
            sb.append("- Referral date required\n");

        if (txtReason.getText().trim().isEmpty())
            sb.append("- Referral reason required\n");

        if (txtClinicalSummary.getText().trim().isEmpty())
            sb.append("- Clinical summary required\n");

        return sb.toString();
    }

    private void clearFormButKeepIds() {
        txtReason.setText("");
        txtClinicalSummary.setText("");
        txtRequestedService.setText("");
        txtNotes.setText("");
    }
}
