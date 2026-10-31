package com.mottainai.crudinicial.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DADOS DE SAÍDA DO CARGO
// Acrescenta a quantidade de funcionários vinculados e as datas de controle sem expor entidades relacionadas.

@Getter
@Setter
public class EmployeeRoleResponseDTO {

    private int roleId;

    private String name;
    private String description;

    private int permissionLevel;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private long employeeCount;

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
}