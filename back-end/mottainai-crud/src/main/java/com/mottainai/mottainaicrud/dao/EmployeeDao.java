package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.EmployeeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// CONSULTAS JPA DE FUNCIONÁRIOS
// Apoia a cascata por loja, a proteção de cargos vinculados e as projeções usadas nas listagens/painel.

@Repository
public interface EmployeeDao extends JpaRepository<Employee, Integer> {
    // TOTAL E ATIVOS DO PAINEL
    // Conta no banco sem materializar dados pessoais ou relações de todos os funcionários.
    @org.springframework.data.jpa.repository.Query("select count(e) as total, " +
            "count(case when e.active = true and e.deletedAt is null then 1 else null end) as active from Employee e")
    DashboardCount summarizeDashboard();

    // PROJEÇÃO DOS FUNCIONÁRIOS RECENTES
    // Filtra apagados e ordena por criação; name permanece cifrado até a montagem da resposta pelo Service.
    @org.springframework.data.jpa.repository.Query("select e.name as name, e.name as fallbackName, " +
            "e.createdAt as createdAt, e.active as active from Employee e " +
            "where e.deletedAt is null order by e.createdAt desc nulls last")
    List<DashboardRegistration> findDashboardRecent(org.springframework.data.domain.Pageable pageable);

    // CONTAGEM EM LOTE POR CARGO
    // Uma linha por roleId substitui as contagens individuais na listagem, preservando os vínculos históricos.
    @org.springframework.data.jpa.repository.Query(
            "select e.role.roleId as id, count(e) as total from Employee e group by e.role.roleId")
    List<GroupedCount> countGroupedByRole();

    // PROTEÇÃO E CASCATA DOS VÍNCULOS
    // EXISTS impede excluir cargo em uso; a busca por StoreId fornece os funcionários afetados pela exclusão da loja.
    boolean existsByRole(EmployeeRole role);
    long countByRole(EmployeeRole role);
    List<Employee> findByStore_StoreId(Integer storeId);
}
