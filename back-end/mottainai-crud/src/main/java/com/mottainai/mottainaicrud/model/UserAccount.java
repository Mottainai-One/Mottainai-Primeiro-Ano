package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserAccount")
@Getter
@Setter
public class UserAccount extends Registrated {

    // CHAVE PRIMÁRIA AUTOINCREMENTAL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private int userId;

    // CREDENCIAIS DE ACESSO E AUTENTICAÇÃO
    @Column(name = "Email", nullable = false, length = 500)
    private String email;

    // HASH CRIPTOGRÁFICO DA SENHA (NUNCA ARMAZENAR SENHA EM TEXTO PLANO)
    @Column(name = "PasswordHash", nullable = false, length = 255)
    private String passwordHash;

    // REGISTRO DE ÚLTIMO ACESSO DO USUÁRIO NO SISTEMA
    @Column(name = "LastLogin")
    private LocalDateTime lastLogin;

    // RELACIONAMENTO ENTRE TABELAS (CHAVE ESTRANGEIRA)
    // CARDINALIDADE 1:1 - UMA CONTA DE USUÁRIO PERTENCE EXCLUSIVAMENTE A UM FUNCIONÁRIO
    @OneToOne
    @JoinColumn(name = "EmployeeId", nullable = false, unique = true)
    private Employee employee;

    // CONSTRUTORES (SOBRECARGA / OVERLOADING)
    // CONSTRUTOR PADRÃO EXIGIDO PELO JPA
    public UserAccount() {}

    // SOBRECARGA DE CONSTRUTOR PARA CRIAÇÃO DE NOVA CONTA
    public UserAccount(String email, String passwordHash, Employee employee) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.employee = employee;
    }
}