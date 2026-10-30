package com.mottainai.crudinicial.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SubscriptionPlanRequestDTO {

    @NotBlank(message = "Nome do plano é obrigatório")
    private String name;

    @NotBlank(message = "Descrição do plano é obrigatória")
    private String description;

    @NotNull(message = "Preço é obrigatório")
    @Positive(message = "Preço deve ser maior que zero")
    private BigDecimal price;

    @NotNull(message = "Limite de lojas é obrigatório")
    @Positive(message = "Limite de lojas deve ser maior que zero")
    private Integer storeLimit;

    @NotNull(message = "Limite de usuários é obrigatório")
    @Positive(message = "Limite de usuários deve ser maior que zero")
    private Integer userLimit;
}