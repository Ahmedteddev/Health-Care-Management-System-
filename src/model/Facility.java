package model;

/**
 * Facility class representing healthcare facilities.
 * Matches CSV structure: facility_id, facility_name, facility_type, address, 
 * postcode, phone_number, email, opening_hours, manager_name, capacity, specialities_offered
 */
public class Facility {
    
    private String facilityId;
    private String facilityName;
    private String facilityType;
    private String address;
    private String postcode;
    private String phoneNumber;
    private String email;
    private String openingHours;
    private String managerName;
    private int capacity;
    private String specialitiesOffered;
    
    public Facility() {
    }
    
    /**
     * Full-parameter constructor for CSV loading.
     */
    public Facility(String facilityId, String facilityName, String facilityType,
                    String address, String postcode, String phoneNumber,
                    String email, String openingHours, String managerName,
                    int capacity, String specialitiesOffered) {
        this.facilityId = facilityId;
        this.facilityName = facilityName;
        this.facilityType = facilityType;
        this.address = address;
        this.postcode = postcode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.openingHours = openingHours;
        this.managerName = managerName;
        this.capacity = capacity;
        this.specialitiesOffered = specialitiesOffered;
    }
    
    // Backward compatibility methods
    public String getId() {
        return facilityId;
    }
    
    public void setId(String id) {
        this.facilityId = id;
    }
    
    public String getName() {
        return facilityName;
    }
    
    public void setName(String name) {
        this.facilityName = name;
    }
    
    public String getType() {
        return facilityType;
    }
    
    public void setType(String type) {
        this.facilityType = type;
    }
    
    // ============================================================
    // GETTERS AND SETTERS
    // ============================================================
    public String getFacilityId() {
        return facilityId;
    }
    
    public void setFacilityId(String facilityId) {
        this.facilityId = facilityId;
    }
    
    public String getFacilityName() {
        return facilityName;
    }
    
    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }
    
    public String getFacilityType() {
        return facilityType;
    }
    
    public void setFacilityType(String facilityType) {
        this.facilityType = facilityType;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPostcode() {
        return postcode;
    }
    
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getOpeningHours() {
        return openingHours;
    }
    
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    
    public String getManagerName() {
        return managerName;
    }
    
    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    
    public int getCapacity() {
        return capacity;
    }
    
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
    
    public String getSpecialitiesOffered() {
        return specialitiesOffered;
    }
    
    public void setSpecialitiesOffered(String specialitiesOffered) {
        this.specialitiesOffered = specialitiesOffered;
    }
    
    // Backward compatibility
    public String getPhone() {
        return phoneNumber;
    }
    
    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }
    
    public String getSpecialities() {
        return specialitiesOffered;
    }
    
    public void setSpecialities(String specialities) {
        this.specialitiesOffered = specialities;
    }
    
    @Override
    public String toString() {
        return facilityId + " - " + facilityName;
    }
}
