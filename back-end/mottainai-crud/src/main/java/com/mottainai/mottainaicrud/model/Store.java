package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "Store")
@Getter
@Setter
public class Store extends Registrated{

    // CHAVE PRIMÁRIA AUTOINCREMENTAL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "StoreId")
    private int storeId;

    // DADOS CADASTRAIS DA FILIAL / LOJA
    @Column(name = "Name", nullable = false, length = 120)
    private String name;

    @Column(name = "CNPJ", nullable = false, length = 14)
    private String cnpj;

    @Column(name = "Email", nullable = false, length = 500)
    private String email;

    @Column(name = "Phone", nullable = false, length = 500)
    private String phone;

    // GEOLOCALIZAÇÃO
    @Column(name = "Longitude")
    private BigDecimal longitude;

    @Column(name = "Latitude")
    private BigDecimal latitude;

    // RELACIONAMENTOS ENTRE TABELAS (CHAVES ESTRANGEIRAS)
    // CARDINALIDADE N:1 - MUITAS LOJAS PERTENCEM A UMA MESMA EMPRESA
    @ManyToOne
    @JoinColumn(name = "CompanyId", nullable = false)
    private Company company;

    // CARDINALIDADE 1:1 - CADA LOJA POSSUI UM ÚNICO ENDEREÇO EXCLUSIVO
    @OneToOne
    @JoinColumn(name = "AddressId", nullable = false, unique = true)
    private Address address;

    // CONSTRUTORES (SOBRECARGA / OVERLOADING)

    // CONSTRUTOR PADRÃO EXIGIDO PELO JPA
    public Store() {}

    // SOBRECARGA DE CONSTRUTOR COMPLETO PARA CADASTRO DE LOJA
    public Store(String name, String cnpj, String email, String phone, Company company, Address address) {
        this.name = name;
        this.cnpj = cnpj;
        this.email = email;
        this.phone = phone;
        this.company = company;
        this.address = address;
    }
}