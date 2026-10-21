package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private int permissionLevel;

    public EmployeeRole() {}

    public EmployeeRole(String name, String description, int permissionLevel) {
        this.name = name;
        this.description = description;
        this.permissionLevel = permissionLevel;
    }
}