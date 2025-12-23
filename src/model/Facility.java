package model;

public class Facility {

    private String id;
    private String name;
    private String type;               // GP Surgery / Hospital
    private String address;
    private String postcode;
    private String phone;
    private String email;
    private String openingHours;
    private String managerName;
    private int capacity;
    private String specialities;

    public Facility() { }

    public Facility(String id, String name, String type,
                    String address, String postcode, String phone,
                    String email, String openingHours, String managerName,
                    int capacity, String specialities) {

        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
        this.postcode = postcode;
        this.phone = phone;
        this.email = email;
        this.openingHours = openingHours;
        this.managerName = managerName;
        this.capacity = capacity;
        this.specialities = specialities;
    }

    // Getters & Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPostcode() { return postcode; }
    public void setPostcode(String postcode) { this.postcode = postcode; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getOpeningHours() { return openingHours; }
    public void setOpeningHours(String openingHours) { this.openingHours = openingHours; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getSpecialities() { return specialities; }
    public void setSpecialities(String specialities) { this.specialities = specialities; }

    @Override
    public String toString() {
        return id + " - " + name;
    }
}
