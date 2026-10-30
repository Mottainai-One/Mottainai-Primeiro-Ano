package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountDao extends JpaRepository<UserAccount, Integer> {
    boolean existsByEmail(String email);
    // TRUQUE ÚTIL: DÁ PRA DERIVAR A QUERY PASSANDO A ENTIDADE RELACIONADA DIRETO,
    // SEM PRECISAR SABER O TIPO EXATO DA PK DE EMPLOYEE
    boolean existsByEmployee(Employee employee);
    boolean existsByEmployeeAndUserIdNot(Employee employee, int userId);

}   