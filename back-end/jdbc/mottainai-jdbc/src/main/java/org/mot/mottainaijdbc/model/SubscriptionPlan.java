package org.mot.mottainaijdbc.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SubscriptionPlan {
    //ATRIBUTOS
    private int planId;
    private String name;
    private String description;
    private BigDecimal price;
    private int storeLimit;
    private int userLimit;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
    private int companyId;
    //CONSTRUTOR
    public SubscriptionPlan(int planId, String name, String description, BigDecimal price, int storeLimit, int userLimit, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt, int companyId) {
        this.planId = planId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.storeLimit = storeLimit;
        this.userLimit = userLimit;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = deletedAt;
        this.companyId = companyId;
    }
    //SOBRECARGA
    public SubscriptionPlan(int planId, String name, String description, BigDecimal price, int storeLimit, int userLimit) {
        this.planId = planId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.storeLimit = storeLimit;
        this.userLimit = userLimit;
    }
    //GETTERS E SETTERS
    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStoreLimit() {
        return storeLimit;
    }

    public void setStoreLimit(int storeLimit) {
        this.storeLimit = storeLimit;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public void setUserLimit(int userLimit) {
        this.userLimit = userLimit;
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
        return "------------ SUBSCRIPTION PLAN ------------"+ "\n"+
                "Plan ID: "+ this.planId+ "\n"+
                "Name: "+ this.name+"\n"+
                "Description: "+ this.description+ "\n"+
                "Price: "+ this.price+ "\n"+
                "Store Limit: "+ this.storeLimit+ "\n"+
                "User Limiti: "+ this.userLimit+ "\n"+
                "Active: "+ this.active+ "\n"+
                "Created At: "+ this.createdAt+ "\n"+
                "Update At: "+ this.updatedAt+ "\n"+
                "Deleted At: "+ this.deletedAt+ "\n"+
                "Company ID: "+ this.companyId+ "\n"+
                "-------------------------------------------";
    }
}


