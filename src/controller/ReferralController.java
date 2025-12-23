package controller;

import model.*;
import view.ReferralView;

import java.util.ArrayList;
import java.util.List;

public class ReferralController {

    private final ReferralManager referralManager;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentRepository appointmentRepo;
    private final ReferralView view;

    public ReferralController(ReferralManager rm,
                              PatientRepository pr,
                              ClinicianRepository cr,
                              FacilityRepository fr,
                              AppointmentRepository ar,
                              ReferralView view) {

        this.referralManager = rm;
        this.patientRepo = pr;
        this.clinicianRepo = cr;
        this.facilityRepo = fr;
        this.appointmentRepo = ar;
        this.view = view;

        // hook controller into view
        this.view.setController(this);

        refreshReferrals();
    }

    // ---------------------------------------------
    // VIEW HOOKS
    // ---------------------------------------------
    public ReferralView getView() {
        return view;
    }

    public void refreshReferrals() {
        view.showReferrals(referralManager.getAllReferrals());
    }

    // ---------------------------------------------
    // COMBOBOX DATA
    // ---------------------------------------------
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        for (Patient p : patientRepo.getAll()) {
            ids.add(p.getId());
        }
        return ids;
    }

    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicianRepo.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }

    public List<String> getFacilityIds() {
        List<String> ids = new ArrayList<>();
        for (Facility f : facilityRepo.getAll()) {
            ids.add(f.getId());
        }
        return ids;
    }

    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        for (Appointment a : appointmentRepo.getAll()) {
            ids.add(a.getId());
        }
        return ids;
    }

    // ---------------------------------------------
    // AUTO ID GENERATOR
    // ---------------------------------------------
    public String getNextReferralId() {

        int max = 0;

        for (Referral r : referralManager.getAllReferrals()) {
            String id = r.getId();   // Example: "R012"
            if (id != null && id.startsWith("R")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (NumberFormatException ignored) {}
            }
        }

        int next = max + 1;
        return String.format("R%03d", next);
    }

    // ---------------------------------------------
    // ADD REFERRAL
    // ---------------------------------------------
    public void addReferral(Referral r) {
        referralManager.createReferral(r);   // Saves CSV + writes text file
        refreshReferrals();
    }
}
