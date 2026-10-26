package com.mottainai.mottainaicrud.dao;

import com.mottainai.crudinicial.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRoleDao extends JpaRepository<EmployeeRole, Integer> {
    boolean existsByName(String name); // bate com a constraint unEmployeeRoleName
}