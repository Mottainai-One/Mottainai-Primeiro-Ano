package org.mot.mottainaijdbc.model;

import java.time.LocalDateTime;

public class Store {
    //ATRIBUTOS
    private int storeId;
    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private double latitude;
    private double longitude;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int companyId;
    private int adressId;
    //CONSTRUTOR
    public Store(int storeId, String name, String cnpj, String email, String phone, double latitude, double longitude, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, int companyId, int adressId) {
        this.storeId = storeId;
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.companyId = companyId;
        this.adressId = adressId;
    }
    //SOBRECARGA
    public Store(int storeId, String name, String cnpj, String email, String phone) {
        this.storeId = storeId;
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
    }
    //GETTERS E SETTERS
    public int getStoreId() {
        return storeId;
    }

    public void setStoreId(int storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAdressId() {
        return adressId;
    }

    public void setAdressId(int adressId) {
        this.adressId = adressId;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
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

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    //toString
    public String toString() {
        return "------------- STORE -------------"+ "\n"+
                "Store ID: "+ this.storeId+ "\n"+
                "Name: "+ this.name+ "\n"+
                "Phone: "+ this.phone+ "\n"+
                "Latitude: "+ this.latitude+ "\n"+
                "Longitude: "+ this.longitude+ "\n"+
                "Active: "+ this.active+ "\n"+
                "Created At: "+ this.createdAt+ "\n"+
                "Update At: "+ this.updatedAt+ "\n"+
                "Deleted At: "+ this.deletedAt+ "\n"+
                "Company ID: "+ this.companyId+ "\n"+
                "Adress ID: "+ this.adressId + "\n"+
                "---------------------------------";

    }
}

