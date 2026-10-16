package org.mot.mottainaijdbc.model;

import java.time.LocalDateTime;

public class EmployeeRole {
    //ATRIBUTOS
    private int idRole;
    private String name;
    private String description;
    private int permissionLevel;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    //CONSTRUTOR
    public EmployeeRole(int idRole, String name, String description, int permissionLevel, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
        this.idRole = idRole;
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
    }
    //SOBRECARGA
    public EmployeeRole(int idRole, String name, String description, int permissionLevel) {
        this.idRole = idRole;
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }
    //GETTERS E SETTERS
    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPermissionLevel() {
        return permissionLevel;
    }

    public void setPermissionLevel(int permissionLevel) {
        this.permissionLevel = permissionLevel;
    }

    public boolean getActive() {
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
    public String toString() {
        return "-------- EMPLOYEE ROLE -------"+"\n"+
                "Id: "+ this.idRole+ "\n"+
                "Name: "+ this.name+ "\n"+
                "Description: "+ this.description+ "\n"+
                "Permission Level: "+ this.permissionLevel+ "\n"+
                "Active: "+ this.active+ "\n"+
                "Create At: "+ this.createdAt+ "\n"+
                "Update At: "+ this.updatedAt+ "\n"+
                "Delete At: "+ this.deletedAt+ "\n"+
                "------------------------------";
    }
}

