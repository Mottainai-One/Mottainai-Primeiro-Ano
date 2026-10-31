package com.mottainai.crudinicial.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// DADOS DE SAÍDA DA LOJA
// Inclui nome/ID da empresa e dados completos do endereço, além dos contatos preparados pelo Service.

@Getter
@Setter
public class StoreResponseDTO {

    private int storeId;

    private String name;
    private String cnpj;
    private String email;
    private String phone;

    private BigDecimal latitude;
    private BigDecimal longitude;

    private boolean active;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private int companyId;
    private String companyName;

    private int addressId;

    private String addressZipCode;
    private String addressStreet;
    private String addressNumber;
    private String addressComplement;
    private String addressNeighborhood;
    private String addressCity;
    private String addressState;

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
