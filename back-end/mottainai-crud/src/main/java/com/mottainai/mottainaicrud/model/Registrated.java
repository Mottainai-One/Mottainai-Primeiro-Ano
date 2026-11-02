package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @Column(name = "DeletedAt")
    private LocalDateTime deletedAt;

    @PrePersist
    // Usa um único fluxo de datas tanto na inclusão quanto na atualização do registro.
    private void initializeTimestamps() {
        if (createdAt == null) createdAt = timestamp();
        updateTimestamp();
    }

    @PreUpdate
    // Mantém a ordem cronológica exigida pelos CHECKs, inclusive nas exclusões lógicas.
    private void updateTimestamp() {
        LocalDateTime now = timestamp();
        // PostgreSQL armazena microssegundos. A exclusão e o update devem respeitar
        // a mesma precisão e a constraint UpdatedAt >= DeletedAt.
        if (createdAt != null && now.isBefore(createdAt)) now = createdAt;
        if (deletedAt != null && now.isBefore(deletedAt)) now = deletedAt;
        updatedAt = now;
    }

    private static LocalDateTime timestamp() {
        return LocalDateTime.now().truncatedTo(java.time.temporal.ChronoUnit.MICROS);
    }
}
