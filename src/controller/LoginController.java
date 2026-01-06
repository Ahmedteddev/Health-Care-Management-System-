package controller;

import model.*;
import view.*;
import javax.swing.*;
import java.awt.*;

public class LoginController {
    
    private final LoginView loginScreen;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final FacilityRepository facilityRepository;
    
    public LoginController(LoginView loginScreen,
                          PatientRepository patientRepository,
                          StaffRepository staffRepository,
                          ClinicianRepository clinicianRepository,
                          AppointmentRepository appointmentRepository,
                          PrescriptionRepository prescriptionRepository,
                          FacilityRepository facilityRepository) {
        this.loginScreen = loginScreen;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.clinicianRepository = clinicianRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.facilityRepository = facilityRepository;
        
        connectButtonsToActions();
    }
    
    private void connectButtonsToActions() {
        loginScreen.getLoginButton().addActionListener(e -> handleLogin());
        loginScreen.getPasswordField().addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String enteredUsername = loginScreen.getUsername().trim();
        String enteredPassword = loginScreen.getPassword();
        
        // 1. Basic Validation
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            JOptionPane.showMessageDialog(loginScreen, "Please enter both credentials.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 2. Password Check (Universal test password)
        if (!enteredPassword.equals("12345")) {
            JOptionPane.showMessageDialog(loginScreen, "Invalid password. (Hint: 12345)", "Access Denied", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. Determine User Role and Identity
        String userRole = null;
        String thePatientId = null;

        // Check Patients
        Patient foundPatient = patientRepository.findById(enteredUsername);
        if (foundPatient != null) {
            userRole = "Patient";
            thePatientId = foundPatient.getPatientId();
        } else {
            // Check Clinicians Table directly
            Clinician foundClinician = clinicianRepository.findById(enteredUsername);
            if (foundClinician != null) {
                userRole = "Clinician";
            } else {
                // Check General Staff Table
                Staff foundStaff = staffRepository.findStaffById(enteredUsername);
                if (foundStaff != null) {
                    userRole = mapStaffToRole(foundStaff.getRole());
                } else {
                    // Developer/Backdoor Roles
                    userRole = handleDeveloperRoles(enteredUsername);
                }
            }
        }

        // 4. Final Validation
        if (userRole == null) {
            JOptionPane.showMessageDialog(loginScreen, "User ID '" + enteredUsername + "' not found.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 5. Successful Login -> Launch Main UI
        launchMainApplication(userRole, thePatientId);
    }

    private String mapStaffToRole(String jobTitle) {
        if (jobTitle == null) return "Admin";
        String title = jobTitle.toLowerCase();
        
        if (title.contains("clinician") || title.contains("nurse") || title.contains("gp") || title.contains("doctor")) {
            return "Clinician";
        } else if (title.contains("reception")) {
            return "Receptionist";
        } else {
            return "Admin";
        }
    }

    private String handleDeveloperRoles(String username) {
        String u = username.toLowerCase();
        if (u.equals("admin")) return "Admin";
        if (u.equals("clinician")) return "Clinician";
        if (u.equals("receptionist")) return "Receptionist";
        if (u.equals("developer")) return "Developer";
        return null;
    }

    private void launchMainApplication(String role, String patientId) {
        loginScreen.dispose();
        
        JFrame mainWindow = new JFrame("Hospital Management System - [" + role + "]");
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        NavigationCard navigationCard = new NavigationCard(role);
        mainWindow.setContentPane(navigationCard);
        
        // Initialize all controllers with the shared repositories
        setupAllTheControllers(navigationCard, role, patientId);
        
        mainWindow.setSize(1300, 850);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }
    
    private void setupAllTheControllers(NavigationCard nav, String role, String patientId) {
        // Patient Management (Visible to Admin, Receptionist, Clinician, Developer)
        if (nav.getPatientManagementPanel() != null) {
            new PatientManagementController(nav.getPatientManagementPanel(), patientRepository);
        }
        
        // Appointments
        if (nav.getAppointmentPanel() != null) {
            new AppointmentController(nav.getAppointmentPanel(), appointmentRepository, 
                patientRepository, clinicianRepository, facilityRepository);
        }
        
        // Medical Records
        if (nav.getMedicalRecordPanel() != null) {
            new MedicalRecordController(nav.getMedicalRecordPanel(), patientRepository, 
                appointmentRepository, prescriptionRepository, clinicianRepository, facilityRepository);
        }
        
        // Staff Management
        if (nav.getStaffManagementPanel() != null) {
            new StaffManagementController(nav.getStaffManagementPanel(), staffRepository, clinicianRepository);
        }
        
        // Patient Dashboard (Specific to logged-in Patient)
        if (nav.getPatientDashboardPanel() != null) {
            if ("Patient".equalsIgnoreCase(role) && patientId != null) {
                new PatientDashboardController(nav.getPatientDashboardPanel(), appointmentRepository, 
                    prescriptionRepository, patientRepository, clinicianRepository, facilityRepository, patientId);
            } else if ("Developer".equalsIgnoreCase(role)) {
                nav.getPatientDashboardPanel().setPatientName("Developer Mode");
            }
        }
    }
}