package model;

// This model class handles all the saving and loading for Referral data from the CSV
// Uses a Singleton pattern so we only have one copy of the data
import util.CsvUtils;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ReferralRepository {
    
    private static ReferralRepository referralRepo;
    private final List<Referral> referrals;
    private final String csvPath;
    private static final int EXPECTED_COLUMNS = 16;
    
    // Private constructor to enforce Singleton pattern
    private ReferralRepository(String csvPath) {
        this.csvPath = csvPath;
        this.referrals = new ArrayList<>();
        load();
    }
    
    // Public static method to get the singleton instance
    // Making sure we only have one copy of the data so it doesn't reset
    public static synchronized ReferralRepository getInstance(String csvPath) {
        if (referralRepo == null) {
            referralRepo = new ReferralRepository(csvPath);
        }
        return referralRepo;
    }
    
    // Loads referrals from CSV file
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                // Data integrity check
                if (row.length < EXPECTED_COLUMNS) {
                    System.err.println("Warning: Skipping invalid referral row with insufficient columns (" + 
                                     row.length + " < " + EXPECTED_COLUMNS + ")");
                    continue;
                }
                
                // Map CSV row to Referral constructor
                Referral referral = new Referral(
                    row[0],   // referralId
                    row[1],   // patientId
                    row[2],   // referringClinicianId
                    row[3],   // referredToClinicianId
                    row[4],   // referringFacilityId
                    row[5],   // referredToFacilityId
                    row[6],   // referralDate
                    row[7],   // urgencyLevel
                    row[8],   // referralReason
                    row[9],   // clinicalSummary
                    row[10],  // requestedInvestigations
                    row[11],  // status
                    row[12],  // appointmentId
                    row[13],  // notes
                    row[14],  // createdDate
                    row[15]   // lastUpdated
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
    
    // Returns all referrals
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }
    
    // Finds a referral by ID
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
    
    // Gets all referrals for a specific patient
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
    
    // Adds a new referral and appends it to CSV
    public void addAndAppend(Referral referral) {
        if (referral == null) {
            System.err.println("Cannot add null referral.");
            return;
        }
        
        // Check if referral already exists
        if (findById(referral.getReferralId()) != null) {
            System.err.println("Referral with ID " + referral.getReferralId() + " already exists.");
            return;
        }
        
        // Add to in-memory list
        referrals.add(referral);
        
        // Append to CSV file (all 16 columns)
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
    
    // Generates a new referral ID
    // Format: R001, R002, R003, etc.
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
                    // Ignore invalid ID format
                }
            }
        }
        
        return String.format("R%03d", max + 1);
    }
    
    // Deletes all referrals for a specific patient
    public void deleteByPatientId(String patientId) {
        deleteAllByPatientId(patientId);
    }
    
    // Deletes all referrals for a specific patient
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) {
            System.err.println("Cannot delete referrals: patient ID is null or empty.");
            return;
        }
        
        // Count how many will be removed
        int removedCount = 0;
        for (Referral r : referrals) {
            if (patientId.equals(r.getPatientId())) {
                removedCount++;
            }
        }
        
        // Remove all referrals matching the patient ID
        referrals.removeIf(referral -> patientId.equals(referral.getPatientId()));
        
        // Save updated list to CSV
        saveAll();
        
        System.out.println("Deleted " + removedCount + " referral(s) for patient " + patientId);
    }
    
    // Saves all referrals to the CSV file
    private void saveAll() {
        try (java.io.BufferedWriter bw = new java.io.BufferedWriter(new java.io.FileWriter(csvPath))) {
            // Write header
            bw.write("referral_id,patient_id,referring_clinician_id,referred_to_clinician_id,");
            bw.write("referring_facility_id,referred_to_facility_id,referral_date,urgency_level,");
            bw.write("referral_reason,clinical_summary,requested_investigations,status,");
            bw.write("appointment_id,notes,created_date,last_updated");
            bw.newLine();
            
            // Write all referrals
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
    
    // Escapes CSV values that contain commas or quotes
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


