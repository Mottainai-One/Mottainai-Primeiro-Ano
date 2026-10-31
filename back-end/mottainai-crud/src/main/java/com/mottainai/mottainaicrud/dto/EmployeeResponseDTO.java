package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DADOS DE SAÍDA DO FUNCIONÁRIO
// Contém loja/cargo relacionados; o Service mantém o nome histórico legível e mascara os dados anonimizados.

@Getter
@Setter
public class EmployeeResponseDTO {

    private int employeeId;

    private String cpf;
    private String name;
    private String email;
    private String phone;

    private LocalDate hireDate;

    private int storeId;
    private String storeName;

    private int roleId;
    private String roleName;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // DATAS DE APRESENTAÇÃO
    // Os formatadores abaixo atendem ao JSP e preservam null quando a respectiva data não foi registrada.
    public String getCreatedAtFormatted() {
        return createdAt == null
                ? null
                : createdAt.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

    public String getUpdatedAtFormatted() {
        return updatedAt == null
                ? null
                : updatedAt.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

    public String getDeletedAtFormatted() {
        return deletedAt == null
                ? null
                : deletedAt.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }

    public String getHireDateFormatted() {
        return hireDate == null
                ? null
                : hireDate.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")
        );
    }
}