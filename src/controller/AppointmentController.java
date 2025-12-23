package controller;

import model.*;
import view.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AppointmentController {

    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final ClinicianRepository clinicianRepo;
    private final FacilityRepository facilityRepo;
    private final AppointmentView view;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public AppointmentController(AppointmentRepository repo,
                                 PatientRepository patientRepo,
                                 ClinicianRepository clinicianRepo,
                                 FacilityRepository facilityRepo,
                                 AppointmentView view) {

        this.repo = repo;
        this.patientRepo = patientRepo;
        this.clinicianRepo = clinicianRepo;
        this.facilityRepo = facilityRepo;
        this.view = view;

        view.setController(this);
        refreshAppointments();
        view.loadDropdowns(getPatientIds(), getClinicianIds(), getFacilityIds());
    }

    public AppointmentView getView() {
        return view;
    }

    public void refreshAppointments() {
        view.showAppointments(repo.getAll());
    }

    public String generateId() {
        return repo.generateNewId();
    }

    public List<String> getPatientIds() {
        return patientRepo.getAllIds();
    }

    public List<String> getClinicianIds() {
        return clinicianRepo.getAllIds();
    }

    public List<String> getFacilityIds() {
        return facilityRepo.getAllIds();
    }

    public void addAppointment(Appointment a) {
        repo.addAndAppend(a);
        refreshAppointments();
    }

    public void deleteById(String id) {
        Appointment a = repo.findById(id);
        if (a != null) repo.remove(a);
        refreshAppointments();
    }
}

