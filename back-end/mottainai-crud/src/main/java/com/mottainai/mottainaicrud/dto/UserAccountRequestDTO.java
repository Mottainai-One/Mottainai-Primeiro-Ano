package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountRequestDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String passwordHash;

    @NotNull(message = "O funcionário é obrigatório")
    @Positive(message = "O id do funcionário deve ser positivo")
    private Integer employeeId;

}