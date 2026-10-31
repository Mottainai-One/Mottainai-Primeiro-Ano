package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

// CONTROLE COMUM DOS CADASTROS
// Active representa situação operacional; DeletedAt marca exclusão lógica, enquanto os timestamps registram criação/alteração.

@MappedSuperclass // não vira tabela própria; os campos são "herdados" pelas subclasses
@Getter
@Setter
public abstract class Registrated {

    // SITUAÇÃO E HISTÓRICO
    // Um cadastro pode ser inativo sem estar excluído; os Services combinam Active=false e DeletedAt no soft delete.
    @Column(name = "Active", nullable = false)
    private boolean active = true;

    @CreationTimestamp // Hibernate preenche sozinho na criação (INSERT)
    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Hibernate atualiza sozinho a cada UPDATE
    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;
}