package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.SubscriptionPlanDao;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE PLANOS
// VERIFICA NOME E EMPRESAS VINCULADAS; SOFT DELETE PRESERVA O NOME HISTORICO

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanDao subscriptionPlanDao;
    private final CompanyDao companyDao;
    private final DataAnonymizationService anonymizationService;

    public SubscriptionPlanService(SubscriptionPlanDao subscriptionPlanDao,
                                   CompanyDao companyDao,
                                   DataAnonymizationService anonymizationService) {
        this.subscriptionPlanDao = subscriptionPlanDao;
        this.companyDao = companyDao;
        // SERVICO DE ANONIMIZACAO USADO NO SOFT DELETE DO PLANO
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO — NOME DUPLICADO GERA CONFLITO
    public SubscriptionPlanResponseDTO create(SubscriptionPlanRequestDTO dto) {
        if (subscriptionPlanDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ja existe um plano com esse nome.");
        }

        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setStoreLimit(dto.getStoreLimit());
        plan.setUserLimit(dto.getUserLimit());
        plan.setActive(dto.getActive());

        SubscriptionPlan saved = subscriptionPlanDao.save(plan);
        return toResponseDTO(saved);
    }

    // LISTAGEM COM CONTAGEM DE EMPRESAS POR PLANO EM LOTE
    public List<SubscriptionPlanResponseDTO> findAll() {
        var counts = companyDao.countGroupedBySubscriptionPlan()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.mottainai.mottainaicrud.dao.GroupedCount::getId,
                        com.mottainai.mottainaicrud.dao.GroupedCount::getTotal));
        return subscriptionPlanDao.findAll()
                .stream()
                .map(plan -> toResponseDTO(plan, counts.getOrDefault(plan.getPlanId(), 0L)))
                .toList();
    }

    // DETALHE COM CONTAGEM INDIVIDUAL DE EMPRESAS
    public SubscriptionPlanResponseDTO findById(int planId) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado."));
        return toResponseDTO(plan);
    }

    // EDICAO — PLANO EXCLUIDO BLOQUEADO; NOME NOVO VERIFICADO CONTRA DUPLICIDADE
    public SubscriptionPlanResponseDTO update(int planId, SubscriptionPlanRequestDTO dto) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano nao encontrado."));

        // PLANO COM SOFT DELETE NAO PODE SER EDITADO
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar um plano excluido.");
        }

        if (!plan.getName().equals(dto.getName())
                && subscriptionPlanDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ja existe um plano com esse nome.");
        }

        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPrice(dto.getPrice());
        plan.setStoreLimit(dto.getStoreLimit());
        plan.setUserLimit(dto.getUserLimit());
        plan.setActive(dto.getActive());

        SubscriptionPlan updated = subscriptionPlanDao.save(plan);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE — PLANO EM USO BLOQUEADO; NOME PERMANECE COMO HISTORICO
    public void delete(Integer planId) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano de assinatura nao encontrado."));

        // IMPEDE DUPLA EXCLUSAO
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException("Este plano ja foi excluido.");
        }

        // PLANO VINCULADO A EMPRESAS NAO PODE SER EXCLUIDO
        if (companyDao.existsBySubscriptionPlan(plan)) {
            throw new DuplicateResourceException("Nao e possivel excluir um plano associado a empresas.");
        }

        // NOME PERMANECE; DESCRICAO E VALORES PERDEM SIGNIFICADO OPERACIONAL
        plan.setDescription(anonymizationService.anonymize());
        // VALORES MINIMOS QUE RESPEITAM AS CONSTRAINTS DO BANCO
        plan.setPrice(new BigDecimal("0.01"));
        plan.setStoreLimit(1);
        plan.setUserLimit(1);

        LocalDateTime now = LocalDateTime.now();
        plan.setActive(false);
        plan.setDeletedAt(now);
        plan.setUpdatedAt(now);

        subscriptionPlanDao.save(plan);
    }

    // DETALHE: CONTAGEM INDIVIDUAL DE EMPRESAS VINCULADAS
    private SubscriptionPlanResponseDTO toResponseDTO(SubscriptionPlan plan) {
        return toResponseDTO(plan, companyDao.countBySubscriptionPlan(plan));
    }

    // DTO COM CONTAGEM JA CALCULADA — EVITA CONSULTA ADICIONAL NA LISTAGEM
    private SubscriptionPlanResponseDTO toResponseDTO(SubscriptionPlan plan, long companyCount) {
        SubscriptionPlanResponseDTO dto = new SubscriptionPlanResponseDTO();
        dto.setPlanId(plan.getPlanId());
        dto.setName(plan.getName());
        dto.setDescription(plan.getDescription());
        dto.setPrice(plan.getPrice());
        dto.setStoreLimit(plan.getStoreLimit());
        dto.setUserLimit(plan.getUserLimit());
        dto.setActive(plan.isActive());
        dto.setCreatedAt(plan.getCreatedAt());
        dto.setUpdatedAt(plan.getUpdatedAt());
        dto.setDeletedAt(plan.getDeletedAt());
        dto.setCompanyCount(companyCount);
        return dto;
    }
}
