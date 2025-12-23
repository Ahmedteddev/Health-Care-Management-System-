package view;

import controller.*;
import javax.swing.*;

public class MainFrame extends JFrame {

    public MainFrame(
            PatientController pc,
            ClinicianController cc,
            AppointmentController ac,
            PrescriptionController prc,
            ReferralController rc) {

        super("Healthcare Management System (HMS)");

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Patients", pc.getView());
        tabs.addTab("Clinicians", cc.getView());
        tabs.addTab("Appointments", ac.getView());
        tabs.addTab("Prescriptions", prc.getView());
        tabs.addTab("Referrals", rc.getView());

        setContentPane(tabs);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
    }
}
