package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreRequestDTO {

    // id da loja é gerado no banco (create) ou do update.

    @NotBlank(message = "Nome da loja deve ser preenchido.")
    private String name;

    @NotBlank(message = "CNPJ da loja é necessário.")
    @Pattern(regexp = "\\d{14}", message = "CNPJ deve conter 14 dígitos numéricos")
    private String cnpj;

    @NotBlank(message = "E-mail da loja deve ser preenchido")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Telefone da loja deve ser preenchido")
    private String phone;

    @NotNull(message = "A empresa é obrigatória")
    @Positive(message = "O id da empresa deve ser positivo")
    private Integer companyId;

    @NotNull(message = "O endereço é obrigatório")
    @Positive(message = "O id do endereço deve ser positivo")
    private Integer addressId;

}