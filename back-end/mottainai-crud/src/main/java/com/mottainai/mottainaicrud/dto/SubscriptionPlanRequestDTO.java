package com.mottainai.crudinicial.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// DADOS DE ENTRADA DO PLANO
// Valida preço mínimo, limites positivos e situação; a existência de empresas vinculadas é regra do Service.

@Getter
@Setter
public class SubscriptionPlanRequestDTO {

    @NotBlank(message = "O nome do plano é obrigatório.")
    private String name;

    @NotBlank(message = "A descrição é obrigatória.")
    private String description;

    @NotNull(message = "O preço é obrigatório.")
    @DecimalMin(
            value = "0.01",
            message = "O preço deve ser maior que zero."
    )
    private BigDecimal price;

    @NotNull(message = "O limite de lojas é obrigatório.")
    @Positive(message = "O limite de lojas deve ser maior que zero.")
    private Integer storeLimit;

    @NotNull(message = "O limite de usuários é obrigatório.")
    @Positive(message = "O limite de usuários deve ser maior que zero.")
    private Integer userLimit;

    @NotNull(message = "A situação é obrigatória.")
    private Boolean active;
}