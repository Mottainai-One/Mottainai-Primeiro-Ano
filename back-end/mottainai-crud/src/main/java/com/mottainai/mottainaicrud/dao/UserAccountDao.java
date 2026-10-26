package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAccountDao extends JpaRepository<UserAccount, Integer> {
    boolean existsByEmail(String email);
    // Truque útil: dá pra derivar a query passando a entidade relacionada direto,
    // sem precisar saber o tipo exato da PK de Employee.
    boolean existsByEmployeeId(Employee employeeId);
}