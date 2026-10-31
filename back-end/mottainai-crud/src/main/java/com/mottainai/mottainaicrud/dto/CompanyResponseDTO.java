package com.mottainai.crudinicial.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DADOS DE SAÍDA DA EMPRESA
// Inclui ID/nome do plano; email e telefone são legíveis ou marcados como anonimizados pelo Service.
@Getter
@Setter
public class CompanyResponseDTO {

    private int companyId;

    private String cnpj;
    private String officialName;
    private String tradeName;

    private String email;
    private String phone;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private int subscriptionPlanId;
    private String subscriptionPlanName;

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