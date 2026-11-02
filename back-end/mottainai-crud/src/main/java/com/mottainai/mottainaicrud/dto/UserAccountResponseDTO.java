package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DADOS DE SAÍDA DA CONTA DE FUNCIONÁRIO
// Inclui Employee e nomes de loja/cargo; não contém senha nem PasswordHash, apenas dados preparados para consulta.

@Getter
@Setter
public class UserAccountResponseDTO {

    private int userId;

    private String email;

    private LocalDateTime lastLogin;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // CORRELAÇÃO PARA CONSULTA
    // O funcionário e os nomes relacionados são resolvidos pelo Service; PasswordHash não integra esta estrutura.
    private int employeeId;
    private String employeeName;

    private String storeName;
    private String roleName;

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

    public String getLastLoginFormatted() {
        return lastLogin == null
                ? null
                : lastLogin.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
}