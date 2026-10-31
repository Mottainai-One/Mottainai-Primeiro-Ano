package com.mottainai.crudinicial.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

// ENTIDADE DE CARGO DO FUNCIONÁRIO
// Representa nome, descrição e nível cadastrado; o cargo de anonimização sustenta FKs após o soft delete.

@Entity
@Table(name = "EmployeeRole")
@Getter
@Setter
public class EmployeeRole extends Registrated {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private int roleId;

    @Column(name = "Name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "Description", length = 250)
    private String description;

    @Column(name = "PermissionLevel", nullable = false)
    // NÍVEL CADASTRADO DO CARGO
    // É um dado do cadastro de EmployeeRole; a autenticação administrativa atual é feita por Administration.
    private int permissionLevel;

    public EmployeeRole() {}

    public EmployeeRole(String name, String description, int permissionLevel) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }
}