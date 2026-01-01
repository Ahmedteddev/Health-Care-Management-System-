package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class PatientDashboardFrame extends JFrame {
    
    private JLabel welcomeLabel;
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;
    private JTable prescriptionsTable;
    private DefaultTableModel prescriptionsTableModel;
    
    // Appointment table columns
    private static final String[] APPOINTMENT_COLUMNS = {
        "Date", "Time", "Clinician", "Facility", "Status"
    };
    
    // Prescription table columns
    private static final String[] PRESCRIPTION_COLUMNS = {
        "Drug Name", "Dosage", "Instructions", "Date Prescribed"
    };
    
    public PatientDashboardFrame() {
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Patient Portal - Healthcare Management System");
        setSize(1000, 700);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        // Welcome label
        welcomeLabel = new JLabel("Patient Portal - Welcome, ");
        welcomeLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        
        // Appointment table model (read-only)
        appointmentsTableModel = new DefaultTableModel(APPOINTMENT_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        appointmentsTable = new JTable(appointmentsTableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        appointmentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Prescription table model (read-only)
        prescriptionsTableModel = new DefaultTableModel(PRESCRIPTION_COLUMNS, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only
            }
        };
        prescriptionsTable = new JTable(prescriptionsTableModel);
        prescriptionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prescriptionsTable.setRowHeight(25);
        prescriptionsTable.getTableHeader().setReorderingAllowed(false);
    }
    
    private void setupLayout() {
        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout(15, 15));
        contentPane.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header panel (North)
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.add(welcomeLabel);
        contentPane.add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel (Center) - use GridLayout to ensure tables fill the window
        JPanel mainContentPanel = new JPanel(new GridLayout(2, 1, 15, 15));
        
        // Appointment section (Top) - takes 50% of vertical space
        JPanel appointmentPanel = new JPanel(new BorderLayout(10, 10));
        appointmentPanel.setBorder(BorderFactory.createTitledBorder("My Appointments"));
        JScrollPane appointmentScrollPane = new JScrollPane(appointmentsTable);
        appointmentScrollPane.setPreferredSize(new Dimension(0, 300)); // Set preferred height
        appointmentPanel.add(appointmentScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(appointmentPanel);
        
        // Prescription section (Bottom) - takes 50% of vertical space
        JPanel prescriptionPanel = new JPanel(new BorderLayout(10, 10));
        prescriptionPanel.setBorder(BorderFactory.createTitledBorder("My Prescriptions"));
        JScrollPane prescriptionScrollPane = new JScrollPane(prescriptionsTable);
        prescriptionScrollPane.setPreferredSize(new Dimension(0, 300)); // Set preferred height
        prescriptionPanel.add(prescriptionScrollPane, BorderLayout.CENTER);
        mainContentPanel.add(prescriptionPanel);
        
        contentPane.add(mainContentPanel, BorderLayout.CENTER);
    }
    
    // Set patient name in welcome label
    public void setPatientName(String name) {
        welcomeLabel.setText("Patient Portal - Welcome, " + name);
    }
    
    // Clear appointment table
    public void clearAppointments() {
        appointmentsTableModel.setRowCount(0);
    }
    
    // Add appointment row
    public void addAppointmentRow(String date, String time, String clinician, String facility, String status) {
        appointmentsTableModel.addRow(new Object[]{date, time, clinician, facility, status});
    }
    
    // Clear prescription table
    public void clearPrescriptions() {
        prescriptionsTableModel.setRowCount(0);
    }
    
    // Add prescription row
    public void addPrescriptionRow(String drugName, String dosage, String instructions, String datePrescribed) {
        prescriptionsTableModel.addRow(new Object[]{drugName, dosage, instructions, datePrescribed});
    }
}

