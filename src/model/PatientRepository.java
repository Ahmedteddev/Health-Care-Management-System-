package model;

import model.Patient;
import java.io.*;
import java.util.*;

public class PatientRepository {
    private final String csvPath;
    private List<Patient> patients = new ArrayList<>();

    public PatientRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    public synchronized void load() {
        patients.clear();
        File file = new File(csvPath);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] v = line.split(",", -1);
                if (v.length >= 14) {
                    patients.add(new Patient(v[0],v[1],v[2],v[3],v[4],v[5],v[6],v[7],v[8],v[9],v[10],v[11],v[12],v[13]));
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    public synchronized void saveAll() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(csvPath))) {
            pw.println("ID,First,Last,DOB,NHS,Gender,Phone,Email,Address,Postcode,EName,EPhone,RegDate,GPID");
            for (Patient p : patients) {
                pw.println(String.join(",", p.getPatientId(), p.getFirstName(), p.getLastName(), p.getDateOfBirth(),
                    p.getNhsNumber(), p.getGender(), p.getPhoneNumber(), p.getEmail(), p.getAddress(), 
                    p.getPostcode(), p.getEmergencyContactName(), p.getEmergencyContactPhone(), 
                    p.getRegistrationDate(), p.getGpSurgeryId()));
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // REQUIRED METHODS FOR OTHER CONTROLLERS
    public List<Patient> findAll() { return new ArrayList<>(patients); }
    public List<Patient> getAll() { return findAll(); } // Alias for MedicalRecordController
    
    public Patient findById(String id) {
        return patients.stream().filter(p -> p.getPatientId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }
    
    public List<Patient> search(String id, String name, String nhs) {
        return patients.stream()
            .filter(p -> (id == null || id.isEmpty() || p.getPatientId().toLowerCase().contains(id.toLowerCase())))
            .filter(p -> (name == null || name.isEmpty() || p.getFullName().toLowerCase().contains(name.toLowerCase())))
            .filter(p -> (nhs == null || nhs.isEmpty() || p.getNhsNumber().contains(nhs)))
            .toList();
    }

    public void add(Patient p) { patients.add(p); saveAll(); }
    public void update(Patient p) { delete(p.getPatientId()); add(p); }
    public void delete(String id) { patients.removeIf(p -> p.getPatientId().equalsIgnoreCase(id)); saveAll(); }
    public void refresh() { load(); }

    public String generateNewId() {
        int max = 0;
        // Loop through your list of patients
        for (Patient p : patients) {
            try {
                // substring(1) skips the 'P' and takes the rest of the string
                int n = Integer.parseInt(p.getPatientId().substring(1));
                if (n > max) {
                    max = n;
                }
            } catch (Exception ignore) {
                // This ignores any IDs that aren't formatted correctly (like headers or bad data)
            }
        }
        // Formats back to "P" followed by at least 3 digits (e.g., P010)
        return String.format("P%03d", max + 1);
    }
}