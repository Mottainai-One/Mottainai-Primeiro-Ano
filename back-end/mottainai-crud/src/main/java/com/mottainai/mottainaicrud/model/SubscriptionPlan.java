package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// ENTIDADE DE PLANO DE ASSINATURA
// Guarda preço e limites cadastrados; o Service preserva o nome ao anonimizar e excluir logicamente o plano.

@Entity
@Table(name = "SubscriptionPlan")
@Getter
@Setter
public class SubscriptionPlan extends Registrated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanId")
    private int planId;

    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Description", nullable = false, length = 250)
    private String description;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "StoreLimit", nullable = false)
    private int storeLimit;

    @Column(name = "UserLimit", nullable = false)
    private int userLimit;

    public SubscriptionPlan() {}

    public SubscriptionPlan(String name, String description, BigDecimal price,
                            int storeLimit, int userLimit) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.storeLimit = storeLimit;
        this.userLimit = userLimit;
    }
}