package com.mottainai.crudinicial.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;

import java.time.LocalDateTime;

// DADOS DE SAÍDA DO ENDEREÇO
// Reúne endereço, nome da loja vinculada e datas de controle, sem entregar a entidade JPA à listagem.

@Getter
@Setter
public class AddressResponseDTO {

    private Integer addressId;

    private String zipCode;
    private String street;
    private String number;
    private String complement;
    private String neighborhood;
    private String city;
    private String state;

    private String storeName;

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

    public String getDeletedAtFormatted() {
        return deletedAt == null
                ? null
                : deletedAt.format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
        );
    }
}