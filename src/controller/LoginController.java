package controller;

import model.*;
import repository.ReferralRepository;
import repository.StaffRepository;
import view.LoginView;
import view.GPDashboard;
import view.PatientDashboardFrame;
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
        
        // Enter key in password field also triggers login
        view.getPasswordField().addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            view.setStatusMessage("Please enter both username and password.");
            return;
        }
        
        // Universal testing password: "12345" for all accounts
        if (!password.equals("12345")) {
            view.setStatusMessage("Invalid password. Use '12345' for testing.");
            return;
        }
        
        // Check if user is in StaffRepository
        Staff staff = findStaffByUsername(username);
        if (staff != null) {
            // Identify role
            String role = identifyRole(staff);
            // Launch DashboardController with role
            launchStaffDashboard(staff, role);
            view.dispose();
            return;
        }
        
        // Check if user is in PatientRepository (using ID or surname)
        Patient patient = findPatientByUsername(username);
        if (patient != null) {
            // Launch PatientDashboardController
            launchPatientDashboard(patient.getPatientId());
            view.dispose();
            return;
        }
        
        // Login failed
        view.setStatusMessage("Invalid username or password.");
        view.clearFields();
    }
    
    /**
     * Identify the role of a staff member.
     * Returns: "Developer", "GP", "Specialist", "Nurse", "Admin", "Receptionist", or "Staff"
     */
    private String identifyRole(Staff staff) {
        // Check for Developer role first
        String role = staff.getRole();
        if ("Developer".equalsIgnoreCase(role) || "DEV001".equalsIgnoreCase(staff.getStaffId())) {
            return "Developer";
        }
        
        if (staff instanceof Clinician) {
            Clinician clinician = (Clinician) staff;
            String title = clinician.getTitle();
            String speciality = clinician.getSpeciality();
            
            if (title != null && title.toLowerCase().contains("nurse")) {
                return "Nurse";
            } else if (speciality != null && (speciality.toLowerCase().contains("specialist") || 
                                               speciality.toLowerCase().contains("consultant"))) {
                return "Specialist";
            } else if (title != null && title.equalsIgnoreCase("GP")) {
                return "GP";
            } else {
                // Default for clinicians
                return "GP";
            }
        }
        
        // Check role from Staff
        if (role != null) {
            String roleLower = role.toLowerCase();
            if (roleLower.contains("admin") || roleLower.contains("administrator") || 
                roleLower.contains("manager") && roleLower.contains("practice")) {
                return "Admin";
            } else if (roleLower.contains("receptionist")) {
                return "Receptionist";
            } else if (roleLower.contains("nurse")) {
                return "Nurse";
            } else if (roleLower.contains("gp") || roleLower.contains("general practitioner")) {
                return "GP";
            } else if (roleLower.contains("specialist") || roleLower.contains("consultant")) {
                return "Specialist";
            }
        }
        
        // Default to staff role or "Staff"
        return role != null ? role : "Staff";
    }
    
    private Staff findStaffByUsername(String username) {
        // Check for Developer role (DEV001)
        if ("DEV001".equalsIgnoreCase(username)) {
            // Create a Developer staff object
            return createDeveloperStaff();
        }
        
        // Search in StaffRepository
        for (Staff staff : staffRepository.getAllStaff()) {
            if (username.equalsIgnoreCase(staff.getStaffId()) || 
                username.equalsIgnoreCase(staff.getEmail()) ||
                username.equalsIgnoreCase(staff.getFirstName() + " " + staff.getLastName())) {
                return staff;
            }
        }
        
        // Also check in ClinicianRepository
        for (Clinician clinician : clinicianRepository.getAll()) {
            if (username.equalsIgnoreCase(clinician.getClinicianId()) ||
                username.equalsIgnoreCase(clinician.getEmail()) ||
                username.equalsIgnoreCase(clinician.getFullName())) {
                return clinician; // Clinician extends Staff
            }
        }
        
        return null;
    }
    
    /**
     * Create a Developer staff object for super-user access.
     */
    private Staff createDeveloperStaff() {
        // Create a temporary Staff object with Developer role
        Staff developer = new Staff();
        developer.setStaffId("DEV001");
        developer.setFirstName("Developer");
        developer.setLastName("System");
        developer.setRole("Developer");
        developer.setEmail("dev@hms.system");
        developer.setDepartment("IT");
        developer.setAccessLevel("Super");
        return developer;
    }
    
    private Patient findPatientByUsername(String username) {
        // Search by Patient ID
        Patient patient = patientRepository.findById(username);
        if (patient != null) {
            return patient;
        }
        
        // Search by surname (last name)
        for (Patient p : patientRepository.getAll()) {
            if (username.equalsIgnoreCase(p.getLastName()) ||
                username.equalsIgnoreCase(p.getEmail())) {
                return p;
            }
        }
        
        return null;
    }
    
    private void launchStaffDashboard(Staff staff, String role) {
        // Find the clinician if it's a clinician
        Clinician clinician = null;
        if (staff instanceof Clinician) {
            clinician = (Clinician) staff;
        } else {
            // Try to find in ClinicianRepository by ID
            clinician = clinicianRepository.findById(staff.getStaffId());
        }
        
        if (clinician == null) {
            // If not a clinician, create a default clinician or handle differently
            // For now, we'll use the first available clinician
            if (!clinicianRepository.getAll().isEmpty()) {
                clinician = clinicianRepository.getAll().get(0);
            } else {
                JOptionPane.showMessageDialog(view, 
                    "No clinician found. Please contact administrator.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        // Create GP Dashboard
        GPDashboard dashboard = new GPDashboard(clinician);
        
        // Create Dashboard Controller with role
        DashboardController dashboardController = new DashboardController(
            dashboard,
            appointmentRepository,
            patientRepository,
            clinicianRepository,
            facilityRepository,
            prescriptionRepository,
            referralRepository,
            staffRepository,
            role  // Pass the role string
        );
        
        // Create Appointment Controller
        AppointmentController appointmentController = new AppointmentController(
            dashboardController.getAppointmentPanel(),
            appointmentRepository,
            patientRepository,
            clinicianRepository,
            facilityRepository
        );
        
        dashboard.setVisible(true);
        
        String staffName = staff.getFirstName() + " " + staff.getLastName();
        System.out.println("Staff Dashboard launched for: " + staffName + " (Role: " + role + ")");
    }
    
    private void launchPatientDashboard(String patientId) {
        // Create Patient Dashboard
        PatientDashboardFrame patientDashboard = new PatientDashboardFrame();
        
        // Create Patient Dashboard Controller with logged-in patient ID
        PatientDashboardController patientController = new PatientDashboardController(
            patientDashboard,
            appointmentRepository,
            prescriptionRepository,
            patientRepository,
            clinicianRepository,
            facilityRepository,
            patientId  // Pass the logged-in patient ID
        );
        
        patientDashboard.setVisible(true);
        
        System.out.println("Patient Dashboard launched for: " + patientId);
    }
}

