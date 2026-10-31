package com.mottainai.crudinicial.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// DADOS DE ENTRADA DA EMPRESA
// Valida CNPJ/email e recebe o ID do plano; o Service resolve o vínculo e protege email/telefone com AES.

@Getter
@Setter
public class CompanyRequestDTO {

    @NotBlank(message = "CNPJ é obrigatório")
    @Pattern(
            regexp = "\\d{14}",
            message = "CNPJ deve conter 14 dígitos numéricos"
    )
    private String cnpj;

    @NotBlank(message = "Razão social é obrigatória")
    private String officialName;

    private String tradeName;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    private String phone;

    private BigDecimal latitude;
    private BigDecimal longitude;

    @NotNull(message = "A situação é obrigatória.")
    private Boolean active;

    @NotNull(message = "O plano de assinatura é obrigatório")
    @Positive(message = "O id do plano deve ser positivo")
    private Integer subscriptionPlanId;
}