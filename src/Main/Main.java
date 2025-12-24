package Main;

import controller.*;
import model.*;
import view.*;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            // ================================
            // REPOSITORIES
            // ================================
            PatientRepository pr =
                    new PatientRepository("src/data/patients.csv");

            ClinicianRepository cr =
                    new ClinicianRepository("src/data/clinicians.csv");

            FacilityRepository fr =
                    new FacilityRepository("src/data/facilities.csv");

            AppointmentRepository ar =
                    new AppointmentRepository("src/data/appointments.csv");

            PrescriptionRepository pResR =
                    new PrescriptionRepository("src/data/prescriptions.csv");

            // ================================
            // REFERRAL MANAGER (Singleton)
            // ================================
            ReferralManager rm = ReferralManager.getInstance(
                    "src/data/referrals.csv",  // referralCsvPath
                    pr,   // PatientRepository
                    cr,   // ClinicianRepository
                    fr,   // FacilityRepository
                    "src/data/referrals_output.txt"  // referralTextPath
            );

            // ================================
            // VIEWS
            // ================================
            PatientView pv = new PatientView();
            ClinicianView cv = new ClinicianView();
            AppointmentView av = new AppointmentView();
            PrescriptionView presV = new PrescriptionView();
            ReferralView rv = new ReferralView();

            // ================================
            // CONTROLLERS (MATCHING YOUR CONSTRUCTORS)
            // ================================
            PatientController pc = new PatientController(pr, pv);

            ClinicianController cc = new ClinicianController(cr, cv);

            AppointmentController ac = new AppointmentController(
                    ar,   // AppointmentRepository
                    pr,   // PatientRepository
                    cr,   // ClinicianRepository
                    fr,   // FacilityRepository
                    av    // AppointmentView
            );

            PrescriptionController prc = new PrescriptionController(
                    pResR,
                    pr,
                    cr,
                    ar,
                    presV
            );

            ReferralController rc = new ReferralController(
                    rm,   // ReferralManager
                    pr,   // PatientRepository
                    cr,   // ClinicianRepository
                    fr,   // FacilityRepository
                    ar,   // AppointmentRepository
                    rv    // ReferralView
            );

            // ================================
            // MAIN FRAME
            // ================================
            MainFrame frame = new MainFrame(pc, cc, ac, prc, rc);
            frame.setVisible(true);
        });
    }
}
