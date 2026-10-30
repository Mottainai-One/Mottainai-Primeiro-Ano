package com.mottainai.crudinicial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

// ENTIDADE DAS CREDENCIAIS ADMINISTRATIVAS
// Email identifica o administrador e PasswordHash armazena BCrypt; essa autenticação é separada de UserAccount.

@Entity
@Table(name = "Administration")

@Getter
@Setter
public class Administration {

    //ATRIBUTOS
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    @Column(name = "AdministrationId")
    private int administrationId;

    @Column(name = "Email", nullable = false, length = 100)
    private String email;

    @Column(name = "PasswordHash", nullable = false)
    // HASH DE AUTENTICAÇÃO
    // O valor persistido é BCrypt; a entidade não deve ser enviada como modelo da página de login.
    private String passwordHash;
    //CONSTRUTOR
    public Administration() {}

    public Administration(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    //SOBRECARGA
}
