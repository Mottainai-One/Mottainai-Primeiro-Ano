package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt; // vem de Registrated — substitui o antigo registrationDate
    private int subscriptionPlanId;
}