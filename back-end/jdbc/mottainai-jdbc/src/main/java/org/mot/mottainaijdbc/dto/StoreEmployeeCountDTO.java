package org.mot.mottainaijdbc.dto;

public class StoreEmployeeCountDTO {
    //ATRIBUTOS
    private int storeId;
    private String nameStore;
    private int totalEmployee;
    //CONSTRUTOR
    public StoreEmployeeCountDTO(int storeId, String nameStore, int totalEmployee) {
        this.storeId = storeId;
        this.nameStore = nameStore;
        this.totalEmployee = totalEmployee;
    }
    //GETTERS
    public int getStoreId() {
        return storeId;
    }

    public String getNameStore() {
        return nameStore;
    }

    public int getTotalEmployee() {
        return totalEmployee;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "StoreResponseDTO{" +
                "storeId=" + storeId +
                ", nameStore='" + nameStore + '\'' +
                ", totalEmployee=" + totalEmployee +
                '}';
    }
}
