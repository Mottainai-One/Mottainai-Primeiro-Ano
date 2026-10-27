package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class UserAccountResponseDTO {
    private int userId;
    private String email;
    private LocalDateTime lastLogin;
    private int employeeId;
}