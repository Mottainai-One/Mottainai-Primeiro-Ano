package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer> {
    // VERIFICACAO DE EXISTENCIA POR CPF
    boolean existsByCpf(String cpf);
    Optional<Employee> findByCpf(String cpf);

    boolean existsByRole(EmployeeRole role);
    boolean existsByCpfAndEmployeeIdNot(String cpf, int employeeId);
}