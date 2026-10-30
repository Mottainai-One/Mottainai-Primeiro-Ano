package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StoreResponseDTO {
    private int storeId;
    private String name;
    private String cnpj;
    private String email;
    private String phone;
    private int companyId;
    private int addressId;

}
