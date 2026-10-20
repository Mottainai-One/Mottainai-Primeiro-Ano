package org.mot.mottainaijdbc.dto;

public class StoreUserAccountCountDTO {
    //ATRIBTUOS
    private int storeId;
    private String store;
    private int totalUsers;
    //CONSTRUTOR
    public StoreUserAccountCountDTO(int storeId, String store, int totalUsers) {
        this.storeId = storeId;
        this.store = store;
        this.totalUsers = totalUsers;
    }
    //GETTERS
    public int getStoreId() {
        return storeId;
    }

    public String getStore() {
        return store;
    }

    public int getTotalUsers() {
        return totalUsers;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "StoreUserAccountCountDTO{" +
                "storeId=" + storeId +
                ", store='" + store + '\'' +
                ", totalUsers=" + totalUsers +
                '}';
    }
}
