package model;

public class Clinician {

    private String id;
    private String firstName;
    private String lastName;
    private String title;
    private String speciality;
    private String gmcNumber;
    private String phone;
    private String email;
    private String workplaceId;
    private String workplaceType;
    private String employmentStatus;
    private String startDate;

    public Clinician() {}

    public Clinician(String id, String title, String firstName, String lastName,
                     String speciality, String gmcNumber, String phone,
                     String email, String workplaceId, String workplaceType,
                     String employmentStatus, String startDate) {

        this.id = id;
        this.title = title;
        this.firstName = firstName;
        this.lastName = lastName;
        this.speciality = speciality;
        this.gmcNumber = gmcNumber;
        this.phone = phone;
        this.email = email;
        this.workplaceId = workplaceId;
        this.workplaceType = workplaceType;
        this.employmentStatus = employmentStatus;
        this.startDate = startDate;
    }

    // ---- Getters ----
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getSpeciality() { return speciality; }
    public String getGmcNumber() { return gmcNumber; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getWorkplaceId() { return workplaceId; }
    public String getWorkplaceType() { return workplaceType; }
    public String getEmploymentStatus() { return employmentStatus; }
    public String getStartDate() { return startDate; }

    public String getFullName() {
        return title + " " + firstName + " " + lastName;
    }
}
