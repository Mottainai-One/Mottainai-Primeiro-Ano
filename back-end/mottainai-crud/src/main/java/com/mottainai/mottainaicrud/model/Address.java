package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    // Callbacks JPA registram criação/alteração; o Service preenche DeletedAt, sem campo Active nesta entidade.
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @PrePersist
    // Inicializa as datas antes de inserir, com a precisão de microssegundos usada pelo PostgreSQL.
    private void initializeTimestamps() {
        if (createdAt == null) createdAt = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        updateTimestamp();
    }

    @PreUpdate
    // Uma exclusão não pode deixar UpdatedAt anterior a DeletedAt.
    private void updateTimestamp() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
        if (createdAt != null && now.isBefore(createdAt)) now = createdAt;
        if (deletedAt != null && now.isBefore(deletedAt)) now = deletedAt;
        updatedAt = now;
    }

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
