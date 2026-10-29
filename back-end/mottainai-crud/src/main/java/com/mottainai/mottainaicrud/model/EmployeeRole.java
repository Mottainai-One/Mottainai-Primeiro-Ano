package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EmployeeRole")
@Getter
@Setter
public class EmployeeRole extends Registrated {

    // CHAVE PRIMÁRIA AUTOINCREMENTAL
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private int roleId;

    // DADOS DO CARGO E PERMISSÕES
    @Column(name = "Name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "Description", length = 250)
    private String description;

    // NÍVEL HIERÁRQUICO DE ACESSO DO CARGO NO SISTEMA
    @Column(name = "PermissionLevel", nullable = false)
    private int permissionLevel;

    // CONSTRUTORES (SOBRECARGA / OVERLOADING)
    // CONSTRUTOR PADRÃO EXIGIDO PELO JPA
    public EmployeeRole() {}

    // SOBRECARGA DE CONSTRUTOR COMPLETO
    public EmployeeRole(String name, String description, int permissionLevel) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }
}