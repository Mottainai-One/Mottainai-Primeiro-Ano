package com.mottainai.mottainaicrud.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdministrationRequestDTO {

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido.")
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    private String passwordHash;

}
