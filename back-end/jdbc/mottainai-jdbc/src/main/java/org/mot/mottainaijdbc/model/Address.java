package org.mot.mottainaijdbc.model;

import java.time.LocalDateTime;

public class Address {
    //ATRIBUTOS
    private int addressId;
    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private LocalDateTime deleteAt;
    //CONSTRUTOR
    public Address(int addressId, String zipCode, String street, String number, String complement, String neighborhood, String city, String state, LocalDateTime createAt, LocalDateTime updateAt, LocalDateTime deleteAt) {
        this.addressId = addressId;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.deleteAt = deleteAt;
    }
    //SOBRECARGA
    public Address(int addressId, String zipCode, String street, String number, String complement, String neighborhood, String city, String state) {
        this.addressId = addressId;
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }
    //GETTERS E SETTERS
    public int getAddressId() {
        return addressId;
    }

    public void setAddressId(int addressId) {
        this.addressId = addressId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public LocalDateTime getCreateAt() {
        return createAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        this.createAt = createAt;
    }

    public LocalDateTime getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(LocalDateTime updateAt) {
        this.updateAt = updateAt;
    }

    public LocalDateTime getDeleteAt() {
        return deleteAt;
    }

    public void setDeleteAt(LocalDateTime deleteAt) {
        this.deleteAt = deleteAt;
    }

    //toString
    public String toString(){
        return "--------- ADRESS --------"+ "\n"+
                "Id Address: "+ this.addressId+ "\n"+
                "Zip Code: "+ this.zipCode+ "\n"+
                "Street: "+ this.street+ "\n"+
                "Number: "+ this.number+ "\n"+
                "Complement: "+this.complement+ "\n"+
                "Neighborhood: "+  this.complement+ "\n"+
                "City: "+ this.city + "\n"+
                "State: "+ this.state+"\n"+
                "Create At: "+this.createAt+ "\n"+
                "Update At: "+this.updateAt+ "\n"+
                "Delete At: "+this.deleteAt+ "\n"+
                "-------------------------";
    }
}

