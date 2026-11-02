package org.mot.mottainaijdbc.model;

public class Administration {
    //ATRIBUTOS
    private int administrationId;
    private String email;
    private String passwordHash;
    //CONSTRUTOR
    public Administration(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    //SOBRECARGA
    public Administration(int administrationId, String email, String passwordHash) {
        this.administrationId = administrationId;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    //GETTERS
    public int getId() {
        return administrationId;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
    //SETTERS
    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    //toString
    public String toString() {
        return "---------- ADMINISTRATION ----------"+"\n"+
                "ID: "+ this.administrationId+"\n"+
                "Email: "+ this.email+ "\n"+
                "-----------------------------------";
    }
}

