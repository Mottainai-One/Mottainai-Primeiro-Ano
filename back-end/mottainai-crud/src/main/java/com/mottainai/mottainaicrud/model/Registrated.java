package com.mottainai.mottainaicrud.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@MappedSuperclass // não vira tabela própria; os campos são "herdados" pelas subclasses
@Getter
@Setter
public abstract class Registrated {

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