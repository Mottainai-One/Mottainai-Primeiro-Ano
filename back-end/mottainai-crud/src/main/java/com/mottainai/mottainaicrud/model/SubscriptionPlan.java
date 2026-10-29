package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "SubscriptionPlan")
@Getter
@Setter
public class SubscriptionPlan extends Registrated {

    // CHAVE PRIMÁRIA AUTOINCREMENTAL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PlanId")
    private int planId;

    // DADOS DO PLANO DE ASSINATURA
    @Column(name = "Name", nullable = false, length = 100)
    private String name;

    @Column(name = "Description", nullable = false, length = 250)
    private String description;

    @Column(name = "Price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // LIMITES OPERACIONAIS DO PLANO
    @Column(name = "StoreLimit", nullable = false)
    private int storeLimit;

    @Column(name = "UserLimit", nullable = false)
    private int userLimit;

    // CONSTRUTORES (SOBRECARGA / OVERLOADING)
    // CONSTRUTOR PADRÃO EXIGIDO PELO JPA
    public SubscriptionPlan() {}

    // SOBRECARGA DE CONSTRUTOR COMPLETO
    public SubscriptionPlan(String name, String description, BigDecimal price,
                            int storeLimit, int userLimit) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.storeLimit = storeLimit;
        this.userLimit = userLimit;
    }
}