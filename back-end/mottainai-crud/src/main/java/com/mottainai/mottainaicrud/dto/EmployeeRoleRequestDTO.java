package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRoleRequestDTO {

    @NotBlank(message = "Nome do cargo é obrigatório")
    private String name;

    private String description; // opcional — a coluna é nullable no banco

    @Positive(message = "O nível de permissão deve ser maior que zero")
    @NotBlank
    private int permissionLevel;

}