package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class SubscriptionPlanResponseDTO {
    private int planId;
    private String name;
    private String description;
    private BigDecimal price;
    private int storeLimit;
    private int userLimit;
}