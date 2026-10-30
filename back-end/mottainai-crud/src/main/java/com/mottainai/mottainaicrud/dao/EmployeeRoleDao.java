package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// CONSULTAS JPA DE CARGOS
// Verifica nomes duplicados e localiza pelo nome o cargo reservado para funcionários anonimizados.

@Repository
public interface EmployeeRoleDao extends JpaRepository<EmployeeRole, Integer> {

    // DUPLICIDADE DE CARGO
    // A consulta por nome também permite ao Service reconhecer o cargo técnico sem depender apenas do ID.
    boolean existsByName(String name);
    // Permite localizar o cargo reservado usado
    // para funcionários que foram anonimizados.
    Optional<EmployeeRole> findByName(String name);
}