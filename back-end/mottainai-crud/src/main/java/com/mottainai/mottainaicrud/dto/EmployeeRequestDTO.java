package com.mottainai.mottainaicrud.dto;

import com.mottainai.crudinicial.validation.ValidCPF;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeRequestDTO {

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
    private LocalDate hireDate;

    @NotNull(message = "Loja é obrigatória")
    @Positive(message = "O id da loja deve ser positivo")
    private Integer storeId;

    @NotNull(message = "Cargo é obrigatório")
    @Positive(message = "O id do cargo deve ser positivo")
    private Integer roleId;
}