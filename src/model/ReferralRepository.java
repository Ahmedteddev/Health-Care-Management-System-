package model;

// Handles loading and saving referral data from CSV (uses singleton pattern)
import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReferralRepository {
    
    private static ReferralRepository referralRepo;
    private final List<Referral> referrals;
    private final String csvPath;
    private static final int EXPECTED_COLUMNS = 16;
    
    private ReferralRepository(String csvPath) {
        this.csvPath = csvPath;
        this.referrals = new ArrayList<>();
        load();
    }
    
    // Get the singleton instance (only one copy of the data)
    public static synchronized ReferralRepository getInstance(String csvPath) {
        if (referralRepo == null) {
            referralRepo = new ReferralRepository(csvPath);
        }
        return referralRepo;
    }
    
    // Load referrals from CSV
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid referral row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + ")");
                    continue;
                }
                
                Referral referral = new Referral(
                    row[0],
                    row[1],
                    row[2],
                    row[3],
                    row[4],
                    row[5],
                    row[6],
                    row[7],
                    row[8],
                    row[9],
                    row[10],
                    row[11],
                    row[12],
                    row[13],
                    row[14],
                    row[15]
                );
                
                referrals.add(referral);
            }
            
            System.out.println("Loaded " + referrals.size() + " referrals from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load referrals from CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading referrals: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }
    
    public Referral findById(String id) {
        if (id == null) {
            return null;
        }
        
        for (Referral referral : referrals) {
            if (id.equals(referral.getReferralId())) {
                return referral;
            }
        }
        return null;
    }
    
    public List<Referral> findByPatientId(String patientId) {
        List<Referral> result = new ArrayList<>();
        if (patientId == null) {
            return result;
        }
        
        for (Referral referral : referrals) {
            if (patientId.equals(referral.getPatientId())) {
                result.add(referral);
            }
        }
        return result;
    }
    
    // Add a new referral and append to CSV
    public void addAndAppend(Referral referral) {
        if (referral == null) {
            System.err.println("Cannot add null referral.");
            return;
        }
        
        if (findById(referral.getReferralId()) != null) {
            System.err.println("Referral with ID " + referral.getReferralId() + " already exists.");
            return;
        }
        
        referrals.add(referral);
        
        try {
            String[] rowData = {
                referral.getReferralId(),
                referral.getPatientId(),
                referral.getReferringClinicianId(),
                referral.getReferredToClinicianId(),
                referral.getReferringFacilityId(),
                referral.getReferredToFacilityId(),
                referral.getReferralDate(),
                referral.getUrgencyLevel(),
                referral.getReferralReason(),
                referral.getClinicalSummary(),
                referral.getRequestedInvestigations(),
                referral.getStatus(),
                referral.getAppointmentId(),
                referral.getNotes(),
                referral.getCreatedDate(),
                referral.getLastUpdated()
            };
            
            CsvUtils.appendLine(csvPath, rowData);
            System.out.println("Successfully added referral " + referral.getReferralId() + " to repository and CSV.");
            
        } catch (IOException ex) {
            System.err.println("Failed to append referral to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
        } catch (Exception ex) {
            System.err.println("Unexpected error while adding referral: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    // Generate a new referral ID (R001, R002, etc.)
    public String generateNewId() {
        int max = 0;
        
        for (Referral referral : referrals) {
            String id = referral.getReferralId();
            if (id != null && id.startsWith("R")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) {
                        max = num;
                    }
                } catch (NumberFormatException ex) {
                }
            }
        }
        
        return String.format("R%03d", max + 1);
    }
    
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    // Delete all referrals for a patient
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete referrals: patient ID is null or empty.");
            return;
        }
        
        int removedCount = 0;
        for (Referral r : referrals) {
            if (patientId.equals(r.getPatientId())) {
                removedCount++;
            }
        }
        
        referrals.removeIf(referral -> patientId.equals(referral.getPatientId()));
        saveAll();
        
        System.out.println("Deleted " + removedCount + " referral(s) for patient " + patientId);
    }
    
    // Save all referrals back to CSV
    private void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            bw.write("referral_id,patient_id,referring_clinician_id,referred_to_clinician_id,");
            bw.write("referring_facility_id,referred_to_facility_id,referral_date,urgency_level,");
            bw.write("referral_reason,clinical_summary,requested_investigations,status,");
            bw.write("appointment_id,notes,created_date,last_updated");
            bw.newLine();
            
            for (Referral r : referrals) {
                bw.write(escapeCsv(r.getReferralId()) + ",");
                bw.write(escapeCsv(r.getPatientId()) + ",");
                bw.write(escapeCsv(r.getReferringClinicianId()) + ",");
                bw.write(escapeCsv(r.getReferredToClinicianId()) + ",");
                bw.write(escapeCsv(r.getReferringFacilityId()) + ",");
                bw.write(escapeCsv(r.getReferredToFacilityId()) + ",");
                bw.write(escapeCsv(r.getReferralDate()) + ",");
                bw.write(escapeCsv(r.getUrgencyLevel()) + ",");
                bw.write(escapeCsv(r.getReferralReason()) + ",");
                bw.write(escapeCsv(r.getClinicalSummary()) + ",");
                bw.write(escapeCsv(r.getRequestedInvestigations()) + ",");
                bw.write(escapeCsv(r.getStatus()) + ",");
                bw.write(escapeCsv(r.getAppointmentId()) + ",");
                bw.write(escapeCsv(r.getNotes()) + ",");
                bw.write(escapeCsv(r.getCreatedDate()) + ",");
                bw.write(escapeCsv(r.getLastUpdated()));
                bw.newLine();
            }
            
        } catch (java.io.IOException ex) {
            System.err.println("Failed to save referrals to CSV file: " + csvPath);
            System.err.println("Error: " + ex.getMessage());
        }
    }
    
    // Escape commas and quotes in CSV values
    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}


