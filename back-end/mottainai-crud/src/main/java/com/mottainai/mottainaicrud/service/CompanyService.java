package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.SubscriptionPlanDao;
import com.mottainai.mottainaicrud.dto.CompanyRequestDTO;
import com.mottainai.mottainaicrud.dto.CompanyResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CompanyService {

    // ACESSO AOS DADOS DE EMPRESAS
    private final CompanyDao companyDao;

    // ACESSO AOS DADOS DE PLANOS
    private final SubscriptionPlanDao subscriptionPlanDao;

    public CompanyService(
            CompanyDao companyDao,
            SubscriptionPlanDao subscriptionPlanDao
    ) {
        this.companyDao = companyDao;
        this.subscriptionPlanDao = subscriptionPlanDao;
    }

    // LISTA TODAS AS EMPRESAS
    public List<CompanyResponseDTO> findAll() {
        return companyDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA EMPRESAS PELO NOME FANTASIA
    public List<CompanyResponseDTO> findByTradeName(String tradeName) {
        return companyDao.findByTradeName(tradeName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA EMPRESAS PELA RAZAO SOCIAL
    public List<CompanyResponseDTO> findByOfficialName(String officialName) {
        return companyDao.findByOfficialName(officialName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA EMPRESA PELO ID
    public CompanyResponseDTO findByCompanyId(int companyId) {
        Company company = companyDao.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa com id " + companyId + " não encontrada."
                ));

        return toResponseDTO(company);
    }

    // CONVERTE DTO PARA ENTIDADE
    private Company toEntity(CompanyRequestDTO dto) {
        Company company = new Company();
        company.setCnpj(dto.getCnpj());
        company.setOfficialName(dto.getOfficialName());
        company.setTradeName(dto.getTradeName());
        company.setEmail(dto.getEmail());
        company.setPhone(dto.getPhone());
        company.setLatitude(dto.getLatitude());
        company.setLongitude(dto.getLongitude());
        return company;
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCnpj(company.getCnpj());
        dto.setOfficialName(company.getOfficialName());
        dto.setTradeName(company.getTradeName());
        dto.setEmail(company.getEmail());
        dto.setPhone(company.getPhone());
        dto.setLatitude(company.getLatitude());
        dto.setLongitude(company.getLongitude());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setSubscriptionPlanId(company.getSubscriptionPlan().getPlanId());
        return dto;
    }

    // CADASTRA UMA NOVA EMPRESA
    public CompanyResponseDTO create(CompanyRequestDTO dto) {

        // VALIDA CNPJ UNICO
        if (companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma empresa cadastrada com esse CNPJ."
            );
        }

        // BUSCA O PLANO INFORMADO
        SubscriptionPlan plan = subscriptionPlanDao
                .findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano de assinatura não encontrado."
                ));

        // MONTA A EMPRESA PARA SALVAR
        Company company = toEntity(dto);
        company.setSubscriptionPlan(plan);

        // SALVA E RETORNA A RESPOSTA
        Company saved = companyDao.save(company);
        return toResponseDTO(saved);
    }

    // ATUALIZA UMA EMPRESA PELO ID
    public CompanyResponseDTO update(int companyId, CompanyRequestDTO dto) {

        // BUSCA O PLANO INFORMADO
        SubscriptionPlan plan = subscriptionPlanDao
                .findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Plano de assinatura não encontrado."
                ));

        // BUSCA A EMPRESA EXISTENTE
        Company existing = companyDao.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa com id " + companyId + " não encontrada."
                ));

        // VALIDA CNPJ AO ALTERAR
        if (!existing.getCnpj().equals(dto.getCnpj())
                && companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma empresa cadastrada com esse CNPJ."
            );
        }

        // ATUALIZA OS DADOS DA EMPRESA
        existing.setCnpj(dto.getCnpj());
        existing.setOfficialName(dto.getOfficialName());
        existing.setTradeName(dto.getTradeName());
        existing.setEmail(dto.getEmail());
        existing.setPhone(dto.getPhone());
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setSubscriptionPlan(plan);

        // SALVA AS ALTERACOES
        return toResponseDTO(companyDao.save(existing));
    }

    // REMOVE EMPRESA PELO ID
    public void delete(int companyId) {

        // VALIDA EMPRESA EXISTENTE
        if (!companyDao.existsById(companyId)) {
            throw new ResourceNotFoundException(
                    "Empresa com id " + companyId + " não encontrada."
            );
        }

        companyDao.deleteById(companyId);
    }
}