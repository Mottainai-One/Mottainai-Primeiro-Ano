package com.mottainai.mottainaicrud.dao;

import com.mottainai.crudinicial.model.Company;
import com.mottainai.crudinicial.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanDao extends JpaRepository<SubscriptionPlan, Integer> {
    boolean existsByCompanyId(Company companyId); // 1 plano por empresa (unSubscriptionPlanCompanyId)
}