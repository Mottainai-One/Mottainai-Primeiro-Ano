package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAccountRequestDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String passwordHash;

    @NotNull(message = "O funcionário é obrigatório")
    @Positive(message = "O id do funcionário deve ser positivo")
    private Integer employeeId;

}