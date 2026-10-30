package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// REPOSITÓRIO JPA DE PLANOS
// Verifica duplicidade de nome; as regras e a exclusão lógica permanecem no SubscriptionPlanService.

@Repository
public interface SubscriptionPlanDao extends JpaRepository<SubscriptionPlan, Integer> {
    boolean existsByName(String name);
}