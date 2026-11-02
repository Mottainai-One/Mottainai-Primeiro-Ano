package com.mottainai.mottainaicrud.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;

// DADOS DE ENTRADA DA LOJA
// Recebe IDs de empresa/endereço e valida CNPJ, email e faixas de latitude/longitude, quando informadas.

@Getter
@Setter
public class StoreRequestDTO {

    @NotBlank(message = "O nome da loja é obrigatório.")
    private String name;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(
            regexp = "\\d{14}",
            message = "CNPJ deve conter 14 dígitos numéricos."
    )
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "E-mail inválido.")
    private String email;

    @NotBlank(message = "O telefone é obrigatório.")
    private String phone;

    // LOCALIZAÇÃO OPCIONAL
    // Se informadas, as coordenadas são verificadas nos intervalos geográficos declarados abaixo.
    @DecimalMin(
            value = "-90",
            message = "Latitude deve estar entre -90 e 90."
    )
    @DecimalMax(
            value = "90",
            message = "Latitude deve estar entre -90 e 90."
    )
    private BigDecimal latitude;

    @DecimalMin(
            value = "-180",
            message = "Longitude deve estar entre -180 e 180."
    )
    @DecimalMax(
            value = "180",
            message = "Longitude deve estar entre -180 e 180."
    )
    private BigDecimal longitude;

    @NotNull(message = "A situação é obrigatória.")
    private Boolean active;

    @NotNull(message = "A empresa é obrigatória.")
    private Integer companyId;

    @NotNull(message = "O endereço é obrigatório.")
    private Integer addressId;
}