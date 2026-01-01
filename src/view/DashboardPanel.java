package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {
    
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;
    private JTextField txtSearchID;
    private JTextField txtDateFilter;
    private JButton btnSearch;
    private JButton btnUpdateStatus;
    private JButton btnReschedule;
    private JButton btnShowToday;
    private JButton refreshButton;
    
    // Updated columns: ID, Date, Time, Patient Name, Clinician Name, Facility, Reason, Status
    private static final String[] APPOINTMENT_COLUMN_NAMES = {
        "ID", "Date", "Time", "Patient Name", "Clinician Name", "Facility", "Reason", "Status"
    };
    
    public DashboardPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Create table model with updated columns
        appointmentsTableModel = new DefaultTableModel(APPOINTMENT_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentsTable = new JTable(appointmentsTableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        
        // Search and filter controls
        txtSearchID = new JTextField(15);
        txtSearchID.setToolTipText("Search by Appointment ID");
        txtDateFilter = new JTextField(15);
        txtDateFilter.setToolTipText("Filter by date (YYYY-MM-DD)");
        btnSearch = new JButton("Search");
        
        // Action buttons
        btnUpdateStatus = new JButton("Update Status");
        btnReschedule = new JButton("Reschedule");
        btnShowToday = new JButton("Show Today");
        refreshButton = new JButton("Refresh");
        
        // Disable buttons until row is selected
        btnUpdateStatus.setEnabled(false);
        btnReschedule.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel with title and search/filter controls
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // Title at the very top
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JLabel titleLabel = new JLabel("My Appointments");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titlePanel.add(titleLabel);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        topPanel.add(titlePanel);
        
        // Search and filter panel
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchFilterPanel.setBorder(BorderFactory.createTitledBorder("Search & Filter"));
        searchFilterPanel.add(new JLabel("Search ID:"));
        searchFilterPanel.add(txtSearchID);
        searchFilterPanel.add(new JLabel("Date Filter:"));
        searchFilterPanel.add(txtDateFilter);
        searchFilterPanel.add(btnSearch);
        searchFilterPanel.add(btnShowToday);
        searchFilterPanel.add(refreshButton);
        
        topPanel.add(searchFilterPanel);
        add(topPanel, BorderLayout.NORTH);
        
        // Appointments table - full width
        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsTable);
        appointmentsScrollPane.setBorder(BorderFactory.createTitledBorder("Today's Appointments"));
        add(appointmentsScrollPane, BorderLayout.CENTER);
        
        // Action buttons panel at bottom
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        actionPanel.add(btnUpdateStatus);
        actionPanel.add(btnReschedule);
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    // Method to add appointment row with all required fields
    public void clearAppointments() {
        appointmentsTableModel.setRowCount(0);
    }
    
    public void addAppointmentRow(String id, String date, String time, String patientName, 
                                  String clinicianName, String facility, String reason, String status) {
        appointmentsTableModel.addRow(new Object[]{id, date, time, patientName, clinicianName, facility, reason, status});
    }
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public JTable getAppointmentsTable() {
        return appointmentsTable;
    }
    
    public JTextField getTxtSearchID() {
        return txtSearchID;
    }
    
    public JTextField getTxtDateFilter() {
        return txtDateFilter;
    }
    
    public JButton getBtnUpdateStatus() {
        return btnUpdateStatus;
    }
    
    public JButton getBtnReschedule() {
        return btnReschedule;
    }
    
    public JButton getBtnShowToday() {
        return btnShowToday;
    }
    
    public JButton getBtnSearch() {
        return btnSearch;
    }
    
    public void setUpdateStatusButtonEnabled(boolean enabled) {
        btnUpdateStatus.setEnabled(enabled);
    }
    
    public void setRescheduleButtonEnabled(boolean enabled) {
        btnReschedule.setEnabled(enabled);
    }
    
    public String getSelectedAppointmentId() {
        int row = appointmentsTable.getSelectedRow();
        if (row >= 0) {
            return (String) appointmentsTableModel.getValueAt(row, 0); // Column 0 is ID
        }
        return null;
    }
}
