package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
}