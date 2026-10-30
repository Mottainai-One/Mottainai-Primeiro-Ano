package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dao.UserAccountDao;
import com.mottainai.mottainaicrud.dto.EmployeeRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.EmployeeRole;
import com.mottainai.mottainaicrud.model.Store;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS E DADOS PESSOAIS DO FUNCIONARIO
// AES NOS DADOS PESSOAIS; CASCATA DE ANONIMIZACAO PARA A CONTA VINCULADA

@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;
    private final StoreDao storeDao;
    private final EmployeeRoleDao employeeRoleDao;
    // DAO USADO PARA LOCALIZAR E ANONIMIZAR A CONTA DO FUNCIONARIO NA EXCLUSAO
    private final UserAccountDao userAccountDao;
    // SERVICO DE CRIPTOGRAFIA REVERSIVEL DOS DADOS PESSOAIS
    private final DataEncryptionService encryptionService;
    // SERVICO DE ANONIMIZACAO IRREVERSIVEL USADO NO SOFT DELETE
    private final DataAnonymizationService anonymizationService;

    public EmployeeService(EmployeeDao employeeDao,
                           StoreDao storeDao,
                           EmployeeRoleDao employeeRoleDao,
                           DataEncryptionService encryptionService,
                           DataAnonymizationService anonymizationService,
                           UserAccountDao userAccountDao) {
        this.employeeDao = employeeDao;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;
        this.userAccountDao = userAccountDao;
        this.encryptionService = encryptionService;
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO — LOJA E CARGO VALIDOS; CPF UNICO; DADOS PESSOAIS CRIPTOGRAFADOS COM AES
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {

        // CPF DUPLICADO NAO PODE SER CADASTRADO NOVAMENTE
        // NOTA: REQUER existsByCpf(String) NO EmployeeDao
        if (employeeDao.existsByCpf(encryptionService.encrypt(dto.getCpf()))) {
            throw new DuplicateResourceException("CPF ja cadastrado.");
        }

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada."));

        // LOJA EXCLUIDA NAO PODE RECEBER NOVOS FUNCIONARIOS
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel cadastrar um funcionario em uma loja excluida.");
        }

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo nao encontrado."));

        // CARGO EXCLUIDO NAO PODE SER UTILIZADO EM NOVO VINCULO
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um cargo excluido.");
        }

        Employee employee = new Employee();
        employee.setActive(dto.getActive());
        // DADOS PESSOAIS ENTRAM NO BANCO SOMENTE APOS AES
        employee.setCpf(encryptionService.encrypt(dto.getCpf()));
        employee.setName(encryptionService.encrypt(dto.getName()));
        employee.setEmail(encryptionService.encrypt(dto.getEmail()));
        employee.setPhone(encryptionService.encrypt(dto.getPhone()));
        employee.setHireDate(dto.getHireDate());
        employee.setStore(store);
        employee.setRole(role);

        Employee saved = employeeDao.save(employee);
        return toResponseDTO(saved);
    }

    // LISTAGEM COM DADOS DESCRIPTOGRAFADOS OU MARCADOS COMO ANONIMIZADOS
    public List<EmployeeResponseDTO> findAll() {
        return employeeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // CONSULTA PELO ID — 404 SE AUSENTE
    public EmployeeResponseDTO findById(Integer employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado."));
        return toResponseDTO(employee);
    }

    // EDICAO — CPF UNICO EXCLUINDO O PROPRIO REGISTRO; BLOQUEIA EXCLUIDOS E VINCULOS INVALIDOS
    public EmployeeResponseDTO update(Integer employeeId, EmployeeRequestDTO dto) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado."));

        // FUNCIONARIO COM SOFT DELETE NAO PODE SER EDITADO
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar um funcionario excluido.");
        }

        // CPF JA USADO POR OUTRO FUNCIONARIO NAO PODE SER ATRIBUIDO
        // NOTA: REQUER existsByCpfAndEmployeeIdNot(String, int) NO EmployeeDao
        if (employeeDao.existsByCpfAndEmployeeIdNot(
                encryptionService.encrypt(dto.getCpf()), employeeId)) {
            throw new DuplicateResourceException("CPF ja cadastrado para outro funcionario.");
        }

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada."));

        // NAO PERMITE MOVER O FUNCIONARIO PARA UMA LOJA EXCLUIDA
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel vincular o funcionario a uma loja excluida.");
        }

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo nao encontrado."));

        // NAO PERMITE ATRIBUIR UM CARGO JA EXCLUIDO
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um cargo excluido.");
        }

        // DADOS ATUALIZADOS RECEBEM NOVA CRIPTOGRAFIA AES ANTES DE SALVAR
        employee.setCpf(encryptionService.encrypt(dto.getCpf()));
        employee.setName(encryptionService.encrypt(dto.getName()));
        employee.setEmail(encryptionService.encrypt(dto.getEmail()));
        employee.setPhone(encryptionService.encrypt(dto.getPhone()));
        employee.setHireDate(dto.getHireDate());
        employee.setStore(store);
        employee.setRole(role);
        employee.setActive(dto.getActive());

        Employee updated = employeeDao.save(employee);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE COM ANONIMIZACAO — CASCATA PARA A CONTA VINCULADA; LOJA E CARGO PASSAM A REGISTROS TECNICOS
    public void delete(Integer employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado."));

        // IMPEDE DUPLA EXCLUSAO
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException("Este funcionario ja foi excluido.");
        }

        // DADOS PESSOAIS SUBSTITUIDOS POR VALORES ALEATORIOS IRREVERSIVEIS
        employee.setCpf(anonymizationService.anonymize());
        employee.setEmail(anonymizationService.anonymize());
        employee.setPhone(anonymizationService.anonymize());
        // NOME CONTINUA CRIPTOGRAFADO COM AES PARA IDENTIFICACAO HISTORICA
        employee.setHireDate(anonymizationService.anonymizeDate());

        employee.setActive(false);
        employee.setDeletedAt(LocalDateTime.now());

        // CARGO REAL SUBSTITUIDO PELO CARGO TECNICO PARA PRESERVAR A FK
        EmployeeRole anonymizedRole = employeeRoleDao.findByName(TECHNICAL_ROLE_NAME)
                .orElseThrow(() -> new IllegalStateException("Cargo de anonimizacao nao encontrado."));
        employee.setRole(anonymizedRole);

        // LOJA REAL SUBSTITUIDA PELA LOJA TECNICA PARA PRESERVAR A FK
        Store anonymizedStore = storeDao.findByCnpj(TECHNICAL_STORE_CNPJ)
                .orElseThrow(() -> new IllegalStateException("Loja de anonimizacao nao encontrada."));
        employee.setStore(anonymizedStore);

        // CASCATA: CONTA VINCULADA TAMBEM E ANONIMIZADA SE AINDA NAO EXCLUIDA
        userAccountDao.findByEmployee(employee)
                .filter(userAccount -> userAccount.getDeletedAt() == null)
                .ifPresent(userAccount -> {
                    userAccount.setEmail(anonymizationService.anonymize());
                    // HASH ANTIGO DESCARTADO — HASHES SAO IRREVERSIVEIS, NAO PRECISAM SER DESCRIPTOGRAFADOS
                    userAccount.setPasswordHash(anonymizationService.anonymize());
                    userAccount.setLastLogin(null);
                    userAccount.setActive(false);
                    userAccount.setDeletedAt(LocalDateTime.now());
                    userAccountDao.save(userAccount);
                });

        employeeDao.save(employee);
    }

    // SAIDA DOS DADOS PESSOAIS — CPF/EMAIL/TELEFONE MARCADOS COMO ANONIMIZADOS APOS SOFT DELETE
    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        // NOME CONTINUA AES MESMO APOS EXCLUSAO — UNICO DADO PESSOAL MANTIDO
        dto.setName(encryptionService.decrypt(employee.getName()));

        if (employee.getDeletedAt() != null) {
            // VALORES ALEATORIOS NAO SAO EXPOSTOS — INTERFACE RECEBE APENAS O MARCADOR
            dto.setCpf("Anonimizado");
            dto.setEmail("Anonimizado");
            dto.setPhone("Anonimizado");
            dto.setHireDate(null);
        } else {
            dto.setCpf(encryptionService.decrypt(employee.getCpf()));
            dto.setEmail(encryptionService.decrypt(employee.getEmail()));
            dto.setPhone(encryptionService.decrypt(employee.getPhone()));
            dto.setHireDate(employee.getHireDate());
        }

        dto.setStoreId(employee.getStore().getStoreId());
        dto.setStoreName(employee.getStore().getName());
        dto.setRoleId(employee.getRole().getRoleId());
        dto.setRoleName(employee.getRole().getName());
        dto.setActive(employee.isActive());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        dto.setDeletedAt(employee.getDeletedAt());
        return dto;
    }
}
