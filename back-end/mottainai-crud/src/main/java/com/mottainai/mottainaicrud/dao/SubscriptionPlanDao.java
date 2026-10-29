package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanDao extends JpaRepository<SubscriptionPlan, Integer> {
    boolean existsByName(String name); // BUSCA POR NOME
}