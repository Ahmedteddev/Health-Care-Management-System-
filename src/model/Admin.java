package model;

// Admin class - for administrative staff
public class Admin extends Staff {
    
    private String accessLevel;
    
    public Admin() {
        super();
    }
    
    public Admin(String username, String password, String email,
                 String staffId, String department, String hireDate,
                 String accessLevel) {
        super(username, password, email, staffId, department, hireDate);
        this.accessLevel = accessLevel;
    }
    
    public String getAccessLevel() {
        return accessLevel;
    }
    
    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }
}

