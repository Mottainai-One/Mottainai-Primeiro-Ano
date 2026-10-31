package com.mottainai.crudinicial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// ENTIDADE DE ENDEREÇO
// O ID é gerado pelo banco; DeletedAt permite a anonimização/exclusão lógica sem remover o vínculo da loja.

@Entity
@Table(name = "Address")
@Getter
@Setter
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AddressId")
    private Integer addressId;

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

    // DATAS DE CONTROLE
    // Hibernate registra criação/alteração; o Service preenche DeletedAt ao anonimizar, sem um campo Active nesta entidade.
    @CreationTimestamp
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    public Address() {}

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