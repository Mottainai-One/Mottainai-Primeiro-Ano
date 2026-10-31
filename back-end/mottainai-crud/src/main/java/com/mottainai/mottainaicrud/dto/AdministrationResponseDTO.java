package com.mottainai.crudinicial.dto;

import lombok.Getter;
import lombok.Setter;

// SAÍDA MÍNIMA DA ADMINISTRATION
// Contém somente ID e email; PasswordHash não é transferido ao Controller, JSP ou sessão por este DTO.

@Getter
@Setter
public class AdministrationResponseDTO {

    private int administrationId;
    private String email;
}