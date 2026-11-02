package org.mot.mottainaijdbc.dto;

public class StoreCountByStateDTO {
    //ATRIBUTOS
    private String state;
    private int totalStore;
    //CONSTRUTOR
    public StoreCountByStateDTO(String state, int totalStore) {
        this.state = state;
        this.totalStore = totalStore;
    }
    //GETTERS
    public String getState() {
        return state;
    }

    public int getTotalStore() {
        return totalStore;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "AddressStoreCountDTO{" +
                "state='" + state + '\'' +
                ", totalStore=" + totalStore +
                '}';
    }
}

