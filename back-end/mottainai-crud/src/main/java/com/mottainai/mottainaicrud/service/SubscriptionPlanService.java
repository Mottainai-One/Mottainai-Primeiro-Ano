package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.SubscriptionPlanDao;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.math.BigDecimal;

import java.util.List;

// REGRAS DO CADASTRO DE PLANOS
// Verifica nomes e empresas vinculadas; preserva o nome histórico e anonimiza os demais dados no soft delete.

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanDao subscriptionPlanDao;
    private final CompanyDao companyDao;
    private final DataAnonymizationService anonymizationService;

    public SubscriptionPlanService(SubscriptionPlanDao subscriptionPlanDao, CompanyDao companyDao, DataAnonymizationService anonymizationService) {
        this.subscriptionPlanDao = subscriptionPlanDao;
        this.companyDao = companyDao;
        // Serviço usado quando o plano puder ser excluído logicamente.
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO DO PLANO
    // Nome duplicado é conflito; preço e limites já validados pelo DTO são mantidos no cadastro.
    public SubscriptionPlanResponseDTO create(SubscriptionPlanRequestDTO dto) {

        if (subscriptionPlanDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Já existe um plano com esse nome."
            );
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

    // LISTAGEM COM CONTAGEM DE EMPRESAS EM LOTE
    // Uma agregação por plano substitui contagens individuais e mantém os critérios dos vínculos existentes.
    public List<SubscriptionPlanResponseDTO> findAll() {
        var counts = companyDao.countGroupedBySubscriptionPlan().stream().collect(java.util.stream.Collectors.toMap(
                com.mottainai.mottainaicrud.dao.GroupedCount::getId,
                com.mottainai.mottainaicrud.dao.GroupedCount::getTotal));
        return subscriptionPlanDao.findAll().stream()
                .map(plan -> toResponseDTO(plan, counts.getOrDefault(plan.getPlanId(), 0L))).toList();
    }

    // CONSULTA DO PLANO
    // O detalhe inclui a contagem individual de empresas e as datas de controle no DTO.
    public SubscriptionPlanResponseDTO findById(int planId) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado."));
        return toResponseDTO(plan);
    }

    // EDIÇÃO DO PLANO EXISTENTE
    // Não permite editar soft deleted e verifica nome duplicado quando ele muda.
    public SubscriptionPlanResponseDTO update(int planId, SubscriptionPlanRequestDTO dto) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plano não encontrado."));

        // Um plano excluído logicamente não pode ser editado novamente.
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar um plano excluído."
            );
        }

        if (!plan.getName().equals(dto.getName())
                && subscriptionPlanDao.existsByName(dto.getName())) {

            throw new DuplicateResourceException(
                    "Já existe um plano com esse nome."
            );
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
    // EXCLUSÃO LÓGICA SEM ROMPER EMPRESAS
    // Planos vinculados são bloqueados; o nome permanece histórico e os demais valores são substituídos respeitando constraints.
    public void delete(Integer planId) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(planId).orElseThrow(() -> new ResourceNotFoundException(
                                "Plano de assinatura não encontrado."
                        )
                );

        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Este plano já foi excluído."
            );
        }

        // Mantém a regra antiga:
        // um plano ainda utilizado por empresas não pode
        // ser excluído diretamente.
        if (companyDao.existsBySubscriptionPlan(plan)) {
            throw new DuplicateResourceException(
                    "Não é possível excluir um plano associado a empresas."
            );
        }
        // Mantém o nome como referência histórica.
        // A descrição perde o conteúdo original.
        plan.setDescription(anonymizationService.anonymize());
        // Esses valores deixam de ter função operacional,
        // mas continuam respeitando as constraints do banco.
        plan.setPrice(new BigDecimal("0.01"));
        plan.setStoreLimit(1);
        plan.setUserLimit(1);
        // Exclusão lógica: o registro permanece no banco.
        LocalDateTime now = LocalDateTime.now();

        plan.setActive(false);
        plan.setDeletedAt(now);
        plan.setUpdatedAt(now);

        subscriptionPlanDao.save(plan);
    }

    // CONVERSÃO DO DETALHE
    // Obtém uma contagem individual de empresas; a listagem fornece a contagem pronta para evitar consultas por item.
    private SubscriptionPlanResponseDTO toResponseDTO(
            SubscriptionPlan plan) {
        return toResponseDTO(plan, companyDao.countBySubscriptionPlan(plan));
    }

    // DTO COM AGREGAÇÃO JÁ CALCULADA
    // A contagem recebida completa os dados do plano sem carregar todas as empresas vinculadas.
    private SubscriptionPlanResponseDTO toResponseDTO(SubscriptionPlan plan, long companyCount) {

        SubscriptionPlanResponseDTO dto =
                new SubscriptionPlanResponseDTO();

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

        dto.setCompanyCount(
                companyCount
        );

        return dto;
    }
}
