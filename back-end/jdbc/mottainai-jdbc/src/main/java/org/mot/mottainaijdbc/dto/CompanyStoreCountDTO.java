package org.mot.mottainaijdbc.dto;

public class CompanyStoreCountDTO {
    //ATRIBUTOS
    private int companyId;
    private String tradeName;
    private int totalStores;
    //CONSTRUTOR
    public CompanyStoreCountDTO(int companyId, String tradeName, int totalStores) {
        this.companyId = companyId;
        this.tradeName = tradeName;
        this.totalStores = totalStores;
    }

    //GETTERS
    public int getCompanyId() {
        return companyId;
    }

    public String getTradeName() {
        return tradeName;
    }

    public int getTotalStores() {
        return totalStores;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "CompanyResponseDTO{" +
                "companyId=" + companyId +
                ", tradeName='" + tradeName + '\'' +
                ", totalStores=" + totalStores +
                '}';
    }
}

