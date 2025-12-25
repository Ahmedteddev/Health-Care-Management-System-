package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AppointmentPanel extends JPanel {
    
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton searchButton;
    private JButton bookNewButton;
    private JButton rescheduleButton;
    private JButton editButton;
    private JButton cancelButton;
    
    // Updated columns: ID, Date, Time, Patient Name, Clinician Name, Facility, Reason, Status
    private static final String[] COLUMN_NAMES = {
        "ID", "Date", "Time", "Patient Name", "Clinician Name", "Facility", "Reason", "Status"
    };
    
    public AppointmentPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
        // Create table model with updated columns
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentsTable = new JTable(tableModel);
        appointmentsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentsTable.setRowHeight(25);
        appointmentsTable.getTableHeader().setReorderingAllowed(false);
        
        // Search components
        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{"Patient ID", "Patient Name", "Clinician ID", "Clinician Name"});
        searchButton = new JButton("Search");
        
        // Action buttons
        bookNewButton = new JButton("Book New");
        rescheduleButton = new JButton("Reschedule");
        editButton = new JButton("Edit");
        cancelButton = new JButton("Cancel");
        
        // Disable buttons until row is selected
        rescheduleButton.setEnabled(false);
        editButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Top panel with heading and search
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        
        // Heading
        JPanel headingPanel = createHeadingPanel();
        topPanel.add(headingPanel);
        topPanel.add(Box.createVerticalStrut(10));
        
        // Search Header
        JPanel searchPanel = createSearchPanel();
        topPanel.add(searchPanel);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Central Table (Center)
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Action Footer (South)
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createHeadingPanel() {
        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
        JLabel headingLabel = new JLabel("Appointment Management");
        headingLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        headingPanel.add(headingLabel);
        return headingPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(searchButton);
        
        return searchPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        
        actionPanel.add(bookNewButton);
        actionPanel.add(rescheduleButton);
        actionPanel.add(editButton);
        actionPanel.add(cancelButton);
        
        return actionPanel;
    }
    
    public void clearTable() {
        tableModel.setRowCount(0);
    }
    
    public void addRow(Object[] rowData) {
        tableModel.addRow(rowData);
    }
    
    public void refreshTable() {
        tableModel.fireTableDataChanged();
    }
    
    public int getSelectedRow() {
        return appointmentsTable.getSelectedRow();
    }
    
    public String getSelectedAppointmentId() {
        int row = appointmentsTable.getSelectedRow();
        if (row >= 0) {
            return (String) tableModel.getValueAt(row, 0); // Column 0 is ID
        }
        return null;
    }
    
    public void setRescheduleButtonEnabled(boolean enabled) {
        rescheduleButton.setEnabled(enabled);
    }
    
    public void setEditButtonEnabled(boolean enabled) {
        editButton.setEnabled(enabled);
    }
    
    public void setCancelButtonEnabled(boolean enabled) {
        cancelButton.setEnabled(enabled);
    }
    
    public JTextField getSearchField() {
        return searchField;
    }
    
    public JComboBox<String> getFilterComboBox() {
        return filterComboBox;
    }
    
    public JButton getSearchButton() {
        return searchButton;
    }
    
    public JButton getBookNewButton() {
        return bookNewButton;
    }
    
    public JButton getRescheduleButton() {
        return rescheduleButton;
    }
    
    public JButton getEditButton() {
        return editButton;
    }
    
    public JButton getCancelButton() {
        return cancelButton;
    }
    
    public JTable getTable() {
        return appointmentsTable;
    }
}
