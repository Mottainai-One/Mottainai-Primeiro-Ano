package org.mot.mottainaijdbc.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Employee {
    //ATRIBUTOS
    private int employeeId;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private boolean active;
    private LocalDate hireDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int storeId;
    private int roleId;

    //CONSTRUTOR
    public Employee(int employeeId, String name, String cpf, String email, String phone, boolean active, LocalDate hireDate, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, int storeId, int roleId) {
        this.employeeId = employeeId;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.active = active;
        this.hireDate = hireDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.storeId = storeId;
        this.roleId = roleId;
    }
    //SOBRECARGA
    public Employee(int employeeId, String name, String cpf, String email, String phone, boolean active) {
        this.employeeId = employeeId;
        this.name = name;
        this.cpf = cpf;
        this.email = email;
        this.phone = phone;
        this.active = active;
    }
    //GETTERS E SETTERS
    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
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

    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    //toString
    public String toString() {
        return "------------- EMPLYOEE ------------"+"\n"+
                "Employee ID: "+ this.employeeId+ "\n"+
                "Name: "+ this.name+ "\n"+
                "CPF: "+ this.cpf+ "\n"+
                "E-mail: "+ this.email+ "\n"+
                "Phone: "+ this.phone+ "\n"+
                "Active: "+ this.active+"\n"+
                "Hire Date: "+ this.hireDate+"\n"+
                "Created At: "+ this.createdAt+ "\n"+
                "Update At: "+ this.updatedAt+ "\n"+
                "Deleted At: "+ this.deletedAt+ "\n"+
                "Store ID: "+ this.storeId+ "\n"+
                "Role ID: "+ this.roleId+ "\n"+
                "----------------------------------";
    }
}

