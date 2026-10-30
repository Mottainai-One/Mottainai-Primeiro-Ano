package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeResponseDTO {
    private int employeeId;
    private String cpf;
    private String name;
    private String email;
    private String phone;
    private LocalDate hireDate;
    private int storeId;
    private int roleId;
}