package controller;

import model.*;
import repository.PatientRepository;
import repository.ReferralRepository;
import repository.StaffRepository;
import view.LoginView;
import view.NavigationCard;
import view.MedicalRecordPanel;
import view.PatientManagementPanel;
import view.PatientDashboardPanel;
import view.StaffManagementPanel;
import view.AppointmentPanel;
import javax.swing.*;

public class LoginController {
    
    private final LoginView view;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final FacilityRepository facilityRepository;
    private final ReferralRepository referralRepository;
    
    public LoginController(LoginView view,
                          PatientRepository patientRepository,
                          StaffRepository staffRepository,
                          ClinicianRepository clinicianRepository,
                          AppointmentRepository appointmentRepository,
                          PrescriptionRepository prescriptionRepository,
                          FacilityRepository facilityRepository,
                          ReferralRepository referralRepository) {
        this.view = view;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.clinicianRepository = clinicianRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.facilityRepository = facilityRepository;
        this.referralRepository = referralRepository;
        
        bind();
    }
    
    private void bind() {
        view.getLoginButton().addActionListener(e -> handleLogin());
        view.getPasswordField().addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String username = view.getUsername().trim();
        String password = view.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Hard-coded password for everyone
        if (!password.equals("12345")) {
            System.out.println("Login Failed: Invalid password");
            JOptionPane.showMessageDialog(view, "Invalid password. Use '12345' for testing.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Determine role based on username - take the username string exactly as typed
        String role = null;
        String patientId = null;
        
        // First, check if the username matches a record in PatientRepository (e.g., P001)
        Patient patient = patientRepository.findById(username);
        if (patient != null) {
            // If it matches a patient, assign the role "Patient"
            role = "Patient";
            patientId = username; // Use exact string (e.g., "P001")
            System.out.println("Login: Found patient with ID: " + patientId);
        } else {
            // Second, check if the username matches a Clinician ID (e.g., C001)
            Clinician clinician = clinicianRepository.findById(username);
            if (clinician != null) {
                // If it matches a clinician, assign the role "Clinician"
                role = "Clinician";
                System.out.println("Login: Found clinician with ID: " + username);
            } else {
                // Third, check if it matches a Staff record (e.g., ST001)
                Staff staff = staffRepository.findStaffById(username);
                if (staff != null) {
                    String staffRole = staff.getRole();
                    if ("Clinician".equalsIgnoreCase(staffRole) || "GP".equalsIgnoreCase(staffRole) || 
                        "Consultant".equalsIgnoreCase(staffRole) || "Nurse".equalsIgnoreCase(staffRole)) {
                        role = "Clinician";
                    } else if ("Practice Manager".equalsIgnoreCase(staffRole) || 
                               "Hospital Administrator".equalsIgnoreCase(staffRole)) {
                        role = "Admin";
                    } else if ("Receptionist".equalsIgnoreCase(staffRole)) {
                        role = "Receptionist";
                    } else {
                        role = "Admin"; // Default for other staff roles
                    }
                    System.out.println("Login: Found staff with ID: " + username + ", Role: " + role);
                } else {
                    // Finally, check for hardcoded role names (backward compatibility)
                    if (username.equalsIgnoreCase("Clinician")) {
                        role = "Clinician";
                    } else if (username.equalsIgnoreCase("Admin")) {
                        role = "Admin";
                    } else if (username.equalsIgnoreCase("Receptionist")) {
                        role = "Receptionist";
                    } else if (username.equalsIgnoreCase("Developer")) {
                        role = "Developer";
                    } else {
                        System.out.println("Login Failed: Invalid username - " + username);
                        JOptionPane.showMessageDialog(view, 
                            "Invalid username. Accepted formats:\n" +
                            "- Patient ID: P001, P002, etc.\n" +
                            "- Clinician ID: C001, C002, etc.\n" +
                            "- Staff ID: ST001, ST002, etc.\n" +
                            "- Role names: Clinician, Admin, Receptionist, Developer", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }
        
        // Close the login window
        view.dispose();
        
        // Launch NavigationCard in a JFrame
        JFrame frame = new JFrame("Hospital System - " + role);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NavigationCard navigationCard = new NavigationCard(role);
        frame.setContentPane(navigationCard); // Ensure this is the content
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        // Initialize controllers for the panels in NavigationCard
        // Pass patientId so PatientDashboardPanel can load the correct data
        initializeControllers(navigationCard, patientId);
        
        System.out.println("NavigationCard launched for role: " + role + (patientId != null ? " (Patient ID: " + patientId + ")" : ""));
    }
    
    /**
     * Initialize controllers for the panels in NavigationCard based on role.
     */
    private void initializeControllers(NavigationCard navigationCard, String patientId) {
        String role = navigationCard.getRole();
        
        // Initialize MedicalRecordPanel controller (for Clinician and Developer)
        if ("Clinician".equalsIgnoreCase(role) || "Developer".equalsIgnoreCase(role)) {
            MedicalRecordPanel medicalPanel = navigationCard.getMedicalRecordPanel();
            if (medicalPanel != null) {
                new MedicalRecordController(
                    medicalPanel,
                    patientRepository,
                    appointmentRepository,
                    prescriptionRepository,
                    clinicianRepository,
                    facilityRepository
                );
            }
        }
        
        // Initialize AppointmentPanel controller (for Receptionist, Clinician, and Developer)
        if ("Receptionist".equalsIgnoreCase(role) || "Clinician".equalsIgnoreCase(role) || "Developer".equalsIgnoreCase(role)) {
            AppointmentPanel appointmentPanel = navigationCard.getAppointmentPanel();
            if (appointmentPanel != null) {
                new AppointmentController(
                    appointmentPanel,
                    appointmentRepository,
                    patientRepository,
                    clinicianRepository,
                    facilityRepository
                );
            }
        }
        
        // Initialize PatientManagementPanel controller (for Admin, Receptionist, Clinician, and Developer)
        if ("Admin".equalsIgnoreCase(role) || "Receptionist".equalsIgnoreCase(role) || 
            "Clinician".equalsIgnoreCase(role) || "Developer".equalsIgnoreCase(role)) {
            PatientManagementPanel patientMgmtPanel = navigationCard.getPatientManagementPanel();
            if (patientMgmtPanel != null) {
                new PatientManagementController(
                    patientMgmtPanel,
                    patientRepository
                );
            }
        }
        
        // Initialize StaffManagementPanel controller (for Admin and Developer)
        if ("Admin".equalsIgnoreCase(role) || "Developer".equalsIgnoreCase(role)) {
            StaffManagementPanel staffPanel = navigationCard.getStaffManagementPanel();
            if (staffPanel != null) {
                new StaffManagementController(
                    staffPanel,
                    staffRepository,
                    clinicianRepository
                );
            }
        }
        
        // Initialize PatientDashboardPanel controller (for Patient and Developer)
        if ("Patient".equalsIgnoreCase(role) || "Developer".equalsIgnoreCase(role)) {
            PatientDashboardPanel patientDashPanel = navigationCard.getPatientDashboardPanel();
            if (patientDashPanel != null) {
                // For Patient role, use the exact patientId from login (e.g., "P001")
                if ("Patient".equalsIgnoreCase(role) && patientId != null) {
                    // Pass the exact string (e.g., "P001") to the controller
                    System.out.println("Initializing PatientDashboardController with Patient ID: " + patientId);
                    new PatientDashboardController(
                        patientDashPanel,
                        appointmentRepository,
                        prescriptionRepository,
                        patientRepository,
                        clinicianRepository,
                        facilityRepository,
                        patientId // Pass exact string
                    );
                } else if ("Developer".equalsIgnoreCase(role)) {
                    // For Developer, just set a placeholder name
                    patientDashPanel.setPatientName("Developer View");
                }
            }
        }
    }
}
