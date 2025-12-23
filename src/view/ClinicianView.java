package view;

import controller.ClinicianController;
import model.Clinician;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ClinicianView extends JPanel {

    private ClinicianController controller;

    private JTable table;
    private DefaultTableModel model;

    private JLabel lblId;
    private JTextField txtFirstName, txtLastName, txtSpeciality, txtGmc, txtPhone, txtEmail, txtWorkplaceId;
    private JComboBox<String> cmbTitle, cmbWorkplaceType, cmbEmployment;
    private JSpinner dateSpinner;

    public ClinicianView() {

        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ============================================================
        // TABLE
        // ============================================================
        model = new DefaultTableModel(
                new Object[]{
                        "ID","Title","First","Last","Speciality","GMC",
                        "Phone","Email","Workplace ID","Workplace Type",
                        "Employment","Start Date"
                }, 0
        );

        table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.SOUTH);

        // ============================================================
        // FORM PANEL (4 COLUMNS)
        // ============================================================
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(10, 15, 10, 15);
        gc.fill = GridBagConstraints.HORIZONTAL;

        lblId = new JLabel("C001");

        txtFirstName = new JTextField();
        txtLastName = new JTextField();
        txtSpeciality = new JTextField();
        txtGmc = new JTextField();
        txtPhone = new JTextField();
        txtEmail = new JTextField();
        txtWorkplaceId = new JTextField();

        cmbTitle = new JComboBox<>(new String[]{"GP","Consultant","Nurse","Specialist"});
        cmbWorkplaceType = new JComboBox<>(new String[]{"GP Surgery","Hospital","Clinic"});
        cmbEmployment = new JComboBox<>(new String[]{"Full-time","Part-time","Locum"});

        dateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));

        int row = 0;

        // ============================  4 COLUMNS  ============================
        add4(form, gc, row++, "Clinician ID:", lblId,    "Title:", cmbTitle);
        add4(form, gc, row++, "First Name:", txtFirstName, "Last Name:", txtLastName);
        add4(form, gc, row++, "Speciality:", txtSpeciality, "GMC Number:", txtGmc);
        add4(form, gc, row++, "Phone:", txtPhone, "Email:", txtEmail);
        add4(form, gc, row++, "Workplace ID:", txtWorkplaceId, "Workplace Type:", cmbWorkplaceType);
        add4(form, gc, row++, "Employment:", cmbEmployment, "Start Date:", dateSpinner);

        add(form, BorderLayout.CENTER);

        // ============================================================
        // BUTTON PANEL
        // ============================================================
        JButton btnAdd = new JButton("Add Clinician");
        JButton btnDelete = new JButton("Delete Selected");

        btnAdd.addActionListener(e -> onAdd());
        btnDelete.addActionListener(e -> onDelete());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttons.add(btnAdd);
        buttons.add(btnDelete);

        add(buttons, BorderLayout.NORTH);
    }

    // Helper for 4-column rows
    private void add4(JPanel panel, GridBagConstraints gc, int row,
                      String label1, JComponent field1,
                      String label2, JComponent field2) {

        gc.gridy = row;

        // Left label
        gc.gridx = 0;
        gc.weightx = 0.15;
        panel.add(new JLabel(label1), gc);

        // Left field
        gc.gridx = 1;
        gc.weightx = 0.35;
        panel.add(field1, gc);

        // Right label
        gc.gridx = 2;
        gc.weightx = 0.15;
        panel.add(new JLabel(label2), gc);

        // Right field
        gc.gridx = 3;
        gc.weightx = 0.35;
        panel.add(field2, gc);
    }

    // ============================================================
    // CONTROLLER LINK
    // ============================================================
    public void setController(ClinicianController controller) {
        this.controller = controller;
    }

    // ============================================================
    // DISPLAY DATA
    // ============================================================
    public void showClinicians(List<Clinician> list) {
        model.setRowCount(0);

        for (Clinician c : list) {
            model.addRow(new Object[]{
                    c.getId(), c.getTitle(), c.getFirstName(), c.getLastName(),
                    c.getSpeciality(), c.getGmcNumber(), c.getPhone(), c.getEmail(),
                    c.getWorkplaceId(), c.getWorkplaceType(),
                    c.getEmploymentStatus(), c.getStartDate()
            });
        }

        if (controller != null)
            lblId.setText(controller.generateId());
    }

    // ============================================================
    // ADD NEW CLINICIAN
    // ============================================================
    private void onAdd() {

        if (controller == null) return;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        Clinician c = new Clinician(
                lblId.getText(),
                (String) cmbTitle.getSelectedItem(),
                txtFirstName.getText(),
                txtLastName.getText(),
                txtSpeciality.getText(),
                txtGmc.getText(),
                txtPhone.getText(),
                txtEmail.getText(),
                txtWorkplaceId.getText(),
                (String) cmbWorkplaceType.getSelectedItem(),
                (String) cmbEmployment.getSelectedItem(),
                sdf.format(dateSpinner.getValue())
        );

        controller.addClinician(c);
    }

    // ============================================================
    // DELETE CLINICIAN
    // ============================================================
    private void onDelete() {
        if (controller == null) return;

        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Select a clinician to delete!",
                    "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String id = (String) model.getValueAt(row, 0);
        controller.deleteById(id);
    }
}
