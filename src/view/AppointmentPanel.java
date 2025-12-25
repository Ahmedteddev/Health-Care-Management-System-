package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class AppointmentPanel extends JPanel {
    
    private JTable appointmentsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JButton bookNewButton;
    private JButton editButton;
    private JButton cancelButton;
    
    private static final String[] COLUMN_NAMES = {
        "ID", "Patient ID", "Patient Name", "Clinician ID", "Date", "Time", "Reason", "Status"
    };
    
    public AppointmentPanel() {
        initializeComponents();
        setupLayout();
    }
    
    private void initializeComponents() {
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
        
        searchField = new JTextField(20);
        filterComboBox = new JComboBox<>(new String[]{"Patient ID", "Patient Name", "Clinician ID"});
        
        bookNewButton = new JButton("Book New");
        editButton = new JButton("Edit/Reschedule");
        cancelButton = new JButton("Cancel");
        
        editButton.setEnabled(false);
        cancelButton.setEnabled(false);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Search Header (North)
        JPanel searchPanel = createSearchPanel();
        add(searchPanel, BorderLayout.NORTH);
        
        // Central Table (Center)
        JScrollPane tableScrollPane = new JScrollPane(appointmentsTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Appointments"));
        add(tableScrollPane, BorderLayout.CENTER);
        
        // Action Footer (South)
        JPanel actionPanel = createActionPanel();
        add(actionPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(filterComboBox);
        
        return searchPanel;
    }
    
    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        actionPanel.add(bookNewButton);
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
            return (String) tableModel.getValueAt(row, 0);
        }
        return null;
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
    
    public JButton getBookNewButton() {
        return bookNewButton;
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

