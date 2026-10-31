package com.mottainai.crudinicial.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

// DADOS DE ENTRADA DA CONTA DE FUNCIONÁRIO
// Recebe a senha informada e o ID de Employee; o Service aplica BCrypt e nunca persiste essa senha em claro.

@Getter
@Setter
public class UserAccountRequestDTO {

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    // O formulário envia a senha normal.
    // O hash é gerado somente dentro do service.
    // SENHA DE ENTRADA
    // Na edição, valor vazio mantém o hash existente; a criação e o tamanho mínimo são verificados no Service.
    private String password;

    @NotNull(message = "A situação é obrigatória.")
    private Boolean active;

    @NotNull(message = "O funcionário é obrigatório.")
    @Positive(message = "O id do funcionário deve ser positivo.")
    private Integer employeeId;
}