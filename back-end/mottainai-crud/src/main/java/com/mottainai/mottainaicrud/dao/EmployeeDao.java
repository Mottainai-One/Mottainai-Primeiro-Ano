package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer> {
    // A PK AGORA EH EMPLOYEEID (STRING GERADA) — O COMENTÁRIO ANTIGO DIZENDO
    // "CPF EH STRING, ENTAO A PK EH STRING" ESTAVA DESATUALIZADO. CPF NAO EH MAIS PK

    boolean existsByCpf(String cpf);
    Optional<Employee> findByCpf(String cpf);
}