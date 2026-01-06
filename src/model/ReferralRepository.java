package model;

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
    
    public static synchronized ReferralRepository getInstance(String csvPath) {
        if (referralRepo == null) {
            referralRepo = new ReferralRepository(csvPath);
        }
        return referralRepo;
    }
    
    private void load() {
        try {
            List<String[]> rows = CsvUtils.readCsv(csvPath);
            
            for (String[] row : rows) {
                // GUARD 1: Skip completely empty rows
                if (row == null || row.length == 0 || (row.length == 1 && row[0].trim().isEmpty())) {
                    continue;
                }

                // GUARD 2: Skip header row
                if (row[0].equalsIgnoreCase("referral_id")) {
                    continue;
                }

                // GUARD 3: SAFE ARRAY FILLING
                // This prevents ArrayIndexOutOfBoundsException if the CSV row is too short
                String[] safe = new String[EXPECTED_COLUMNS];
                for (int i = 0; i < EXPECTED_COLUMNS; i++) {
                    safe[i] = (i < row.length) ? row[i] : "";
                }
                
                Referral referral = new Referral(
                    safe[0], safe[1], safe[2], safe[3], safe[4], safe[5],
                    safe[6], safe[7], safe[8], safe[9], safe[10], safe[11],
                    safe[12], safe[13], safe[14], safe[15]
                );
                
                referrals.add(referral);
            }
            
            System.out.println("Loaded " + referrals.size() + " referrals from " + csvPath);
            
        } catch (IOException ex) {
            System.err.println("Failed to load referrals from CSV file: " + csvPath);
        } catch (Exception ex) {
            System.err.println("Unexpected error while loading referrals: " + ex.getMessage());
        }
    }
    
    public List<Referral> getAll() {
        return new ArrayList<>(referrals);
    }
    
    public Referral findById(String id) {
        if (id == null) return null;
        String tid = id.trim();
        for (Referral r : referrals) {
            if (tid.equalsIgnoreCase(r.getReferralId() != null ? r.getReferralId().trim() : "")) {
                return r;
            }
        }
        return null;
    }
    
    public List<Referral> findByPatientId(String patientId) {
        List<Referral> result = new ArrayList<>();
        if (patientId == null || patientId.isEmpty()) return result;
        
        String trimmedId = patientId.trim();
        for (Referral referral : referrals) {
            String pId = referral.getPatientId() != null ? referral.getPatientId().trim() : "";
            if (trimmedId.equalsIgnoreCase(pId)) {
                result.add(referral);
            }
        }
        return result;
    }
    
    public void addAndAppend(Referral referral) {
        if (referral == null) return;
        
        referrals.add(referral);
        
        try {
            String[] rowData = {
                referral.getReferralId(), referral.getPatientId(), referral.getReferringClinicianId(),
                referral.getReferredToClinicianId(), referral.getReferringFacilityId(),
                referral.getReferredToFacilityId(), referral.getReferralDate(),
                referral.getUrgencyLevel(), referral.getReferralReason(),
                referral.getClinicalSummary(), referral.getRequestedInvestigations(),
                referral.getStatus(), referral.getAppointmentId(), referral.getNotes(),
                referral.getCreatedDate(), referral.getLastUpdated()
            };
            
            CsvUtils.appendLine(csvPath, rowData);
        } catch (IOException ex) {
            System.err.println("Failed to append referral: " + ex.getMessage());
        }
    }
    
    public String generateNewId() {
        int max = 0;
        for (Referral referral : referrals) {
            String id = referral.getReferralId();
            if (id != null && id.startsWith("R")) {
                try {
                    int num = Integer.parseInt(id.substring(1));
                    if (num > max) max = num;
                } catch (Exception ignore) {}
            }
        }
        return String.format("R%03d", max + 1);
    }
    
    public void deleteAllByPatientId(String patientId) {
        if (patientId == null || patientId.isEmpty()) return;
        String trimmedId = patientId.trim();
        referrals.removeIf(r -> {
            String pId = r.getPatientId() != null ? r.getPatientId().trim() : "";
            return trimmedId.equalsIgnoreCase(pId);
        });
        saveAll();
    }
    
    private void saveAll() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvPath))) {
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
        } catch (IOException ex) {
            System.err.println("Failed to save referrals: " + ex.getMessage());
        }
    }
    
    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}