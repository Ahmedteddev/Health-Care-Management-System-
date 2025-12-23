package model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReferralRepository {

    private final List<Referral> referrals = new ArrayList<>();
    private final String csvPath;

    public ReferralRepository(String csvPath) {
        this.csvPath = csvPath;
        load();
    }

    private void load() {
        try {
            for (String[] row : CsvUtils.readCsv(csvPath)) {

                // Create Referral object with ALL 16 columns
                Referral r = new Referral(
                        row[0],  // referral_id
                        row[1],  // patient_id
                        row[2],  // referring_clinician
                        row[3],  // referred_to_clinician
                        row[4],  // referring_facility
                        row[5],  // referred_to_facility
                        row[6],  // referral_date
                        row[7],  // urgency_level
                        row[8],  // referral_reason
                        row[9],  // clinical_summary
                        row[10], // requested_service
                        row[11], // status
                        row[12], // appointment_id
                        row[13], // notes
                        row[14], // created_date
                        row[15]  // last_updated
                );

                referrals.add(r);
            }

        } catch (IOException ex) {
            System.err.println("Failed to load referrals: " + ex.getMessage());
        }
    }


    public List<Referral> getAll() {
        return referrals;
    }


    /**
     * Add referral and append to CSV (ALL 16 COLUMNS)
     */
    public void addAndAppend(Referral r) {
        referrals.add(r);

        try {
            CsvUtils.appendLine(csvPath, new String[] {
                    r.getId(),
                    r.getPatientId(),
                    r.getReferringClinicianId(),
                    r.getReferredToClinicianId(),
                    r.getReferringFacilityId(),
                    r.getReferredToFacilityId(),
                    r.getReferralDate(),
                    r.getUrgencyLevel(),
                    r.getReferralReason(),
                    r.getClinicalSummary(),
                    r.getRequestedService(),
                    r.getStatus(),
                    r.getAppointmentId(),
                    r.getNotes(),
                    r.getCreatedDate(),
                    r.getLastUpdated()
            });

        } catch (IOException ex) {
            System.err.println("Failed to append referral: " + ex.getMessage());
        }
    }
}
