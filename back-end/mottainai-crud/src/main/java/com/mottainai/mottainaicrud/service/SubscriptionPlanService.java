package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.SubscriptionPlanDao;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import org.springframework.stereotype.Service;
import com.mottainai.mottainaicrud.dao.CompanyDao;

import java.util.List;

@Service
public class SubscriptionPlanService {

    // ACESSO AOS DADOS DE PLANOS
    private final SubscriptionPlanDao subscriptionPlanDao;

    // ACESSO AOS DADOS DE EMPRESAS
    private final CompanyDao companyDao;

    public SubscriptionPlanService(
            SubscriptionPlanDao subscriptionPlanDao,
            CompanyDao companyDao
    ) {
        this.subscriptionPlanDao = subscriptionPlanDao;
        this.companyDao = companyDao;
    }

    // LISTA TODOS OS PLANOS
    public List<SubscriptionPlanResponseDTO> findAll() {
        return subscriptionPlanDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA PLANO PELO ID
    public SubscriptionPlanResponseDTO findById(int planId) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano não encontrado."
                ));

        return toResponseDTO(plan);
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private SubscriptionPlanResponseDTO toResponseDTO(
            SubscriptionPlan plan
    ) {
        SubscriptionPlanResponseDTO dto = new SubscriptionPlanResponseDTO();
        dto.setPlanId(plan.getPlanId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setStoreLimit(plan.getStoreLimit());
        dto.setUserLimit(plan.getUserLimit());
        return dto;
    }
}