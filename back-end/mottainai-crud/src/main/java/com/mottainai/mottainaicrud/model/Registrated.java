package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// CLASSE BASE ABSTRATA DE AUDITORIA (HERANÇA JPA)
// NÃO GERA TABELA PRÓPRIA NO BANCO DE DADOS
// OS CAMPOS SÃO HERDADOS PELAS SUBCLASSES CONCRETAS

@MappedSuperclass // não vira tabela própria; os campos são "herdados" pelas subclasses
@Getter
@Setter
public abstract class Registrated {

    // CONTROLE DE ESTADO E SOFT DELETE
    @Column(name = "Active", nullable = false)
    private boolean active = true;

    // PREENCHIMENTO AUTOMÁTICO PELO HIBERNATE NO INSERT
    @CreationTimestamp // HIBERNATE PREENCHE SOZINHO NA CRIAÇÃO (INSERT)
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // HIBERNATE ATUALIZA SOZINHO A CADA UPDATE
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    // DATA DE EXCLUSÃO LÓGICA (SOFT DELETE)
    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;
}