package org.mot.mottainaijdbc.dto;

public class StoreCountByCityDTO {
    //ATRIBUTOS
    private String city;
    private String state;
    private int totalStore;
    //CONSTRUTOR
    public StoreCountByCityDTO(String city, String state, int totalStore) {
        this.city = city;
        this.state = state;
        this.totalStore = totalStore;
    }
    //GETTERS
    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getTotalStore() {
        return totalStore;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "AddressStoreCountByCityDTO{" +
                "city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", totalStore=" + totalStore +
                '}';
    }
}

