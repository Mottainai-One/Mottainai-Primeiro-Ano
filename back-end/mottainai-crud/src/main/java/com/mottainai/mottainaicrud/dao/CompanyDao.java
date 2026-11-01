package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// CONSULTAS JPA DE EMPRESAS E SEUS PLANOS
// Reúne consultas específicas e projeções para o dashboard e para contagens sem uma consulta por plano.

@Repository
public interface CompanyDao extends JpaRepository<Company, Integer> {
    // AGREGAÇÃO DOS CARDS
    // Conta total/ativos em uma projeção; apagados entram no total, mas não nos ativos, e o ID técnico é excluído.
    @org.springframework.data.jpa.repository.Query("select count(c) as total, " +
            "count(case when c.active = true and c.deletedAt is null then 1 else null end) as active " +
            "from Company c where c.companyId <> :technicalId")
    DashboardCount summarizeDashboard(@org.springframework.data.repository.query.Param("technicalId") int technicalId);

    // RECENTES SEM ENTIDADE COMPLETA
    // Retira soft deleted e ID técnico; Pageable limita o resultado, com datas nulas após as demais.
    @org.springframework.data.jpa.repository.Query("select c.tradeName as name, c.officialName as fallbackName, " +
            "c.createdAt as createdAt, c.active as active from Company c " +
            "where c.deletedAt is null and c.companyId <> :technicalId order by c.createdAt desc nulls last")
    List<DashboardRegistration> findDashboardRecent(@org.springframework.data.repository.query.Param("technicalId") int technicalId,
                                                   org.springframework.data.domain.Pageable pageable);

    // CONTAGEM EM LOTE POR PLANO
    // Mantém os vínculos existentes na contagem e evita executar countBySubscriptionPlan para cada linha da lista.
    @org.springframework.data.jpa.repository.Query(
            "select c.subscriptionPlan.planId as id, count(c) as total from Company c group by c.subscriptionPlan.planId")
    List<GroupedCount> countGroupedBySubscriptionPlan();


    // CNPJ não é mais PK, mas continua sendo único no negócio.
    boolean existsByCnpj(String cnpj);
    long countBySubscriptionPlan(SubscriptionPlan subscriptionPlan);
    boolean existsBySubscriptionPlan(SubscriptionPlan subscriptionPlan);
    Optional<Company> findByCompanyId(int companyId);
    List<Company> findByOfficialName(String officialName);
    List<Company> findByTradeName(String tradeName);
}
