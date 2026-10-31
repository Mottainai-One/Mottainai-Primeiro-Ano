package com.mottainai.crudinicial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

// ENTIDADE DA CONTA VINCULADA AO FUNCIONÁRIO
// Email é protegido por AES e PasswordHash por BCrypt; a sessão do painel administrativo utiliza Administration.

@Entity
@Table(name = "UserAccount")
@Getter
@Setter
public class UserAccount extends Registrated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private int userId;

    @Column(name = "Email", nullable = false, length = 500)
    private String email;

    @Column(name = "PasswordHash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "LastLogin")
    private LocalDateTime lastLogin;

    // UMA CONTA POR FUNCIONÁRIO
    // EmployeeId é obrigatório e único; o vínculo é preservado como referência no soft delete da conta.
    @OneToOne
    @JoinColumn(name = "EmployeeId", nullable = false, unique = true)
    private Employee employee;

    public UserAccount() {}

    public UserAccount(String email, String passwordHash, Employee employee) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.employee = employee;
    }
}