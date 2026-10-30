package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.Employee;
import com.mottainai.crudinicial.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CONSULTAS JPA DAS CONTAS DE FUNCIONÁRIOS
// Consulta a conta vinculada ao Employee e verifica sua disponibilidade sem expor PasswordHash por DTO.

@Repository
public interface UserAccountDao extends JpaRepository<UserAccount, Integer> {
    // CARDS DE CONTAS
    // A agregação dispensa carregar email cifrado e Employee apenas para obter total e ativos.
    @org.springframework.data.jpa.repository.Query("select count(a) as total, " +
            "count(case when a.active = true and a.deletedAt is null then 1 else null end) as active from UserAccount a")
    DashboardCount summarizeDashboard();

    // RECENTES CORRELACIONADOS COM EMPLOYEE
    // O nome projetado é o nome AES do funcionário, sem senha/hash; contas apagadas ficam fora dos recentes.
    @org.springframework.data.jpa.repository.Query("select a.employee.name as name, a.employee.name as fallbackName, " +
            "a.createdAt as createdAt, a.active as active from UserAccount a " +
            "where a.deletedAt is null order by a.createdAt desc nulls last")
    java.util.List<DashboardRegistration> findDashboardRecent(org.springframework.data.domain.Pageable pageable);

    // VÍNCULO EXCLUSIVO DO FUNCIONÁRIO
    // As verificações com UserIdNot ignoram a própria conta durante a edição.
    boolean existsByEmployee(Employee employee);
    boolean existsByEmployeeAndUserIdNot(Employee employee, int userId);

    // Permite localizar a conta vinculada a um funcionário,
    // caso ele possua uma.
    Optional<UserAccount> findByEmployee(Employee employee);

    // DISPONIBILIDADE PARA OS FORMULÁRIOS
    // Consulta apenas a existência do vínculo pelo ID, sem montar o DTO completo da conta.
    boolean existsByEmployee_EmployeeId(
            Integer employeeId
    );

    boolean existsByEmployee_EmployeeIdAndUserIdNot(
            Integer employeeId,
            int userId
    );
}
