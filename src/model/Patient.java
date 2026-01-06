package model;

public class Patient extends User {
    private String patientId, firstName, lastName, dateOfBirth, nhsNumber, gender;
    private String phoneNumber, address, postcode, emergencyContactName, emergencyContactPhone;
    private String registrationDate, gpSurgeryId;

    public Patient() { 
        super(); 
        this.registrationDate = java.time.LocalDate.now().toString(); 
    }

    // Constructor for CSV Loading (14 Fields)
    public Patient(String patientId, String firstName, String lastName, String dateOfBirth, 
                   String nhsNumber, String gender, String phoneNumber, String email, 
                   String address, String postcode, String emergencyContactName, 
                   String emergencyContactPhone, String registrationDate, String gpSurgeryId) {
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

    private static String generateUsername(String email, String id) {
        return (email != null && email.contains("@")) ? email.split("@")[0] : id;
    }

    // THE FULL NAME METHOD
    public String getFullName() {
        return ((firstName != null ? firstName : "") + " " + (lastName != null ? lastName : "")).trim();
    }

    // Alias for controllers using .getName()
    public String getName() { return getFullName(); }

    // Getters and Setters
    public String getPatientId() { return patientId; }
    public void setPatientId(String id) { this.patientId = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String fn) { this.firstName = fn; }
    public String getLastName() { return lastName; }
    public void setLastName(String ln) { this.lastName = ln; }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dob) { this.dateOfBirth = dob; }
    public String getNhsNumber() { return nhsNumber; }
    public void setNhsNumber(String nhs) { this.nhsNumber = nhs; }
    public String getGender() { return gender; }
    public void setGender(String g) { this.gender = g; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String p) { this.phoneNumber = p; }
    public String getAddress() { return address; }
    public void setAddress(String a) { this.address = a; }
    public String getPostcode() { return postcode; }
    public void setPostcode(String pc) { this.postcode = pc; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String n) { this.emergencyContactName = n; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String p) { this.emergencyContactPhone = p; }
    public String getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(String rd) { this.registrationDate = rd; }
    public String getGpSurgeryId() { return gpSurgeryId; }
    public void setGpSurgeryId(String id) { this.gpSurgeryId = id; }
}