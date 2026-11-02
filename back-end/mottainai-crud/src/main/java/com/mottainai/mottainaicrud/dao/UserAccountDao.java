package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// CONSULTAS JPA DAS CONTAS DE FUNCIONÁRIOS
// Consulta a conta vinculada ao Employee e verifica sua disponibilidade sem expor PasswordHash por DTO.

@Repository
public interface UserAccountDao extends JpaRepository<UserAccount, Integer> {
    String FETCH = "select a from UserAccount a left join fetch a.employee e " +
            "left join fetch e.store s left join fetch s.company c " +
            "left join fetch c.subscriptionPlan left join fetch s.address left join fetch e.role";

    @Override
    @org.springframework.data.jpa.repository.Query(FETCH)
    java.util.List<UserAccount> findAll();

    @Override
    @org.springframework.data.jpa.repository.Query(FETCH + " where a.userId = :id")
    Optional<UserAccount> findById(@org.springframework.data.repository.query.Param("id") Integer id);

    @org.springframework.data.jpa.repository.Query(value = FETCH +
            " where (:active is null or a.active = :active) order by a.userId",
            countQuery = "select count(a) from UserAccount a where (:active is null or a.active = :active)")
    org.springframework.data.domain.Page<UserAccount> findPageWithRelations(
            @org.springframework.data.repository.query.Param("active") Boolean active,
            org.springframework.data.domain.Pageable pageable);

    // Uma consulta substitui uma verificação de disponibilidade por funcionário.
    @org.springframework.data.jpa.repository.Query("select a.employee.employeeId from UserAccount a " +
            "where (:currentUserId is null or a.userId <> :currentUserId)")
    java.util.Set<Integer> findOccupiedEmployeeIds(
            @org.springframework.data.repository.query.Param("currentUserId") Integer currentUserId);

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
