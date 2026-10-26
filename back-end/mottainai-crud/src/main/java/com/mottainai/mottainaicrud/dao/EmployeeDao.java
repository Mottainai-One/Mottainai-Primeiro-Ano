package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer> {
    // A PK agora é employeeId (String gerado) — o comentário antigo dizendo
    // "CPF é String, então a PK é String" estava desatualizado. CPF não é mais PK.

    boolean existsByCpf(String cpf);
    Optional<Employee> findByCpf(String cpf);
}