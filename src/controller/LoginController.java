package controller;

import model.*;
import view.LoginView;
import view.NavigationCard;
import view.MedicalRecordPanel;
import view.PatientManagementPanel;
import view.PatientDashboardPanel;
import view.StaffManagementPanel;
import view.AppointmentPanel;
import javax.swing.*;

public class LoginController {
    
    // These are all the things the controller needs to work
    private final LoginView loginScreen;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final FacilityRepository facilityRepository;
    
    // Constructor - this sets up the controller when it's first created
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
        
        // Connect the buttons to the methods that handle clicks
        connectButtonsToActions();
    }
    
    // This method connects the login button and password field to the handleLogin method
    // When someone clicks the button or presses Enter in the password field, it calls handleLogin
    private void connectButtonsToActions() {
        loginScreen.getLoginButton().addActionListener(e -> handleLogin());
        loginScreen.getPasswordField().addActionListener(e -> handleLogin());
    }
    
    // This is the main method that runs when someone tries to log in
    private void handleLogin() {
        // Get what the user typed in the username and password fields
        // Using trim() to remove any extra spaces at the start or end
        String enteredUsername = loginScreen.getUsername().trim();
        String enteredPassword = loginScreen.getPassword();
        
        // Check if they actually typed something in both fields
        if (enteredUsername.isEmpty() || enteredPassword.isEmpty()) {
            JOptionPane.showMessageDialog(loginScreen, "Please enter both username and password.", "Login Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // For this coursework, everyone uses the same password: "12345"
        // In a real system, this would check against a database
        if (!enteredPassword.equals("12345")) {
            System.out.println("Login Failed: Invalid password");
            JOptionPane.showMessageDialog(loginScreen, "Invalid password. Use '12345' for testing.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Now we need to figure out what role this user has
        // We check different places to see if the username matches a patient, clinician, staff, etc.
        String userRole = null;
        String thePatientId = null;
        
        // First, check if the username is a patient ID (like P001, P002, etc.)
        // Checking if it's a patient by looking for the ID in the patient repository
        Patient foundPatient = patientRepository.findById(enteredUsername);
        if (foundPatient != null) {
            // If we found a patient, set their role to "Patient"
            userRole = "Patient";
            thePatientId = enteredUsername; // Keep the exact ID they typed (like "P001")
            System.out.println("Login: Found patient with ID: " + thePatientId);
        } else {
            // If it's not a patient, check if it's a clinician ID (like C001, C002, etc.)
            Clinician foundClinician = clinicianRepository.findById(enteredUsername);
            if (foundClinician != null) {
                // If we found a clinician, set their role to "Clinician"
                userRole = "Clinician";
                System.out.println("Login: Found clinician with ID: " + enteredUsername);
            } else {
                // If it's not a clinician, check if it's a staff member (like ST001, ST002, etc.)
                Staff foundStaff = staffRepository.findStaffById(enteredUsername);
                if (foundStaff != null) {
                    // Staff members have different roles, so we need to check what their job is
                    String staffJobTitle = foundStaff.getRole();
                    // Check what kind of staff member they are and assign the right role
                    if ("Clinician".equalsIgnoreCase(staffJobTitle) || "GP".equalsIgnoreCase(staffJobTitle) || 
                        "Consultant".equalsIgnoreCase(staffJobTitle) || "Nurse".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Clinician";
                    } else if ("Practice Manager".equalsIgnoreCase(staffJobTitle) || 
                               "Hospital Administrator".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Admin";
                    } else if ("Receptionist".equalsIgnoreCase(staffJobTitle)) {
                        userRole = "Receptionist";
                    } else {
                        // If we don't recognize the job title, default to Admin
                        userRole = "Admin";
                    }
                    System.out.println("Login: Found staff with ID: " + enteredUsername + ", Role: " + userRole);
                } else {
                    // Last check: see if they typed a role name directly (for testing)
                    if (enteredUsername.equalsIgnoreCase("Clinician")) {
                        userRole = "Clinician";
                    } else if (enteredUsername.equalsIgnoreCase("Admin")) {
                        userRole = "Admin";
                    } else if (enteredUsername.equalsIgnoreCase("Receptionist")) {
                        userRole = "Receptionist";
                    } else if (enteredUsername.equalsIgnoreCase("Developer")) {
                        userRole = "Developer";
                    } else {
                        // If we can't find them anywhere, show an error
                        System.out.println("Login Failed: Invalid username - " + enteredUsername);
                        JOptionPane.showMessageDialog(loginScreen, 
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
        
        // Close the login window since we're moving to the main application
        loginScreen.dispose();
        
        // Create the main window that shows after login
        // This window will have different buttons and panels depending on the user's role
        JFrame mainWindow = new JFrame("Hospital System - " + userRole);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        NavigationCard navigationCard = new NavigationCard(userRole);
        mainWindow.setContentPane(navigationCard);
        mainWindow.setSize(1200, 800);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
        
        // Now we need to set up all the controllers for the different panels
        // This part hides the buttons that the user shouldn't see based on their job
        // We also pass the patient ID if they're a patient, so their dashboard shows their data
        setupAllTheControllers(navigationCard, thePatientId);
        
    }
    
    // This method sets up all the controllers for the different panels in the navigation
    // Each role sees different panels, so we only create controllers for the ones they can use
    private void setupAllTheControllers(NavigationCard navigationCard, String thePatientId) {
        String userRole = navigationCard.getRole();
        
        // If they're a Clinician or Developer, they can see the Medical Records panel
        if ("Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
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
        
        // Receptionists, Clinicians, and Developers can see the Appointments panel
        if ("Receptionist".equalsIgnoreCase(userRole) || "Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
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
        
        // Admins, Receptionists, Clinicians, and Developers can manage patients
        if ("Admin".equalsIgnoreCase(userRole) || "Receptionist".equalsIgnoreCase(userRole) || 
            "Clinician".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            PatientManagementPanel patientMgmtPanel = navigationCard.getPatientManagementPanel();
            if (patientMgmtPanel != null) {
                new PatientManagementController(
                    patientMgmtPanel,
                    patientRepository
                );
            }
        }
        
        // Only Admins and Developers can manage staff
        if ("Admin".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            StaffManagementPanel staffPanel = navigationCard.getStaffManagementPanel();
            if (staffPanel != null) {
                new StaffManagementController(
                    staffPanel,
                    staffRepository,
                    clinicianRepository
                );
            }
        }
        
        // Patients and Developers can see the Patient Dashboard
        if ("Patient".equalsIgnoreCase(userRole) || "Developer".equalsIgnoreCase(userRole)) {
            PatientDashboardPanel patientDashPanel = navigationCard.getPatientDashboardPanel();
            if (patientDashPanel != null) {
                // If they're a patient, we need to load their specific data using their patient ID
                if ("Patient".equalsIgnoreCase(userRole) && thePatientId != null) {
                    // Create the controller and pass the patient ID so it knows which patient's data to show
                    new PatientDashboardController(
                        patientDashPanel,
                        appointmentRepository,
                        prescriptionRepository,
                        patientRepository,
                        clinicianRepository,
                        facilityRepository,
                        thePatientId
                    );
                } else if ("Developer".equalsIgnoreCase(userRole)) {
                    // For developers, just show a placeholder message
                    patientDashPanel.setPatientName("Developer View");
                }
            }
        }
    }
}
