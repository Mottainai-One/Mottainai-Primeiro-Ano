package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.UserAccountDao;
import com.mottainai.mottainaicrud.dto.UserAccountRequestDTO;
import com.mottainai.mottainaicrud.dto.UserAccountResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.UserAccount;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import com.mottainai.mottainaicrud.security.PasswordHashService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DA CONTA DE FUNCIONARIO
// AES NO EMAIL, BCRYPT NA SENHA; EMAIL UNICO; SOFT DELETE ANONIMIZA CREDENCIAIS

@Service
public class UserAccountService {

    private final UserAccountDao userAccountDao;
    private final EmployeeDao employeeDao;
    // CRIPTOGRAFIA REVERSIVEL DO EMAIL ENQUANTO A CONTA ESTIVER VALIDA
    private final DataEncryptionService encryptionService;
    // ANONIMIZACAO IRREVERSIVEL USADA NO SOFT DELETE
    private final DataAnonymizationService anonymizationService;
    // HASH IRREVERSIVEL DA SENHA — SOMENTE O HASH E ARMAZENADO
    private final PasswordHashService passwordHashService;

    public UserAccountService(UserAccountDao userAccountDao,
                              EmployeeDao employeeDao,
                              DataEncryptionService encryptionService,
                              DataAnonymizationService anonymizationService,
                              PasswordHashService passwordHashService) {
        this.userAccountDao = userAccountDao;
        this.employeeDao = employeeDao;
        this.encryptionService = encryptionService;
        this.anonymizationService = anonymizationService;
        this.passwordHashService = passwordHashService;
    }

    // CADASTRO — SENHA MINIMA, FUNCIONARIO VALIDO, EMAIL E VINCULO UNICOS; BCRYPT NA SENHA
    public UserAccountResponseDTO create(UserAccountRequestDTO dto) {

        // SENHA OBRIGATORIA E COM MINIMO DE 8 CARACTERES
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatoria.");
        }
        if (dto.getPassword().length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres.");
        }

        // EMAIL JA CADASTRADO EM OUTRA CONTA NAO PODE SER REUTILIZADO
        // NOTA: REQUER existsByEmail(String) NO UserAccountDao
        if (userAccountDao.existsByEmail(encryptionService.encrypt(dto.getEmail()))) {
            throw new DuplicateResourceException("Ja existe uma conta com este e-mail.");
        }

        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado."));

        // FUNCIONARIO EXCLUIDO NAO PODE RECEBER NOVAS CONTAS
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel criar uma conta para um funcionario excluido.");
        }

        // FUNCIONARIO JA POSSUI UMA CONTA — VINCULO EXCLUSIVO
        if (userAccountDao.existsByEmployee(employee)) {
            throw new DuplicateResourceException("Esse funcionario ja possui uma conta de usuario.");
        }

        UserAccount account = new UserAccount();
        // EMAIL CRIPTOGRAFADO COM AES ANTES DE SALVAR NO BANCO
        account.setEmail(encryptionService.encrypt(dto.getEmail()));
        // APENAS O HASH BCRYPT E ARMAZENADO — NUNCA A SENHA EM TEXTO PURO
        account.setPasswordHash(passwordHashService.hash(dto.getPassword()));
        account.setEmployee(employee);
        account.setActive(dto.getActive());

        UserAccount saved = userAccountDao.save(account);
        return toResponseDTO(saved);
    }

    // LISTAGEM DE CONTAS ATIVAS E HISTORICAS
    public List<UserAccountResponseDTO> findAll() {
        return userAccountDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE SEM HASH — CREDENCIAIS NAO SAO EXPOSTAS NA RESPOSTA
    public UserAccountResponseDTO findById(int userId) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada."));
        return toResponseDTO(account);
    }

    // EDICAO — EMAIL UNICO EXCLUINDO A PROPRIA CONTA; SENHA NOVA RECEBE BCRYPT; EXCLUIDAS BLOQUEADAS
    public UserAccountResponseDTO update(int userId, UserAccountRequestDTO dto) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada."));

        // CONTA COM SOFT DELETE NAO PODE SER EDITADA
        if (account.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar uma conta excluida.");
        }

        // EMAIL JA USADO EM OUTRA CONTA NAO PODE SER ATRIBUIDO
        // NOTA: REQUER existsByEmailAndUserIdNot(String, int) NO UserAccountDao
        if (userAccountDao.existsByEmailAndUserIdNot(
                encryptionService.encrypt(dto.getEmail()), userId)) {
            throw new DuplicateResourceException("Ja existe uma conta com este e-mail.");
        }

        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionario nao encontrado."));

        // NAO PERMITE VINCULAR A CONTA A UM FUNCIONARIO EXCLUIDO
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel vincular a conta a um funcionario excluido.");
        }

        // OUTRO FUNCIONARIO JA POSSUI UMA CONTA — VINCULO EXCLUSIVO
        if (userAccountDao.existsByEmployeeAndUserIdNot(employee, userId)) {
            throw new DuplicateResourceException("Esse funcionario ja possui uma conta de usuario.");
        }

        // EMAIL RECEBE NOVA CRIPTOGRAFIA AES
        account.setEmail(encryptionService.encrypt(dto.getEmail()));

        // HASH SO E SUBSTITUIDO SE UMA NOVA SENHA FOR INFORMADA
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            if (dto.getPassword().length() < 8) {
                throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres.");
            }
            account.setPasswordHash(passwordHashService.hash(dto.getPassword()));
        }

        account.setEmployee(employee);
        account.setActive(dto.getActive());

        UserAccount updated = userAccountDao.save(account);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE — EMAIL E HASH ANONIMIZADOS; EMPLOYEEID MANTIDO COMO REFERENCIA HISTORICA
    public void delete(int userId) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada."));

        // IMPEDE DUPLA EXCLUSAO
        if (account.getDeletedAt() != null) {
            throw new BusinessRuleException("Esta conta ja foi excluida.");
        }

        // EMAIL E SENHA PERDEM QUALQUER RELACAO COM OS VALORES ORIGINAIS
        account.setEmail(anonymizationService.anonymize());
        // VALOR GERADO COM TAMANHO SUFICIENTE PARA RESPEITAR A CONSTRAINT >= 8 CHARS
        account.setPasswordHash(anonymizationService.anonymize());
        account.setLastLogin(null);
        // EMPLOYEEID MANTIDO PORQUE E OBRIGATORIO NO BANCO E SERVE COMO HISTORICO
        account.setActive(false);
        account.setDeletedAt(LocalDateTime.now());
        userAccountDao.save(account);
    }

    // RESPOSTA SEM HASH — EMAIL DESCRIPTOGRAFADO SOMENTE ENQUANTO NAO ANONIMIZADO
    private UserAccountResponseDTO toResponseDTO(UserAccount account) {
        UserAccountResponseDTO dto = new UserAccountResponseDTO();
        dto.setUserId(account.getUserId());

        boolean deleted = account.getDeletedAt() != null;
        if (deleted) {
            dto.setEmail("Anonimizado");
        } else {
            dto.setEmail(encryptionService.decrypt(account.getEmail()));
        }

        dto.setLastLogin(account.getLastLogin());
        dto.setActive(account.isActive());
        dto.setCreatedAt(account.getCreatedAt());
        dto.setUpdatedAt(account.getUpdatedAt());
        dto.setDeletedAt(account.getDeletedAt());

        Employee employee = account.getEmployee();
        dto.setEmployeeId(employee.getEmployeeId());
        // NOME DO FUNCIONARIO CONTINUA AES — DESCRIPTOGRAFADO APENAS PARA O DTO
        dto.setEmployeeName(encryptionService.decrypt(employee.getName()));
        dto.setStoreName(employee.getStore().getName());
        dto.setRoleName(employee.getRole().getName());

        return dto;
    }

    // DISPONIBILIDADE DO FUNCIONARIO — USADO PARA VALIDAR O SELECT NO FORMULARIO
    public boolean isEmployeeAvailable(Integer employeeId, Integer currentUserId) {
        if (currentUserId == null) {
            // CADASTRO: FUNCIONARIO NAO PODE TER NENHUMA CONTA
            return !userAccountDao.existsByEmployee_EmployeeId(employeeId);
        }
        // EDICAO: A PROPRIA CONTA PODE MANTER SEU FUNCIONARIO
        return !userAccountDao.existsByEmployee_EmployeeIdAndUserIdNot(employeeId, currentUserId);
    }
}
