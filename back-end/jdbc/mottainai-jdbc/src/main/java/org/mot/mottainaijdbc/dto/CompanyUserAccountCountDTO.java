package org.mot.mottainaijdbc.dto;

public class CompanyUserAccountCountDTO {
    //ATRIBUTOS
    private int companyId;
    private String nameCompany;
    private int totalUsers;
    //CONSTRUTOR
    public CompanyUserAccountCountDTO(int companyId, String nameCompany, int totalUsers) {
        this.companyId = companyId;
        this.nameCompany = nameCompany;
        this.totalUsers = totalUsers;
    }
    //GETTERS
    public int getCompanyId() {
        return companyId;
    }

    public String getNameCompany() {
        return nameCompany;
    }

    public int getTotalUsers() {
        return totalUsers;
    }
    //TOSTRING
    @Override
    public String toString() {
        return "CompanyUserAccountCountDTO{" +
                "companyId=" + companyId +
                ", nameCompany='" + nameCompany + '\'' +
                ", totalUsers=" + totalUsers +
                '}';
    }
}

