package model;

// Patient class - stores all the patient info like name, DOB, NHS number, etc.
public class Patient extends User {
    
    private String patientId;
    private String firstName;
    private String lastName;
    private String dateOfBirth;
    private String nhsNumber;
    private String gender;
    private String phoneNumber;
    private String address;
    private String postcode;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String registrationDate;
    private String gpSurgeryId;
    
    public Patient() {
        super();
    }
    
    public Patient(String username, String password, String email,
                   String patientId, String firstName, String lastName,
                   String dateOfBirth, String nhsNumber, String gender,
                   String phoneNumber, String address, String postcode,
                   String emergencyContactName, String emergencyContactPhone,
                   String registrationDate, String gpSurgeryId) {
        super(username, password, email);
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhsNumber = nhsNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.postcode = postcode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.registrationDate = registrationDate;
        this.gpSurgeryId = gpSurgeryId;
    }
    
    // Constructor for loading from CSV
    public Patient(String patientId, String firstName, String lastName,
                   String dateOfBirth, String nhsNumber, String gender,
                   String phoneNumber, String email, String address, String postcode,
                   String emergencyContactName, String emergencyContactPhone,
                   String registrationDate, String gpSurgeryId) {
        super(generateUsername(email, patientId), "default", email);
        this.patientId = patientId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.nhsNumber = nhsNumber;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.postcode = postcode;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactPhone = emergencyContactPhone;
        this.registrationDate = registrationDate;
        this.gpSurgeryId = gpSurgeryId;
    }
    
    private static String generateUsername(String email, String patientId) {
        if (email != null && !email.isEmpty()) {
            return email.split("@")[0];
        }
        return patientId;
    }
    
    public String getName() {
        return getFullName();
    }
    
    public String getId() {
        return patientId;
    }
    
    public void setId(String id) {
        this.patientId = id;
    }
    
    public String getPatientId() {
        return patientId;
    }
    
    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getDob() {
        return dateOfBirth;
    }
    
    public void setDob(String dob) {
        this.dateOfBirth = dob;
    }
    
    public String getNhsNumber() {
        return nhsNumber;
    }
    
    public void setNhsNumber(String nhsNumber) {
        this.nhsNumber = nhsNumber;
    }
    
    public String getGender() {
        return gender;
    }
    
    public void setGender(String gender) {
        this.gender = gender;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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
    
    public String getEmergencyContactName() {
        return emergencyContactName;
    }
    
    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }
    
    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }
    
    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }
    
    public String getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getGpSurgeryId() {
        return gpSurgeryId;
    }
    
    public void setGpSurgeryId(String gpSurgeryId) {
        this.gpSurgeryId = gpSurgeryId;
    }
}
