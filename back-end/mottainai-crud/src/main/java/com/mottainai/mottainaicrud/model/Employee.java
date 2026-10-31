package com.mottainai.crudinicial.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

// ENTIDADE DE FUNCIONÁRIO
// Nome, CPF, email e telefone são armazenados com AES; loja e cargo preservam os vínculos exigidos pelo banco.

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
    // CAMPOS PESSOAIS CIFRADOS
    // Nome, CPF, email e telefone usam AES; após exclusão somente o nome continua cifrado e recuperável.
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
    // VÍNCULOS OBRIGATÓRIOS
    // O soft delete troca loja/cargo por registros técnicos, em vez de deixar essas FKs nulas.
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
