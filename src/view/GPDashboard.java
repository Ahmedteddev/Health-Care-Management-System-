package view;

import model.Clinician;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * GP Dashboard view for displaying appointments and patient medical records.
 * Phase 1: Focus on appointments display and row selection.
 */
public class GPDashboard extends JFrame {
    
    private final Clinician clinician;
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private JTextArea medicalRecordArea;
    private JButton refreshButton;
    
    // Table column names
    private static final String[] COLUMN_NAMES = {
        "Time", "Patient ID", "Patient Name", "Reason", "Status"
    };
    
    /**
     * Constructor for GPDashboard.
     * 
     * @param clinician The clinician (GP/Specialist) using this dashboard
     */
    public GPDashboard(Clinician clinician) {
        this.clinician = clinician;
        
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("GP Dashboard - " + clinician.getFullName());
        setSize(900, 700);
        setLocationRelativeTo(null);
    }
    
    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        // Initialize table model
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };
        
        // Initialize table
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        appointmentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Initialize medical record area
        medicalRecordArea = new JTextArea(10, 50);
        medicalRecordArea.setEditable(false);
        medicalRecordArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        medicalRecordArea.setBorder(BorderFactory.createTitledBorder("Patient Medical Record"));
        
        // Initialize refresh button
        refreshButton = new JButton("Refresh");
    }
    
    /**
     * Sets up the layout of the dashboard.
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Header Panel (North)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Table Section (Center)
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Medical Record Section (South)
        JPanel recordPanel = createRecordPanel();
        add(recordPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header panel showing clinician name and specialty.
     */
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        headerPanel.setBackground(new Color(240, 240, 240));
        
        // Clinician name label
        String clinicianName = clinician.getFullName();
        JLabel nameLabel = new JLabel(clinicianName);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        
        // Specialty label
        String specialty = clinician.getSpeciality() != null ? 
                          clinician.getSpeciality() : "General Practice";
        JLabel specialtyLabel = new JLabel("Specialty: " + specialty);
        specialtyLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        specialtyLabel.setForeground(new Color(100, 100, 100));
        
        // Add components to header
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(specialtyLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(refreshButton, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    /**
     * Creates the medical record panel with text area and scroll pane.
     */
    private JPanel createRecordPanel() {
        JPanel recordPanel = new JPanel(new BorderLayout(5, 5));
        recordPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        recordPanel.setPreferredSize(new Dimension(0, 200));
        
        // Scroll pane for medical record area
        JScrollPane recordScrollPane = new JScrollPane(medicalRecordArea);
        recordScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        recordScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        recordPanel.add(recordScrollPane, BorderLayout.CENTER);
        
        return recordPanel;
    }
    
    /**
     * Clears all appointments from the table.
     */
    public void clearAppointments() {
        tableModel.setRowCount(0);
    }
    
    /**
     * Adds an appointment row to the table.
     * 
     * @param time The appointment time
     * @param patientId The patient ID
     * @param patientName The patient name
     * @param reason The reason for visit
     * @param status The appointment status
     */
    public void addAppointmentRow(String time, String patientId, String patientName, 
                                  String reason, String status) {
        tableModel.addRow(new Object[]{time, patientId, patientName, reason, status});
    }
    
    /**
     * Updates the medical record area with patient information.
     * 
     * @param recordText The text to display in the medical record area
     */
    public void updateMedicalRecord(String recordText) {
        medicalRecordArea.setText(recordText);
        medicalRecordArea.setCaretPosition(0); // Scroll to top
    }
    
    /**
     * Gets the selected row index from the appointments table.
     * 
     * @return The selected row index, or -1 if no row is selected
     */
    public int getSelectedRowIndex() {
        return appointmentsTable.getSelectedRow();
    }
    
    /**
     * Gets the patient ID from the selected row.
     * 
     * @return The patient ID, or null if no row is selected
     */
    public String getSelectedPatientId() {
        int selectedRow = appointmentsTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < tableModel.getRowCount()) {
            return (String) tableModel.getValueAt(selectedRow, 1); // Column 1 is Patient ID
        }
        return null;
    }
    
    /**
     * Gets the refresh button for adding action listeners.
     * 
     * @return The refresh button
     */
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    /**
     * Gets the appointments table for adding selection listeners.
     * 
     * @return The appointments table
     */
    public JTable getAppointmentsTable() {
        return appointmentsTable;
    }
    
    /**
     * Gets the clinician associated with this dashboard.
     * 
     * @return The clinician
     */
    public Clinician getClinician() {
        return clinician;
    }
    
    /**
     * Temporary testing main method.
     * Instantiates repositories and launches dashboard for C001 (Dr. David Thompson).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Instantiate all repositories with CSV paths
                model.PatientRepository patientRepo = 
                    new model.PatientRepository("src/data/patients.csv");
                
                model.ClinicianRepository clinicianRepo = 
                    new model.ClinicianRepository("src/data/clinicians.csv");
                
                model.AppointmentRepository appointmentRepo = 
                    new model.AppointmentRepository("src/data/appointments.csv");
                
                // Fetch C001 (Dr. David Thompson) from ClinicianRepository
                model.Clinician clinician = clinicianRepo.findById("C001");
                
                if (clinician == null) {
                    JOptionPane.showMessageDialog(null, 
                        "Clinician C001 not found in repository.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create and launch the dashboard
                GPDashboard dashboard = new GPDashboard(clinician);
                
                // Create controller to handle data loading
                controller.GPController controller = 
                    new controller.GPController(dashboard, appointmentRepo, patientRepo);
                
                // Load appointments for this clinician
                controller.loadAppointments();
                
                // Show the dashboard
                dashboard.setVisible(true);
                
                System.out.println("GP Dashboard launched for: " + clinician.getFullName());
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, 
                    "Error launching dashboard: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

