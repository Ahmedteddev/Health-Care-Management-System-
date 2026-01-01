package view;

import model.Clinician;
import javax.swing.*;
import java.awt.*;

public class GPDashboard extends JFrame {
    
    private final Clinician clinician;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JButton logoutButton;
    private JButton dashboardButton;
    private JButton appointmentsButton;
    private JButton medicalRecordsButton;
    private JButton manageStaffButton;
    private JButton managePatientsButton;
    
    public GPDashboard(Clinician clinician) {
        this.clinician = clinician;
        
        initializeComponents();
        setupLayout();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Healthcare Management System - " + clinician.getFullName());
        setSize(1200, 800);
        setLocationRelativeTo(null);
    }
    
    private void initializeComponents() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        
        logoutButton = new JButton("Logout");
        dashboardButton = new JButton("Dashboard");
        appointmentsButton = new JButton("Appointments");
        medicalRecordsButton = new JButton("Medical Records");
        manageStaffButton = new JButton("Manage Staff");
        managePatientsButton = new JButton("Manage Patients");
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header (North)
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Sidebar (West)
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Content Area (Center) with CardLayout
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(10, 5));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(240, 240, 240));
        
        String staffName = clinician.getFullName();
        String role = clinician.getTitle() != null ? clinician.getTitle() : "Clinician";
        String facility = clinician.getWorkplaceId() != null ? clinician.getWorkplaceId() : "N/A";
        
        JLabel nameLabel = new JLabel("Staff: " + staffName);
        nameLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        
        JLabel roleLabel = new JLabel("Role: " + role);
        roleLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        JLabel facilityLabel = new JLabel("Facility: " + facility);
        facilityLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(roleLabel);
        leftPanel.add(Box.createHorizontalStrut(20));
        leftPanel.add(facilityLabel);
        
        headerPanel.add(leftPanel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidebarPanel.setBackground(new Color(220, 220, 220));
        sidebarPanel.setPreferredSize(new Dimension(180, 0));
        
        // Navigation buttons - set alignment and size
        dashboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        appointmentsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        medicalRecordsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        manageStaffButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        managePatientsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dashboardButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        appointmentsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        medicalRecordsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        manageStaffButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        managePatientsButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        
        // Logout button - match style of appointmentsButton
        logoutButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.setPreferredSize(appointmentsButton.getPreferredSize());
        logoutButton.setBackground(appointmentsButton.getBackground());
        logoutButton.setForeground(appointmentsButton.getForeground());
        logoutButton.setFont(appointmentsButton.getFont());
        
        // Add logout button at the top (first button)
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(15));
        sidebarPanel.add(new JSeparator(JSeparator.HORIZONTAL));
        sidebarPanel.add(Box.createVerticalStrut(10));
        
        // Add navigation buttons
        sidebarPanel.add(dashboardButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(appointmentsButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(medicalRecordsButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(manageStaffButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        sidebarPanel.add(managePatientsButton);
        sidebarPanel.add(Box.createVerticalGlue());
        
        return sidebarPanel;
    }
    
    public void addCard(String cardName, JPanel panel) {
        contentPanel.add(panel, cardName);
    }
    
    public void showCard(String cardName) {
        cardLayout.show(contentPanel, cardName);
    }
    
    public JButton getLogoutButton() {
        return logoutButton;
    }
    
    public JButton getDashboardButton() {
        return dashboardButton;
    }
    
    public JButton getAppointmentsButton() {
        return appointmentsButton;
    }
    
    public JButton getMedicalRecordsButton() {
        return medicalRecordsButton;
    }
    
    public JButton getManageStaffButton() {
        return manageStaffButton;
    }
    
    public JButton getManagePatientsButton() {
        return managePatientsButton;
    }
    
    public Clinician getClinician() {
        return clinician;
    }
}
