package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "Address")
@Getter
@Setter
public class Address {

    // CHAVE PRIMÁRIA AUTOINCREMENTAL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId")
    private Integer addressId;

    // DADOS DE LOCALIZAÇÃO E LOGRADOURO
    @Column(name = "ZipCode", nullable = false, length = 8)
    private String zipCode;

    @Column(name = "Street", nullable = false, length = 120)
    private String street;

    @Column(name = "Number", nullable = false, length = 60)
    private String number;

    @Column(name = "Complement", length = 250)
    private String complement;

    @Column(name = "Neighborhood", nullable = false, length = 60)
    private String neighborhood;

    @Column(name = "City", nullable = false, length = 100)
    private String city;

    @Column(name = "State", nullable = false, length = 80)
    private String state;

    // PREENCHIMENTO AUTOMÁTICO PELO HIBERNATE NO INSERT
    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // ATUALIZAÇÃO AUTOMÁTICA PELO HIBERNATE A CADA UPDATE
    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // DATA DE EXCLUSÃO LÓGICA (SOFT DELETE)
    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    // CONSTRUTORES (SOBRECARGA / OVERLOADING)
    // CONSTRUTOR PADRÃO EXIGIDO PELO JPA
    public Address() {}

    // SOBRECARGA DE CONSTRUTOR COMPLETO
    public Address(String zipCode, String street, String number, String complement,
                   String neighborhood, String city, String state) {
        this.zipCode = zipCode;
        this.street = street;
        this.number = number;
        this.complement = complement;
        this.neighborhood = neighborhood;
        this.city = city;
        this.state = state;
    }
}