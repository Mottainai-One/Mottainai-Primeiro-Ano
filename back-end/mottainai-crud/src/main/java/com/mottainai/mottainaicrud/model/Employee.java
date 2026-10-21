package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "Employee")

@Getter
@Setter
public class Employee extends Registrated {

    @Id
    @Setter(AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EmployeeId")
    private int employeeId;

    @Column(name = "Name", nullable = false, length = 500)
    private String name;

    @Column(name = "CPF", nullable = false, length = 500)
    private String cpf;

    @Column(name = "Email", nullable = false, length = 500)
    private String email;

    @Column(name = "Phone", nullable = false, length = 500)
    private String phone;

    @Column(name = "HireDate", nullable = false)
    private LocalDate hireDate;

    @ManyToOne
    @JoinColumn(name = "StoreId", nullable = false)
    private Store store;

    @ManyToOne
    @JoinColumn(name = "RoleId", nullable = false)
    private EmployeeRole role;

    //CONSTRUTOR

    public Employee() {}

    public Employee(String email, String phone, EmployeeRole role, LocalDate hireDate ,String cpf, String name, Store store) {
        this.cpf = cpf;
        this.name = name;
        this.store = store;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.hireDate = hireDate;
    }
}
