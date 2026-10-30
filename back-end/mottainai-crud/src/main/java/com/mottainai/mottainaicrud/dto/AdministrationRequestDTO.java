package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministrationRequestDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido.")
    private String email;

    @NotBlank
    private String passwordHash;

}
