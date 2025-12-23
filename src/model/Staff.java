package model;

public class Staff extends Person {

    private String position;   // receptionist, admin, etc.
    private String facilityId;

    public Staff() { }

    public Staff(String id, String name, String phone, String email,
                 String position, String facilityId) {
        super(id, name, phone, email);
        this.position = position;
        this.facilityId = facilityId;
    }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getFacilityId() { return facilityId; }
    public void setFacilityId(String facilityId) { this.facilityId = facilityId; }
}
