package controller;

import model.Prescription;
import model.PrescriptionRepository;
import model.PatientRepository;
import model.ClinicianRepository;
import model.AppointmentRepository;
import model.Patient;
import model.Clinician;
import model.Appointment;
import view.PrescriptionView;

import java.util.ArrayList;
import java.util.List;

public class PrescriptionController {

    private final PrescriptionRepository repository;
    private final PatientRepository patientRepository;
    private final ClinicianRepository clinicianRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionView view;

    public PrescriptionController(PrescriptionRepository repository,
                                  PatientRepository patientRepository,
                                  ClinicianRepository clinicianRepository,
                                  AppointmentRepository appointmentRepository,
                                  PrescriptionView view) {

        this.repository = repository;
        this.patientRepository = patientRepository;
        this.clinicianRepository = clinicianRepository;
        this.appointmentRepository = appointmentRepository;
        this.view = view;

        view.setController(this);

        // Populate dropdowns
        view.populateDropdowns(
                getPatientIds(),
                getClinicianIds(),
                repository.getMedicationOptions(),
                repository.getPharmacyOptions(),
                getAppointmentIds()
        );

        refreshView();
    }

    public PrescriptionView getView() {
        return view;
    }

    public void refreshView() {
        view.showPrescriptions(repository.getAll());
        view.setNextId(repository.generateNewId());
    }

    // Expose lists for view
    public List<String> getPatientIds() {
        List<String> ids = new ArrayList<>();
        for (Patient p : patientRepository.getAll()) {
            ids.add(p.getId());
        }
        return ids;
    }

    public List<String> getClinicianIds() {
        List<String> ids = new ArrayList<>();
        for (Clinician c : clinicianRepository.getAll()) {
            ids.add(c.getId());
        }
        return ids;
    }

    public List<String> getAppointmentIds() {
        List<String> ids = new ArrayList<>();
        for (Appointment a : appointmentRepository.getAll()) {
            ids.add(a.getId());
        }
        return ids;
    }

    // ---------- CRUD called by view ----------

    public void addPrescription(Prescription p) {
        repository.addAndAppend(p);
        refreshView();
    }

    public void updatePrescription(Prescription p) {
        repository.update(p);
        refreshView();
    }

    public void deleteById(String id) {
        repository.removeById(id);
        refreshView();
    }
}
