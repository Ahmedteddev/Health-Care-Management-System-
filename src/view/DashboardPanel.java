package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class DashboardPanel extends JPanel {
    
    private JTable appointmentsTable;
    private DefaultTableModel appointmentsTableModel;
    private JTable historyTable;
    private DefaultTableModel historyTableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton refreshButton;
    
    private static final String[] APPOINTMENT_COLUMN_NAMES = {
        "Time", "Patient ID", "Patient Name", "Reason", "Status"
    };
    
    private static final String[] HISTORY_COLUMN_NAMES = {
        "Date", "Clinician", "Facility", "Reason", "Outcome"
    };
    
    public DashboardPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        appointmentsTableModel = new DefaultTableModel(APPOINTMENT_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentsTable = new JTable(appointmentsTableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        
        historyTableModel = new DefaultTableModel(HISTORY_COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        historyTable = new JTable(historyTableModel);
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setRowHeight(25);
        
        searchField = new JTextField(15);
        searchButton = new JButton("Search");
        refreshButton = new JButton("Refresh");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        
        JScrollPane appointmentsScrollPane = new JScrollPane(appointmentsTable);
        appointmentsScrollPane.setBorder(BorderFactory.createTitledBorder("My Appointments"));
        appointmentsScrollPane.setPreferredSize(new Dimension(0, 300));
        centerPanel.add(appointmentsScrollPane);
        
        JPanel searchPanel = createSearchPanel();
        centerPanel.add(Box.createVerticalStrut(15));
        centerPanel.add(searchPanel);
        centerPanel.add(Box.createVerticalStrut(15));
        
        JScrollPane historyScrollPane = new JScrollPane(historyTable);
        historyScrollPane.setBorder(BorderFactory.createTitledBorder("Patient Medical History"));
        historyScrollPane.setPreferredSize(new Dimension(0, 300));
        centerPanel.add(historyScrollPane);
        
        add(centerPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.add(refreshButton, BorderLayout.EAST);
        return headerPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Patient Search"));
        searchPanel.add(new JLabel("Patient ID:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        return searchPanel;
    }
    
    public void clearAppointments() {
        appointmentsTableModel.setRowCount(0);
    }
    
    public void addAppointmentRow(String time, String patientId, String patientName, 
                                  String reason, String status) {
        appointmentsTableModel.addRow(new Object[]{time, patientId, patientName, reason, status});
    }
    
    public void clearHistory() {
        historyTableModel.setRowCount(0);
    }
    
    public void addHistoryRow(String date, String clinician, String facility, 
                             String reason, String outcome) {
        historyTableModel.addRow(new Object[]{date, clinician, facility, reason, outcome});
    }
    
    public String getSelectedPatientId() {
        int row = appointmentsTable.getSelectedRow();
        if (row >= 0) {
            return (String) appointmentsTableModel.getValueAt(row, 1);
        }
        return null;
    }
    
    public String getSearchFieldText() {
        return searchField.getText().trim();
    }
    
    public void clearSearchField() {
        searchField.setText("");
    }
    
    public JButton getRefreshButton() {
        return refreshButton;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JTable getAppointmentsTable() {
        return appointmentsTable;
    }
}

