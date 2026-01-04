package view;

import javax.swing.*;
import java.awt.*;

// This class creates the main navigation screen after someone logs in
// It shows different buttons and panels depending on what role the user has
// Uses CardLayout which is like having a stack of cards - you can only see one at a time
public class NavigationCard extends JPanel {
    
    // Store what role the logged-in user has (Patient, Clinician, Admin, etc.)
    private final String userRole;
    // CardLayout lets us switch between different panels like switching cards
    private CardLayout cardLayout;
    // This is the center area where different panels appear
    private JPanel centerPanel;
    // This is the sidebar on the left with all the navigation buttons
    private JPanel sidebarPanel;
    
    // All the buttons that appear in the sidebar
    private JButton medicalButton;
    private JButton patientMgmtButton;
    private JButton patientDashButton;
    private JButton appointmentsButton;
    private JButton staffButton;
    private JButton logoutButton;
    
    // These are the names we use to identify each panel in the CardLayout
    // When we want to show a panel, we use one of these names
    private static final String CARD_MEDICAL = "MEDICAL";
    private static final String CARD_PATIENT_MGMT = "PATIENT_MGMT";
    private static final String CARD_PATIENT_DASH = "PATIENT_DASH";
    private static final String CARD_APPOINTMENTS = "APPOINTMENTS";
    private static final String CARD_STAFF = "STAFF";
    
    // Constructor - this sets up the navigation when someone logs in
    // Takes the user's role so it knows which buttons to show
    public NavigationCard(String role) {
        // If no role was passed in, default to "Developer" for testing
        if (role == null || role.trim().isEmpty()) {
            this.userRole = "Developer";
        } else {
            this.userRole = role;
        }
        
        // Set up all the components (buttons, panels, etc.)
        initializeComponents();
        // Arrange everything on the screen
        setupLayout();
        // This part hides the buttons that the user shouldn't see based on their job
        configureSidebar(this.userRole);
        
        // Make sure everything displays correctly
        this.revalidate();
        this.repaint();
    }
    
    // This method creates all the buttons and panels we need
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
    
    private void styleButton(JButton button) {
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40)); // Consistent MAX_WIDTH
        button.setPreferredSize(new Dimension(180, 40));
        button.setMargin(new Insets(10, 10, 10, 10));
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout(10, 10));
        
        // Add sidebar to West
        add(sidebarPanel, BorderLayout.WEST);
        
        // Add center panel with CardLayout to Center
        add(centerPanel, BorderLayout.CENTER);
        
        // Add cards to CardLayout
        addCardsToLayout();
    }
    
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
    
    // This method decides which buttons to show based on the user's role
    // This part hides the buttons that the user shouldn't see based on their job
    private void configureSidebar(String role) {
        // First, remove everything from the sidebar so we can add the right buttons
        sidebarPanel.removeAll();
        
        // Add a title at the top that says "Hospital System" in bold
        JLabel menuLabel = new JLabel("Hospital System");
        menuLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(menuLabel);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Developers can see everything - all buttons are visible
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
        // Clinicians can see Medical Records, Appointments, and Patient Management
        else if ("Clinician".equalsIgnoreCase(role)) {
            sidebarPanel.add(medicalButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(appointmentsButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientMgmtButton);
        }
        // Admins can see Patient Management and Staff Management
        else if ("Admin".equalsIgnoreCase(role)) {
            sidebarPanel.add(patientMgmtButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(staffButton);
        }
        // Receptionists can see Appointments and Patient Management
        else if ("Receptionist".equalsIgnoreCase(role)) {
            sidebarPanel.add(appointmentsButton);
            sidebarPanel.add(Box.createVerticalStrut(10));
            sidebarPanel.add(patientMgmtButton);
        }
        // Patients can only see their own dashboard
        else if ("Patient".equalsIgnoreCase(role)) {
            sidebarPanel.add(patientDashButton);
            // Immediately show the patient dashboard when they log in
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
    
    // Close current window and restart LoginView
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
    
    public JPanel getCenterPanel() {
        return centerPanel;
    }
    
    public CardLayout getCardLayout() {
        return cardLayout;
    }
    
    // Get the user's role (Patient, Clinician, Admin, etc.)
    public String getRole() {
        return userRole;
    }
    
    public void showCard(String cardName) {
        cardLayout.show(centerPanel, cardName);
    }
    
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

