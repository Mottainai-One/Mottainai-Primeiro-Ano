package org.mot.mottainaijdbc.model;

import java.time.LocalDateTime;

public class Company {
    //ATRIBUTOS
    private int companyId;
    private String officialName;
    private String tradeName;
    private String cnpj;
    private String email;
    private String phone;
    private double latitude;
    private double longitude;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int subscriptionPlanId;
    //CONSTRUTOR
    public Company(int companyId, String officialName, String tradeName, String cnpj, String email, String phone, double latitude, double longitude, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, int subscriptionPlanId) {
        this.companyId = companyId;
        this.officialName = officialName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.subscriptionPlanId = subscriptionPlanId;
    }
    //SOBRECARGA
    public Company(int companyId, String officialName, String tradeName, String cnpj, String email, String phone) {
        this.companyId = companyId;
        this.officialName = officialName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
    }
    //GETTERS E SETTERS
    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String oficialName) {
        this.officialName = officialName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
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

    public int getSubscriptionPlanId() {
        return subscriptionPlanId;
    }

    public void setSubscriptionPlanId(int subscriptionPlanId) {
        this.subscriptionPlanId = subscriptionPlanId;
    }

    //toString
    public String toString() {
        return "----------- COMPANY ----------"+"\n"+
                "Comany ID: "+ this.companyId +"\n"+
                "Official Name: "+ this.officialName+ "\n"+
                "Trade Name: "+ this.tradeName+ "\n"+
                "CNPJ: "+ this.cnpj+ "\n"+
                "E-mail: "+ this.email+ "\n"+
                "Phone: "+ this.phone+ "\n"+
                "Latitude: "+ this.latitude+ "\n"+
                "Longitude: "+ this.longitude+ "\n"+
                "Active: "+ this.active+ "\n"+
                "Created At: "+ this.createdAt+ "\n"+
                "Updated At: "+ this.updatedAt+ "\n"+
                "Deleted At: "+ this.deletedAt+"\n"+
                "-----------------------------";

    }
}

