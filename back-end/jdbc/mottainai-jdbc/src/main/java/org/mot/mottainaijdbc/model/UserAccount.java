package org.mot.mottainaijdbc.model;

import java.time.LocalDateTime;

public class UserAccount {
    //ATRIBUTOS
    private int userId;
    private String email;
    private String passwordHash;
    private LocalDateTime lastLogin;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int employeeId;
    //CONSTRUTOR
    public UserAccount(int userId, String email, String passwordHash, LocalDateTime lastLogin, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, int employeeId) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.lastLogin = lastLogin;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.employeeId = employeeId;
    }
    //SOBRECARGA
    public UserAccount(int userId, String email, String passwordHash, LocalDateTime lastLogin, boolean active) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.lastLogin = lastLogin;
        this.active = active;
    }
    //GETTERS E SETTERS
    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    //toString
    public String toString(){
        return "----------- USER ACCOUNT -----------"+ "\n"+
                "User ID: "+ this.userId+ "\n"+
                "E-mail: "+ this.email+ "\n"+
                "Last Login: "+ this.lastLogin+ "\n"+
                "Active: "+ this.active+ "\n"+
                "Created At: "+ this.createdAt+ "\n"+
                "Update At: "+ this.updatedAt+ "\n"+
                "Deleted At: "+ this.deletedAt+ "\n"+
                "Employee ID:" + this.employeeId+ "\n"+
                "----------------------------------";
    }
}

