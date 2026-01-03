package Main;

// Hospital Management System
// Student ID: [Your ID]
// Coursework Project

import model.*;
import view.LoginView;
import controller.LoginController;
import javax.swing.SwingUtilities;

// This is the main class that starts everything
// When you run the program, this is where it begins
public class Main {

    public static void main(String[] args) {
        // Using SwingUtilities to make sure the GUI runs on the right thread
        // This is important because Java GUI stuff needs to happen on a special thread
        SwingUtilities.invokeLater(() -> {
            try {
                // Setting up all the repositories that hold our data
                // Each one reads from a CSV file and stores the information
                PatientRepository patientRepo = new PatientRepository("src/data/patients.csv");
                ClinicianRepository clinicianRepo = new ClinicianRepository("src/data/clinicians.csv");
                AppointmentRepository appointmentRepo = new AppointmentRepository("src/data/appointments.csv");
                FacilityRepository facilityRepo = new FacilityRepository("src/data/facilities.csv");
                PrescriptionRepository prescriptionRepo = new PrescriptionRepository("src/data/prescriptions.csv");
                
                // StaffRepository uses a special pattern called Singleton
                // This means we use getInstance() instead of "new" because we only want one copy
                // Making sure we only have one copy of the data so it doesn't reset
                StaffRepository staffRepo = StaffRepository.getInstance("src/data/staff.csv", "src/data/clinicians.csv");

                // Creating the login screen that users see first
                LoginView loginScreen = new LoginView();
                
                // Creating the controller that handles what happens when someone tries to log in
                // The controller needs all the repositories so it can check if the login is valid
                LoginController loginController = new LoginController(
                    loginScreen,
                    patientRepo,
                    staffRepo,
                    clinicianRepo,
                    appointmentRepo,
                    prescriptionRepo,
                    facilityRepo
                );
                
                // Making the login screen visible so users can actually see it
                loginScreen.setVisible(true);
                
                System.out.println("Healthcare Management System started successfully.");
                System.out.println("Login screen launched.");

            } catch (Exception ex) {
                // If something goes wrong, show an error message
                System.err.println("Error starting the application: " + ex.getMessage());
                ex.printStackTrace();
                javax.swing.JOptionPane.showMessageDialog(null,
                    "Error starting the application:\n" + ex.getMessage(),
                    "Startup Error",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
