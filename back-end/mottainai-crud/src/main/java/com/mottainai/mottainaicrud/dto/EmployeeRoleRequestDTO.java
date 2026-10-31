package com.mottainai.crudinicial.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// DADOS DE ENTRADA DO CARGO
// Exige nome, situação e nível de permissão positivo; os vínculos e o cargo técnico são tratados no Service.

@Getter
@Setter
public class EmployeeRoleRequestDTO {

    @NotBlank(message = "O nome do cargo é obrigatório.")
    private String name;

    private String description;

    @NotNull(message = "O nível de permissão é obrigatório.")
    @Positive(message = "O nível de permissão deve ser maior que zero.")
    private Integer permissionLevel;

    @NotNull(message = "A situação é obrigatória.")
    private Boolean active;
}