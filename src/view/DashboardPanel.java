package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {
    
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;
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
        
        refreshButton = new JButton("Refresh");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with refresh button
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Appointments table - full width (history table removed)
        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsTable);
        appointmentsScrollPane.setBorder(BorderFactory.createTitledBorder("Today's Appointments"));
        add(appointmentsScrollPane, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.add(refreshButton, BorderLayout.EAST);
        return headerPanel;
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
}
