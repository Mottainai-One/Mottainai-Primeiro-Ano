package com.mottainai.mottainaicrud.dto;

import com.mottainai.mottainaicrud.validation.ValidCPF;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// DADOS DE ENTRADA DO FUNCIONÁRIO
// Valida CPF/email, data e IDs de loja/cargo; os dados pessoais chegam em claro e são cifrados pelo Service.

@Getter
@Setter
public class EmployeeRequestDTO {

    // VALIDAÇÃO ANTES DA CRIPTOGRAFIA
    // NotBlank exige valor e ValidCPF confere os verificadores; o AES é aplicado posteriormente no Service.
    @NotBlank(message = "CPF é obrigatório")
    @ValidCPF
    private String cpf;

    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;

    @NotBlank(message = "Telefone é obrigatório")
    private String phone;

    @NotNull(message = "Data de contratação é obrigatória")
    @PastOrPresent(message = "Data de contratação não pode ser futura")
    // Informa ao Spring que a data enviada pelo input HTML
    // deve ser interpretada no formato ISO: yyyy-MM-dd.
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate hireDate;

    @NotNull(message = "Loja é obrigatória")
    @Positive(message = "O id da loja deve ser positivo")
    private Integer storeId;

    @NotNull(message = "Cargo é obrigatório")
    @Positive(message = "O id do cargo deve ser positivo")
    private Integer roleId;

    @NotNull(message = "Situação é obrigatória")
    private Boolean active = true;
}