package controller;

import model.*;
import view.*;
import javax.swing.*;

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
        // 1. Get and clean inputs
        String enteredUsername = loginScreen.getUsername().trim();
        String enteredPassword = loginScreen.getPassword();
        
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            JOptionPane.showMessageDialog(loginScreen, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // 2. Validate Password 
        if (!enteredPassword.equals("12345")) {
            System.out.println("Login Failed: Invalid password");
            JOptionPane.showMessageDialog(loginScreen, "Invalid password. Use '12345' for testing.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3. REFRESH DATA (Crucial for new registrations)
        // This ensures that if a patient just registered, they are now in the list
        patientRepository.refresh(); 
        
        String userRole = null;
        String thePatientId = null;

        // 4. Check for Patient Role
        // We use the findById we fixed earlier which handles whitespace and case
        Patient foundPatient = patientRepository.findById(enteredUsername);
        
        if (foundPatient != null) {
            userRole = "Patient";
            thePatientId = foundPatient.getPatientId(); // Use the actual ID from the object
            System.out.println("Login: Found patient with ID: " + thePatientId);
        } else {
            // 5. Check for Clinician Role
            Clinician foundClinician = clinicianRepository.findById(enteredUsername);
            if (foundClinician != null) {
                userRole = "Clinician";
                System.out.println("Login: Found clinician with ID: " + enteredUsername);
            } else {
                // 6. Check for Staff Role
                Staff foundStaff = staffRepository.findStaffById(enteredUsername);
                if (foundStaff != null) {
                    String staffJobTitle = foundStaff.getRole();
                    
                    if ("Clinician".equalsIgnoreCase(staffJobTitle) || "GP".equalsIgnoreCase(staffJobTitle) || 
                        "Consultant".equalsIgnoreCase(staffJobTitle) || "Nurse".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Clinician";
                    } else if ("Practice Manager".equalsIgnoreCase(staffJobTitle) || 
                               "Hospital Administrator".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Admin";
                    } else if ("Receptionist".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Receptionist";
                    } else {
                        userRole = "Admin";
                    }
                    System.out.println("Login: Found staff with ID: " + enteredUsername + ", Role: " + userRole);
                } else {
                    // 7. Hardcoded Testing Roles
                    if (enteredUsername.equalsIgnoreCase("Clinician")) {
                        userRole = "Clinician";
                    } else if (enteredUsername.equalsIgnoreCase("Admin")) {
                        userRole = "Admin";
                    } else if (enteredUsername.equalsIgnoreCase("Receptionist")) {
                        userRole = "Receptionist";
                    } else if (enteredUsername.equalsIgnoreCase("Developer")) {
                        userRole = "Developer";
                    }
                }
            }
        }

        // 8. Final Validation
        if (userRole == null) {
            System.out.println("Login Failed: User not found - " + enteredUsername);
            JOptionPane.showMessageDialog(loginScreen, 
                "User ID not found. Ensure you are using the correct ID (e.g., P001).", 
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // 9. Launch Application
        loginScreen.dispose();
        
        JFrame mainWindow = new JFrame("Hospital System - " + userRole);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NavigationCard navigationCard = new NavigationCard(userRole);
        mainWindow.setContentPane(navigationCard);
        mainWindow.setSize(1200, 800);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        
        setupAllTheControllers(navigationCard, thePatientId);
    }
    
    private void setupAllTheControllers(NavigationCard navigationCard, String thePatientId) {
        String userRole = navigationCard.getRole();
        
        // Medical Records
        if ("Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            MedicalRecordPanel medicalPanel = navigationCard.getMedicalRecordPanel();
            if (medicalPanel != null) {
                new MedicalRecordController(medicalPanel, patientRepository, appointmentRepository, 
                                          prescriptionRepository, clinicianRepository, facilityRepository);
            }
        }
        
        // Appointments
        if ("Receptionist".equalsIgnoreCase(userRole) || "Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            AppointmentPanel appointmentPanel = navigationCard.getAppointmentPanel();
            if (appointmentPanel != null) {
                new AppointmentController(appointmentPanel, appointmentRepository, patientRepository, 
                                         clinicianRepository, facilityRepository);
            }
        }
        
        // Patient Management
        if ("Admin".equalsIgnoreCase(userRole) || "Receptionist".equalsIgnoreCase(userRole) || 
            "Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            PatientManagementPanel patientMgmtPanel = navigationCard.getPatientManagementPanel();
            if (patientMgmtPanel != null) {
                new PatientManagementController(patientMgmtPanel, patientRepository);
            }
        }
        
        // Staff Management
        if ("Admin".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            StaffManagementPanel staffPanel = navigationCard.getStaffManagementPanel();
            if (staffPanel != null) {
                new StaffManagementController(staffPanel, staffRepository, clinicianRepository);
            }
        }
        
        // Patient Dashboard
        if ("Patient".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            PatientDashboardPanel patientDashPanel = navigationCard.getPatientDashboardPanel();
            if (patientDashPanel != null) {
                if ("Patient".equalsIgnoreCase(userRole) && thePatientId != null) {
                    new PatientDashboardController(patientDashPanel, appointmentRepository, prescriptionRepository, 
                                                 patientRepository, clinicianRepository, facilityRepository, thePatientId);
                } else if ("Developer".equalsIgnoreCase(userRole)) {
                    patientDashPanel.setPatientName("Developer View");
                }
            }
        }
    }
}