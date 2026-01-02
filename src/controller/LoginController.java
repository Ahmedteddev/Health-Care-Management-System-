package controller;

import model.*;
import repository.PatientRepository;
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
        view.getPasswordField().addActionListener(e -> handleLogin());
    }
    
    private void handleLogin() {
        String username = view.getUsername();
        String password = view.getPassword();
        
        if (username.isEmpty() || password.isEmpty()) {
            view.setStatusMessage("Please enter both username and password.");
            return;
        }
        
        // using universal password for testing - all accounts use "12345"
        if (!password.equals("12345")) {
            view.setStatusMessage("Invalid password. Use '12345' for testing.");
            return;
        }
        
        Staff staff = findStaffByUsername(username);
        if (staff != null) {
            String role = identifyRole(staff);
            launchStaffDashboard(staff, role);
            view.dispose();
            return;
        }
        
        Patient patient = findPatientByUsername(username);
        if (patient != null) {
            launchPatientDashboard(patient.getPatientId());
            view.dispose();
            return;
        }
        
        view.setStatusMessage("Invalid username or password.");
        view.clearFields();
    }
    
    // figures out what role the user is based on their title/role field - had to check a few different places
    private String identifyRole(Staff staff) {
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
                return "GP";
            }
        }
        
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
        
        return role != null ? role : "Staff";
    }
    
    private Staff findStaffByUsername(String username) {
        if ("DEV001".equalsIgnoreCase(username)) {
            return createDeveloperStaff();
        }
        
        for (Staff staff : staffRepository.getAllStaff()) {
            if (username.equalsIgnoreCase(staff.getStaffId()) || 
                username.equalsIgnoreCase(staff.getEmail()) ||
                username.equalsIgnoreCase(staff.getFirstName() + " " + staff.getLastName())) {
                return staff;
            }
        }
        
        for (Clinician clinician : clinicianRepository.getAll()) {
            if (username.equalsIgnoreCase(clinician.getClinicianId()) ||
                username.equalsIgnoreCase(clinician.getEmail()) ||
                username.equalsIgnoreCase(clinician.getFullName())) {
                return clinician;
            }
        }
        
        return null;
    }
    
    // creates a developer user for testing - gives access to everything
    private Staff createDeveloperStaff() {
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
        Patient patient = patientRepository.findById(username);
        if (patient != null) {
            return patient;
        }
        
        for (Patient p : patientRepository.getAll()) {
            if (username.equalsIgnoreCase(p.getLastName()) ||
                username.equalsIgnoreCase(p.getEmail())) {
                return p;
            }
        }
        
        return null;
    }
    
    private void launchStaffDashboard(Staff staff, String role) {
        Clinician clinician = null;
        if (staff instanceof Clinician) {
            clinician = (Clinician) staff;
        } else {
            clinician = clinicianRepository.findById(staff.getStaffId());
        }
        
        if (clinician == null) {
            // fallback to first clinician if not found - needed this for non-clinician staff
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
        
        GPDashboard dashboard = new GPDashboard(clinician);
        
        DashboardController dashboardController = new DashboardController(
            dashboard,
            appointmentRepository,
            patientRepository,
            clinicianRepository,
            facilityRepository,
            prescriptionRepository,
            referralRepository,
            staffRepository,
            role
        );
        
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
        PatientDashboardFrame patientDashboard = new PatientDashboardFrame();
        
        PatientDashboardController patientController = new PatientDashboardController(
            patientDashboard,
            appointmentRepository,
            prescriptionRepository,
            patientRepository,
            clinicianRepository,
            facilityRepository,
            patientId
        );
        
        patientDashboard.setVisible(true);
        
        System.out.println("Patient Dashboard launched for: " + patientId);
    }
}

