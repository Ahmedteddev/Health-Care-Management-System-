package Main;

import model.*;
import view.GPDashboard;
import controller.GPController;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Healthcare Management System.
 * Launches the GP Dashboard for clinician access.
 */
public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            try {
                // ================================
                // REPOSITORIES
                // ================================
                PatientRepository pr =
                        new PatientRepository("src/data/patients.csv");

                ClinicianRepository cr =
                        new ClinicianRepository("src/data/clinicians.csv");

                AppointmentRepository ar =
                        new AppointmentRepository("src/data/appointments.csv");

                // Note: FacilityRepository is available but not used in GP Dashboard
                // Uncomment if needed:
                // FacilityRepository fr =
                //         new FacilityRepository("src/data/facilities.csv");

                // Note: PrescriptionRepository and ReferralManager are available
                // but not used in the GP Dashboard. Uncomment if needed:
                // PrescriptionRepository pResR =
                //         new PrescriptionRepository("src/data/prescriptions.csv");
                // ReferralManager rm = ReferralManager.getInstance(
                //         "src/data/referrals.csv", pr, cr, fr,
                //         "src/data/referrals_output.txt");

                // ================================
                // GP DASHBOARD SETUP
                // ================================
                // Fetch C001 (Dr. David Thompson) from ClinicianRepository
                // You can change this to any clinician ID or implement login logic
                Clinician clinician = cr.findById("C001");
                
                if (clinician == null) {
                    System.err.println("Error: Clinician C001 not found in repository.");
                    System.err.println("Available clinicians:");
                    for (Clinician c : cr.getAll()) {
                        System.err.println("  - " + c.getClinicianId() + ": " + c.getFullName());
                    }
                    return;
                }

                // Create GP Dashboard
                GPDashboard dashboard = new GPDashboard(clinician);

                // Create GP Controller to handle interactions
                GPController gpController = new GPController(dashboard, ar, pr);

                // Load appointments for this clinician
                gpController.loadAppointments();

                // Show the dashboard
                dashboard.setVisible(true);

                System.out.println("Healthcare Management System started successfully.");
                System.out.println("GP Dashboard launched for: " + clinician.getFullName());
                System.out.println("Specialty: " + clinician.getSpeciality());

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
