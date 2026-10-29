package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRoleDao extends JpaRepository<EmployeeRole, Integer> {
    boolean existsByName(String name); // BATE COM A CONSTRAINT UNEMPLOYEEROLENAME
}