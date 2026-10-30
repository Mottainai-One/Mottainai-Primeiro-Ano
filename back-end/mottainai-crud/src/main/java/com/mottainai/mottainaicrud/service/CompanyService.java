package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dao.SubscriptionPlanDao;
import com.mottainai.mottainaicrud.dto.CompanyRequestDTO;
import com.mottainai.mottainaicrud.dto.CompanyResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.SubscriptionPlan;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_COMPANY_CNPJ;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE EMPRESAS
// AES NOS CONTATOS; CASCATA LOGICA COMPLETA: EMPRESA -> LOJAS -> FUNCIONARIOS -> CONTAS -> ENDERECOS

@Service
public class CompanyService {

    private final CompanyDao companyDao;
    private final SubscriptionPlanDao subscriptionPlanDao;
    private final StoreDao storeDao;
    private final StoreService storeService;
    // CRIPTOGRAFIA REVERSIVEL DOS CONTATOS DA EMPRESA
    private final DataEncryptionService encryptionService;
    // ANONIMIZACAO IRREVERSIVEL USADA NO SOFT DELETE DA EMPRESA
    private final DataAnonymizationService anonymizationService;

    public CompanyService(CompanyDao companyDao,
                          SubscriptionPlanDao subscriptionPlanDao,
                          StoreDao storeDao,
                          StoreService storeService,
                          DataEncryptionService encryptionService,
                          DataAnonymizationService anonymizationService) {
        this.companyDao = companyDao;
        this.subscriptionPlanDao = subscriptionPlanDao;
        this.storeDao = storeDao;
        this.storeService = storeService;
        this.encryptionService = encryptionService;
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO — CNPJ UNICO, PLANO VALIDO; EMAIL E TELEFONE CRIPTOGRAFADOS COM AES
    public CompanyResponseDTO create(CompanyRequestDTO dto) {
        if (companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Ja existe uma empresa cadastrada com esse CNPJ.");
        }

        SubscriptionPlan plan = subscriptionPlanDao.findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano de assinatura nao encontrado."));

        // PLANO EXCLUIDO NAO PODE SER UTILIZADO
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um plano de assinatura excluido.");
        }

        Company company = toEntity(dto);
        company.setSubscriptionPlan(plan);
        company.setActive(dto.getActive());
        // EMAIL E TELEFONE CRIPTOGRAFADOS ANTES DE SALVAR
        company.setEmail(encryptionService.encrypt(dto.getEmail()));
        company.setPhone(
                dto.getPhone() == null || dto.getPhone().isBlank()
                        ? null
                        : encryptionService.encrypt(dto.getPhone())
        );

        Company saved = companyDao.save(company);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM O REGISTRO TECNICO — HISTORICO DE SOFT DELETE DISPONIVEL
    public List<CompanyResponseDTO> findAll() {
        return companyDao.findAll()
                .stream()
                .filter(company -> company.getCompanyId() != TECHNICAL_RECORD_ID)
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA POR NOME FANTASIA
    public List<CompanyResponseDTO> findByTradeName(String tradeName) {
        return companyDao.findByTradeName(tradeName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA POR RAZAO SOCIAL
    public List<CompanyResponseDTO> findByOfficialName(String officialName) {
        return companyDao.findByOfficialName(officialName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // CONSULTA PELO ID (PK)
    public CompanyResponseDTO findByCompanyId(int companyId) {
        Company company = companyDao.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa com id " + companyId + " nao encontrada."));
        return toResponseDTO(company);
    }

    // EDICAO — PROTEGE EMPRESA TECNICA E EXCLUIDAS; VERIFICA CNPJ E PLANO
    public CompanyResponseDTO update(int companyId, CompanyRequestDTO dto) {
        SubscriptionPlan plan = subscriptionPlanDao.findById(dto.getSubscriptionPlanId())
                .orElseThrow(() -> new ResourceNotFoundException("Plano de assinatura nao encontrado."));

        // PLANO EXCLUIDO NAO PODE SER REUTILIZADO
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um plano de assinatura excluido.");
        }

        Company existing = companyDao.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa com id " + companyId + " nao encontrada."));

        // EMPRESA TECNICA NAO PODE SER EDITADA
        if (TECHNICAL_COMPANY_CNPJ.equals(existing.getCnpj())) {
            throw new BusinessRuleException("A empresa tecnica de anonimizacao nao pode ser editada.");
        }

        // EMPRESA COM SOFT DELETE NAO PODE SER EDITADA
        if (existing.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar uma empresa excluida.");
        }

        if (!existing.getCnpj().equals(dto.getCnpj()) && companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Ja existe uma empresa cadastrada com esse CNPJ.");
        }

        existing.setCnpj(dto.getCnpj());
        existing.setOfficialName(dto.getOfficialName());
        existing.setTradeName(dto.getTradeName());
        // NOVA CRIPTOGRAFIA AES PARA EMAIL E TELEFONE ATUALIZADOS
        existing.setEmail(encryptionService.encrypt(dto.getEmail()));
        existing.setPhone(
                dto.getPhone() == null || dto.getPhone().isBlank()
                        ? null
                        : encryptionService.encrypt(dto.getPhone())
        );
        existing.setLatitude(dto.getLatitude());
        existing.setLongitude(dto.getLongitude());
        existing.setActive(dto.getActive());
        existing.setSubscriptionPlan(plan);

        Company updated = companyDao.save(existing);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE COM CASCATA COMPLETA — LOJAS -> FUNCIONARIOS -> CONTAS -> ENDERECOS
    public void delete(Integer companyId) {
        Company company = companyDao.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada."));

        // EMPRESA TECNICA NAO PODE SER EXCLUIDA — LOJA TECNICA DEPENDE DELA
        if (TECHNICAL_COMPANY_CNPJ.equals(company.getCnpj())) {
            throw new BusinessRuleException("A empresa tecnica de anonimizacao nao pode ser excluida.");
        }

        // IMPEDE DUPLA EXCLUSAO
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException("Esta empresa ja foi excluida.");
        }

        // CASCATA: LOJAS AINDA ATIVAS SAO EXCLUIDAS LOGICAMENTE ANTES DA EMPRESA
        // StoreService.delete() CUIDA DOS FUNCIONARIOS, CONTAS E ENDERECOS DE CADA LOJA
        storeDao.findByCompany_CompanyId(companyId)
                .stream()
                .filter(store -> store.getDeletedAt() == null)
                .forEach(store -> storeService.delete(store.getStoreId()));

        // NOME FANTASIA REMOVIDO; RAZAO SOCIAL PERMANECE COMO REFERENCIA HISTORICA
        company.setTradeName(null);
        // CNPJ SUBSTITUIDO POR IDENTIFICADOR ANONIMO DE 14 DIGITOS
        company.setCnpj(anonymizationService.anonymizeNumericId(company.getCompanyId(), "99", 14));
        company.setEmail(anonymizationService.anonymize());
        if (company.getPhone() != null) {
            company.setPhone(anonymizationService.anonymize());
        }
        company.setLatitude(null);
        company.setLongitude(null);
        company.setActive(false);
        company.setDeletedAt(LocalDateTime.now());
        companyDao.save(company);
    }

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

    // DTO — CONTATOS DESCRIPTOGRAFADOS SOMENTE SE NAO ANONIMIZADOS
    private CompanyResponseDTO toResponseDTO(Company company) {
        CompanyResponseDTO dto = new CompanyResponseDTO();
        dto.setCompanyId(company.getCompanyId());
        dto.setCnpj(company.getCnpj());
        dto.setOfficialName(company.getOfficialName());
        dto.setTradeName(company.getTradeName());
        dto.setLatitude(company.getLatitude());
        dto.setLongitude(company.getLongitude());
        dto.setActive(company.isActive());
        dto.setCreatedAt(company.getCreatedAt());
        dto.setUpdatedAt(company.getUpdatedAt());
        dto.setDeletedAt(company.getDeletedAt());
        dto.setSubscriptionPlanId(company.getSubscriptionPlan().getPlanId());
        dto.setSubscriptionPlanName(company.getSubscriptionPlan().getName());

        boolean deleted = company.getDeletedAt() != null
                || TECHNICAL_COMPANY_CNPJ.equals(company.getCnpj());

        if (deleted) {
            // CAMPOS ANONIMIZADOS NAO SAO DESCRIPTOGRAFADOS
            dto.setEmail("Anonimizado");
            dto.setPhone(company.getPhone() == null ? null : "Anonimizado");
        } else {
            dto.setEmail(encryptionService.decrypt(company.getEmail()));
            dto.setPhone(company.getPhone() == null
                    ? null
                    : encryptionService.decrypt(company.getPhone()));
        }
        return dto;
    }
}
