package com.mottainai.crudinicial.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

// ENTRADA PRIVADA: a senha é recebida somente para gerar BCrypt e nunca compõe uma resposta.
@Getter
@Setter
public class AdministrationRequestDTO {
    @NotBlank(message = "Email é obrigatório.")
    @Email(message = "Email inválido.")
    @Size(max = 100, message = "Email deve ter até 100 caracteres.")
    private String email;

    // Compatibilidade com o nome histórico da API desativada; o formulário usa password.
    @JsonAlias("passwordHash")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(max = 72, message = "Senha deve ter até 72 bytes em UTF-8.")
    private String password;
}
