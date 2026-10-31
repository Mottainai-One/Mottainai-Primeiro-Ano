package com.mottainai.crudinicial.service;

import com.mottainai.crudinicial.dao.CompanyDao;
import com.mottainai.crudinicial.dao.StoreDao;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import com.mottainai.crudinicial.dao.SubscriptionPlanDao;
import com.mottainai.crudinicial.dto.CompanyRequestDTO;
import com.mottainai.crudinicial.dto.CompanyResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import com.mottainai.crudinicial.model.Company;
import com.mottainai.crudinicial.model.SubscriptionPlan;
import com.mottainai.crudinicial.security.DataAnonymizationService;
import com.mottainai.crudinicial.security.DataEncryptionService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_COMPANY_CNPJ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;

// REGRAS DO CADASTRO DE EMPRESAS
// Valida o plano, protege contatos com AES e inicia a cascata lógica de lojas, funcionários e contas.

@Service
public class CompanyService {

    private final CompanyDao companyDao;
    private final SubscriptionPlanDao subscriptionPlanDao;
    private final StoreDao storeDao;
    private final StoreService storeService;
    private final DataEncryptionService encryptionService;
    private final DataAnonymizationService anonymizationService;

    public CompanyService(CompanyDao companyDao, SubscriptionPlanDao subscriptionPlanDao, StoreDao storeDao, StoreService storeService,DataEncryptionService encryptionService, DataAnonymizationService anonymizationService) {
        this.companyDao = companyDao;
        this.subscriptionPlanDao = subscriptionPlanDao;
        this.storeDao = storeDao;
        this.storeService = storeService;
        this.encryptionService = encryptionService;
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO COM PLANO E CONTATOS PROTEGIDOS
    // Exige CNPJ não duplicado e plano não excluído; email e telefone informado são cifrados antes da persistência.
    public CompanyResponseDTO create(CompanyRequestDTO dto) {
        if (companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Já existe uma empresa cadastrada com esse CNPJ.");
        }

        SubscriptionPlan plan = subscriptionPlanDao.findById(dto.getSubscriptionPlanId()).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plano de assinatura não encontrado."
                        ));

        // Planos excluídos não podem ser utilizados
        // em novos vínculos ou atualizações.
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um plano de assinatura excluído."
            );
        }

        Company company = toEntity(dto);
        company.setSubscriptionPlan(plan);

        // Define a situação escolhida no formulário.
        company.setActive(dto.getActive());
        // Criptografa os dados sensíveis antes de salvar.
        company.setEmail(
                encryptionService.encrypt(dto.getEmail())
        );
        company.setPhone(
                dto.getPhone() == null || dto.getPhone().isBlank()
                        ? null
                        : encryptionService.encrypt(dto.getPhone())
        );

        Company saved = companyDao.save(company);
        return toResponseDTO(saved);
    }

    // LISTAGEM ADMINISTRATIVA
    // Retira o ID técnico e prepara contatos/relação com plano por DTO; o histórico de soft delete continua consultável.
    public List<CompanyResponseDTO> findAll() {
        return companyDao.findAll()
                .stream()
                .filter(company ->
                        company.getCompanyId() != TECHNICAL_RECORD_ID
                )
                .map(this::toResponseDTO)
                .toList();
    }

    // CONSULTA POR NOME FANTASIA
    // Usa a igualdade derivada do Repository e prepara os mesmos campos de saída das demais consultas.
    public List<CompanyResponseDTO> findByTradeName(String tradeName) {
        return companyDao.findByTradeName(tradeName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // CONSULTA POR RAZÃO SOCIAL
    // Mantém a busca específica do cadastro, sem reinterpretar o texto como CNPJ ou ID.
    public List<CompanyResponseDTO> findByOfficialName(String officialName) {
        return companyDao.findByOfficialName(officialName)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Agora busca pelo companyId real (PK), não mais pelo CNPJ
    public CompanyResponseDTO findByCompanyId(int companyId) {
        Company company = companyDao.findByCompanyId(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa com id " + companyId + " não encontrada."));
        return toResponseDTO(company);
    }

    // EDIÇÃO DA EMPRESA
    // Protege empresa técnica/excluída, resolve o plano e confere duplicidade somente quando o CNPJ muda.
    public CompanyResponseDTO update(int companyId, CompanyRequestDTO dto) {

        SubscriptionPlan plan =
                subscriptionPlanDao.findById(
                        dto.getSubscriptionPlanId()
                ).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Plano de assinatura não encontrado."
                        ));

        // Planos excluídos não podem ser utilizados
        // em novos vínculos ou atualizações.
        if (plan.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um plano de assinatura excluído."
            );
        }

        Company existing = companyDao.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa com id " + companyId + " não encontrada."));

        // A empresa técnica existe apenas para sustentar
        // os registros usados no processo de anonimização.
        if (TECHNICAL_COMPANY_CNPJ.equals(
                existing.getCnpj()
        )) {
            throw new BusinessRuleException(
                    "A empresa técnica de anonimização não pode ser editada."
            );
        }

        // Uma empresa excluída logicamente não pode
        // voltar a ser alterada pelo CRUD.
        if (existing.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar uma empresa excluída."
            );
        }

        if (!existing.getCnpj().equals(dto.getCnpj()) && companyDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Já existe uma empresa cadastrada com esse CNPJ.");
        }

        existing.setCnpj(dto.getCnpj());
        existing.setOfficialName(dto.getOfficialName());
        existing.setTradeName(dto.getTradeName());
        // Sempre gera uma nova criptografia para o e-mail atualizado.
        existing.setEmail(encryptionService.encrypt(dto.getEmail()));
        // Mantém null caso não exista telefone.
        // Caso exista, salva novamente usando AES.
        existing.setPhone(dto.getPhone() == null || dto.getPhone().isBlank()
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
    // CASCATA DE EXCLUSÃO LÓGICA
    // Na mesma transação, delega às lojas ainda não excluídas antes de anonimizar a empresa; o nome oficial é preservado.
    public void delete(Integer companyId) {
        Company company = companyDao.findById(companyId).orElseThrow(() ->
                        new ResourceNotFoundException("Empresa não encontrada.")
                );

        // A empresa técnica não pode ser excluída,
        // pois a loja técnica depende dela.
        if (TECHNICAL_COMPANY_CNPJ.equals(
                company.getCnpj()
        )) {
            throw new BusinessRuleException(
                    "A empresa técnica de anonimização não pode ser excluída."
            );
        }

        // Uma empresa já excluída não deve passar
        // novamente pelo processo de anonimização.
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Esta empresa já foi excluída."
            );
        }

        // Antes de anonimizar a empresa, exclui logicamente
        // todas as lojas que ainda pertencem a ela.

        // Cada StoreService.delete() também cuida dos funcionários,
        // contas de usuário e endereço relacionados à loja.
        storeDao.findByCompany_CompanyId(companyId)
                .stream()
                .filter(store -> store.getDeletedAt() == null)
                .forEach(store ->
                        storeService.delete(store.getStoreId())
                );
        // REFERÊNCIA HISTÓRICA
        // O nome oficial permanece como já estava armazenado; email/telefone são substituídos no bloco abaixo.
        company.setTradeName(null);
        // Ele já permanece no formato original armazenado atualmente.
        // Os demais dados identificáveis serão removidos de forma irreversível.

        // Substitui o CNPJ por um identificador numérico anônimo
        // que continua respeitando a constraint de 14 dígitos e unique.
        company.setCnpj(
                anonymizationService.anonymizeNumericId(
                        company.getCompanyId(),
                        "99",
                        14
                )
        );
        // E-mail e telefone deixam de conter qualquer informação recuperável.
        company.setEmail(anonymizationService.anonymize());
        if (company.getPhone() != null) {
            company.setPhone(anonymizationService.anonymize());
        }
        // Coordenadas também deixam de representar a localização original.
        company.setLatitude(null);
        company.setLongitude(null);
        // Marca como excluída sem remover fisicamente a linha.
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

    // CONVERSÃO E PROTEÇÃO DO HISTÓRICO
    // Acrescenta o plano e só descriptografa contatos ainda AES; os campos substituídos no soft delete viram marcadores legíveis.
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

        // Envia também os dados do plano para o JSP
        // sem expor diretamente a entidade JPA.
        dto.setSubscriptionPlanId(
                company.getSubscriptionPlan().getPlanId()
        );

        dto.setSubscriptionPlanName(
                company.getSubscriptionPlan().getName()
        );

        boolean deleted = company.getDeletedAt() != null;

        if (deleted || TECHNICAL_COMPANY_CNPJ.equals(company.getCnpj())) {

            dto.setEmail("Anonimizado");

            dto.setPhone(
                    company.getPhone() == null
                            ? null
                            : "Anonimizado"
            );

        } else {

            dto.setEmail(
                    encryptionService.decrypt(
                            company.getEmail()
                    )
            );

            dto.setPhone(
                    company.getPhone() == null
                            ? null
                            : encryptionService.decrypt(
                            company.getPhone()
                    )
            );
        }

        return dto;
    }
}