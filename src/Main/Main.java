package Main;

import model.*;
import repository.PatientRepository;
import repository.ReferralRepository;
import repository.StaffRepository;
import view.LoginView;
import controller.LoginController;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Healthcare Management System.
 * Launches the Login screen.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                // ================================
                // REPOSITORIES - Initialize all repositories
                // ================================
                PatientRepository pr =
                        new PatientRepository("src/data/patients.csv");

                ClinicianRepository cr =
                        new ClinicianRepository("src/data/clinicians.csv");

                AppointmentRepository ar =
                        new AppointmentRepository("src/data/appointments.csv");

                FacilityRepository fr =
                        new FacilityRepository("src/data/facilities.csv");

                PrescriptionRepository pResR =
                        new PrescriptionRepository("src/data/prescriptions.csv");
                
                // ReferralRepository (Singleton)
                ReferralRepository refRepo = ReferralRepository.getInstance("src/data/referrals.csv");
                
                // StaffRepository (Singleton)
                StaffRepository staffRepo = StaffRepository.getInstance("src/data/staff.csv", "src/data/clinicians.csv");

                // ================================
                // LOGIN VIEW SETUP
                // ================================
                LoginView loginView = new LoginView();
                
                // Create Login Controller
                LoginController loginController = new LoginController(
                    loginView,
                    pr,
                    staffRepo,
                    cr,
                    ar,
                    pResR,
                    fr,
                    refRepo
                );
                
                // Show the login screen
                loginView.setVisible(true);
                
                System.out.println("Healthcare Management System started successfully.");
                System.out.println("Login screen launched.");

            } catch (Exception ex) {
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
