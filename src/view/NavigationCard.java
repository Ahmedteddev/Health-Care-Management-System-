package view;

import javax.swing.*;
import java.awt.*;

/**
 * NavigationCard class handles role-based navigation via CardLayout.
 * Provides a sidebar navigation menu and a center workspace with multiple panels.
 */
public class NavigationCard extends JPanel {
    
    private final String role;
    private CardLayout cardLayout;
    private JPanel centerPanel;
    private JPanel sidebarPanel;
    
    // Navigation buttons
    private JButton medicalButton;
    private JButton patientMgmtButton;
    private JButton patientDashButton;
    private JButton appointmentsButton;
    private JButton staffButton;
    private JButton logoutButton;
    
    // Card names
    private static final String CARD_MEDICAL = "MEDICAL";
    private static final String CARD_PATIENT_MGMT = "PATIENT_MGMT";
    private static final String CARD_PATIENT_DASH = "PATIENT_DASH";
    private static final String CARD_APPOINTMENTS = "APPOINTMENTS";
    private static final String CARD_STAFF = "STAFF";
    
    /**
     * Constructor: Takes a String role (e.g., "Developer", "Clinician", "Admin", "Receptionist", "Patient").
     * If role is null or empty, defaults to "Developer" for testing.
     */
    public NavigationCard(String role) {
        // Set default role to "Developer" if null or empty
        if (role == null || role.trim().isEmpty()) {
            this.role = "Developer";
        } else {
            this.role = role;
        }
        
        initializeComponents();
        setupLayout();
        configureSidebar(this.role);
        
        // Force UI rendering
        this.revalidate();
        this.repaint();
        System.out.println("NavigationCard initialized for role: " + this.role);
    }
    
    /**
     * Initialize all components.
     */
    private void initializeComponents() {
        // Create CardLayout for center panel
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        
        // Create sidebar panel with vertical BoxLayout
        sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Clean border with padding
        sidebarPanel.setPreferredSize(new Dimension(200, 0));
        // Use standard background (null = default system background)
        
        // Create navigation buttons
        medicalButton = new JButton("Medical Records");
        patientMgmtButton = new JButton("Patient Management");
        patientDashButton = new JButton("Patient Dashboard");
        appointmentsButton = new JButton("Appointments");
        staffButton = new JButton("Staff Management");
        logoutButton = new JButton("Logout");
        
        // Style buttons
        styleButton(medicalButton);
        styleButton(patientMgmtButton);
        styleButton(patientDashButton);
        styleButton(appointmentsButton);
        styleButton(staffButton);
        styleButton(logoutButton);
        
        // Add action listeners
        medicalButton.addActionListener(e -> cardLayout.show(centerPanel, CARD_MEDICAL));
        patientMgmtButton.addActionListener(e -> cardLayout.show(centerPanel, CARD_PATIENT_MGMT));
        patientDashButton.addActionListener(e -> cardLayout.show(centerPanel, CARD_PATIENT_DASH));
        appointmentsButton.addActionListener(e -> cardLayout.show(centerPanel, CARD_APPOINTMENTS));
        staffButton.addActionListener(e -> cardLayout.show(centerPanel, CARD_STAFF));
        logoutButton.addActionListener(e -> handleLogout());
    }
    
    /**
     * Style a button for consistent appearance with uniform MAX_WIDTH.
     */
    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40)); // Consistent MAX_WIDTH
        button.setPreferredSize(new Dimension(180, 40));
        button.setMargin(new Insets(10, 10, 10, 10));
    }
    
    /**
     * Setup the main layout using BorderLayout.
     */
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Add sidebar to West
        add(sidebarPanel, BorderLayout.WEST);
        
        // Add center panel with CardLayout to Center
        add(centerPanel, BorderLayout.CENTER);
        
        // Add cards to CardLayout
        addCardsToLayout();
    }
    
    /**
     * Add all required cards to the CardLayout panel.
     */
    private void addCardsToLayout() {
        // MEDICAL: MedicalRecordPanel (Vertical scrollable version)
        MedicalRecordPanel medicalPanel = new MedicalRecordPanel();
        JScrollPane medicalScroll = new JScrollPane(medicalPanel);
        medicalScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        medicalScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(medicalScroll, CARD_MEDICAL);
        
        // PATIENT_MGMT: PatientManagementPanel (For staff)
        PatientManagementPanel patientMgmtPanel = new PatientManagementPanel();
        JScrollPane patientMgmtScroll = new JScrollPane(patientMgmtPanel);
        patientMgmtScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        patientMgmtScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(patientMgmtScroll, CARD_PATIENT_MGMT);
        
        // PATIENT_DASH: PatientDashboard (For the patient's own view)
        PatientDashboardPanel patientDashPanel = new PatientDashboardPanel();
        JScrollPane patientDashScroll = new JScrollPane(patientDashPanel);
        patientDashScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        patientDashScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(patientDashScroll, CARD_PATIENT_DASH);
        
        // APPOINTMENTS: AppointmentPanel
        AppointmentPanel appointmentsPanel = new AppointmentPanel();
        JScrollPane appointmentsScroll = new JScrollPane(appointmentsPanel);
        appointmentsScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        appointmentsScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(appointmentsScroll, CARD_APPOINTMENTS);
        
        // STAFF: StaffManagementPanel
        StaffManagementPanel staffPanel = new StaffManagementPanel();
        JScrollPane staffScroll = new JScrollPane(staffPanel);
        staffScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        staffScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        centerPanel.add(staffScroll, CARD_STAFF);
    }
    
    /**
     * Configure sidebar button visibility based on role.
     * Implement role-based visibility logic.
     */
    private void configureSidebar(String role) {
        // Clear sidebar first
        sidebarPanel.removeAll();
        
        // Add Menu label at the top in bold font
        JLabel menuLabel = new JLabel("Hospital System");
        menuLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(menuLabel);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Developer: All buttons visible
        if ("Developer".equalsIgnoreCase(role)) {
            sidebarPanel.add(medicalButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientMgmtButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientDashButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(appointmentsButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(staffButton);
        }
        // Clinician: Medical Records, Appointments, Patient Management
        else if ("Clinician".equalsIgnoreCase(role)) {
            sidebarPanel.add(medicalButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(appointmentsButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientMgmtButton);
        }
        // Admin: Patient Management, Staff Management
        else if ("Admin".equalsIgnoreCase(role)) {
            sidebarPanel.add(patientMgmtButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(staffButton);
        }
        // Receptionist: Appointments, Patient Management
        else if ("Receptionist".equalsIgnoreCase(role)) {
            sidebarPanel.add(appointmentsButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientMgmtButton);
        }
        // Patient: Only Patient Dashboard - ensure it's the only thing visible
        else if ("Patient".equalsIgnoreCase(role)) {
            sidebarPanel.add(patientDashButton);
            // Set the cardLayout to show PATIENT_DASH immediately
            cardLayout.show(centerPanel, CARD_PATIENT_DASH);
        }
        
        // Add spacer to push logout button to bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        // Logout button at bottom (always visible)
        sidebarPanel.add(Box.createVerticalStrut(20));
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Refresh sidebar
        sidebarPanel.revalidate();
        sidebarPanel.repaint();
    }
    
    /**
     * Handle logout action: Close current window and restart LoginView.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            // Get the parent JFrame and close it
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            if (parentFrame != null) {
                parentFrame.dispose();
            }
            
            // Restart LoginView
            SwingUtilities.invokeLater(() -> {
                LoginView loginView = new LoginView();
                loginView.setVisible(true);
            });
        }
    }
    
    /**
     * Get the center panel (for controller injection if needed).
     */
    public JPanel getCenterPanel() {
        return centerPanel;
    }
    
    /**
     * Get the CardLayout (for programmatic card switching if needed).
     */
    public CardLayout getCardLayout() {
        return cardLayout;
    }
    
    /**
     * Get the role.
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Show a specific card by name.
     */
    public void showCard(String cardName) {
        cardLayout.show(centerPanel, cardName);
    }
    
    /**
     * Get the MedicalRecordPanel (for controller initialization).
     */
    public MedicalRecordPanel getMedicalRecordPanel() {
        Component[] components = centerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof MedicalRecordPanel) {
                    return (MedicalRecordPanel) view;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the PatientManagementPanel (for controller initialization).
     */
    public PatientManagementPanel getPatientManagementPanel() {
        Component[] components = centerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof PatientManagementPanel) {
                    return (PatientManagementPanel) view;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the PatientDashboardPanel (for controller initialization).
     */
    public PatientDashboardPanel getPatientDashboardPanel() {
        Component[] components = centerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof PatientDashboardPanel) {
                    return (PatientDashboardPanel) view;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the AppointmentPanel (for controller initialization).
     */
    public AppointmentPanel getAppointmentPanel() {
        Component[] components = centerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof AppointmentPanel) {
                    return (AppointmentPanel) view;
                }
            }
        }
        return null;
    }
    
    /**
     * Get the StaffManagementPanel (for controller initialization).
     */
    public StaffManagementPanel getStaffManagementPanel() {
        Component[] components = centerPanel.getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                Component view = scrollPane.getViewport().getView();
                if (view instanceof StaffManagementPanel) {
                    return (StaffManagementPanel) view;
                }
            }
        }
        return null;
    }
}

