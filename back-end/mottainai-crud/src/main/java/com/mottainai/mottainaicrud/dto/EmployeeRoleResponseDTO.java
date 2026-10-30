package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeRoleResponseDTO {
    private int roleId;
    private String name;
    private String description;
    private int permissionLevel;
}