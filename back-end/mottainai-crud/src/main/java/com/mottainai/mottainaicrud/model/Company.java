package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Company")

@Getter
@Setter
public class Company extends Registrated{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CompanyId")
    private int companyId;

    @Column(name = "OfficialName", nullable = false, length = 120)
    private String officialName;

    @Column(name = "TradeName", length = 120)
    private String tradeName;

    @Column(name = "CNPJ", nullable = false, length = 14)
    private String cnpj;

    @Column(name = "Email", nullable = false, length = 500)
    private String email;

    @Column(name = "Phone", length = 500)
    private String phone;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    @Column(name = "Longitude")
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name = "SubscriptionPlanId", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    //CONSTRUTOR
    public Company() {}

    public Company(String cnpj, String officialName, String email, SubscriptionPlan subscriptionPlan) {
        this.cnpj = cnpj;
        this.officialName = officialName;
        this.email = email;
        this.subscriptionPlan = subscriptionPlan;
    }

    public Company(String officialName, String tradeName, String cnpj, String email, String phone, BigDecimal latitude, BigDecimal longitude, SubscriptionPlan subscriptionPlan) {
        this.officialName = officialName;
        this.tradeName = tradeName;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
        this.latitude = latitude;
        this.longitude = longitude;
        this.subscriptionPlan = subscriptionPlan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(cnpj, company.cnpj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cnpj);
    }

    @Override
    public String toString() {
        return "Company {" +
                "cnpj='" + cnpj + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}