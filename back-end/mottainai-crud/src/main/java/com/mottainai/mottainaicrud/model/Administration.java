package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

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
    private String passwordHash;
    //CONSTRUTOR
    public Administration() {}

    public Administration(String email, String passwordHash) {
        this.email = email;
        this.passwordHash = passwordHash;
    }
    //SOBRECARGA
}
