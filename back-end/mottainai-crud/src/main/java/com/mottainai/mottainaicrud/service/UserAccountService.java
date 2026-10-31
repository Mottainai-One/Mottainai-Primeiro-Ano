package com.mottainai.crudinicial.service;

import com.mottainai.crudinicial.dao.EmployeeDao;
import com.mottainai.crudinicial.dao.UserAccountDao;
import com.mottainai.crudinicial.dto.UserAccountRequestDTO;
import com.mottainai.crudinicial.dto.UserAccountResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.model.Employee;
import com.mottainai.crudinicial.model.UserAccount;
import com.mottainai.crudinicial.security.DataAnonymizationService;
import com.mottainai.crudinicial.security.DataEncryptionService;
import com.mottainai.crudinicial.security.PasswordHashService;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

import java.util.List;
import com.mottainai.crudinicial.exception.BusinessRuleException;

// REGRAS ATUAIS DA CONTA DE FUNCIONÁRIO
// Valida o vínculo exclusivo com Employee, aplica AES no email e BCrypt na senha, sem autenticar o painel administrativo.

@Service
public class UserAccountService {

    private final UserAccountDao userAccountDao;
    private final EmployeeDao employeeDao;
    private final DataEncryptionService encryptionService;
    private final DataAnonymizationService anonymizationService;
    private final PasswordHashService passwordHashService;

    public UserAccountService(UserAccountDao userAccountDao, EmployeeDao employeeDao, DataEncryptionService encryptionService, DataAnonymizationService anonymizationService, PasswordHashService passwordHashService) {
        this.userAccountDao = userAccountDao;
        this.employeeDao = employeeDao;
        // Responsável pela criptografia reversível do e-mail
        // enquanto a conta continuar válida no sistema.
        this.encryptionService = encryptionService;
        // Usado quando os dados precisam perder
        // definitivamente a relação com o valor original.
        this.anonymizationService = anonymizationService;
        // Serviço responsável por transformar senhas em hashes
        // irreversíveis antes de persistir no banco.
        this.passwordHashService = passwordHashService;
    }

    // CADASTRO DA CONTA VINCULADA AO EMPLOYEE
    // Exige senha mínima e funcionário não excluído sem outra conta; persiste email AES e somente hash BCrypt.
    public UserAccountResponseDTO create(UserAccountRequestDTO dto) {

        if (dto.getPassword() == null
                || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Senha obrigatória."
            );
        }
        if (dto.getPassword().length() < 8) {
            throw new IllegalArgumentException(
                    "Senha deve ter pelo menos 8 caracteres."
            );
        }

        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));

        // Funcionários excluídos logicamente não podem
        // receber novas contas de usuário.
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível criar uma conta para um funcionário excluído."
            );
        }

        if (userAccountDao.existsByEmployee(employee)) {
            throw new DuplicateResourceException("Esse funcionário já possui uma conta de usuário.");
        }

        UserAccount account = new UserAccount();
        // O e-mail é criptografado antes de ser salvo no banco.
        account.setEmail(
                encryptionService.encrypt(dto.getEmail())
        );
        // A senha nunca é salva em texto puro.
        // Apenas o hash BCrypt é armazenado no banco.
        account.setPasswordHash(
                passwordHashService.hash(dto.getPassword()));
        account.setEmployee(employee);

        // Define a situação escolhida no formulário.
        account.setActive(dto.getActive());

        UserAccount saved = userAccountDao.save(account);
        return toResponseDTO(saved);
    }

    // CONSULTA DAS CONTAS ATUAIS E HISTÓRICAS
    // Converte os dados para DTO sem senha/hash; DeletedAt orienta a apresentação do email anonimizado.
    public List<UserAccountResponseDTO> findAll() {
        return userAccountDao.findAll().stream().map(this::toResponseDTO).toList();
    }

    // DETALHE DA CONTA
    // O DTO reúne Employee, loja e cargo sem expor PasswordHash ou a entidade persistida.
    public UserAccountResponseDTO findById(int userId) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));
        return toResponseDTO(account);
    }

    // EDIÇÃO DA CONTA
    // Mantém vínculo exclusivo, bloqueia apagados e só troca o BCrypt se uma senha nova não vazia for informada.
    public UserAccountResponseDTO update(int userId, UserAccountRequestDTO dto) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Conta não encontrada."));

        // Uma conta excluída logicamente não pode ser editada.
        if (account.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar uma conta excluída."
            );
        }

        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));

        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível vincular a conta a um funcionário excluído."
            );
        }

        if (userAccountDao.existsByEmployeeAndUserIdNot(
                employee,
                userId)) {

            throw new DuplicateResourceException(
                    "Esse funcionário já possui uma conta de usuário."
            );
        }

        // Salva novamente o e-mail usando uma nova criptografia AES.
        account.setEmail(
                encryptionService.encrypt(dto.getEmail())
        );
        // Só troca a senha quando uma nova senha
        // realmente tiver sido informada.
        if (dto.getPassword() != null
                && !dto.getPassword().isBlank()) {

            if (dto.getPassword().length() < 8) {
                throw new IllegalArgumentException(
                        "Senha deve ter pelo menos 8 caracteres."
                );
            }

            account.setPasswordHash(
                    passwordHashService.hash(
                            dto.getPassword()
                    )
            );
        }
        account.setEmployee(employee);
        account.setActive(dto.getActive());

        UserAccount updated = userAccountDao.save(account);
        return toResponseDTO(updated);
    }

    @Transactional
    // EXCLUSÃO LÓGICA DA CONTA
    // Descarta email, hash e último acesso; mantém EmployeeId e o registro para preservar o histórico.
    public void delete(int userId) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Conta não encontrada."
                        )
                );

        // Impede que uma conta já excluída
        // seja anonimizada novamente.
        if (account.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Esta conta já foi excluída."
            );
        }

        // O e-mail perde definitivamente qualquer relação
        // com o endereço original.
        account.setEmail(
                anonymizationService.anonymize()
        );
        // A senha também perde qualquer utilidade.
        // O valor gerado possui tamanho suficiente para continuar
        // respeitando a constraint PasswordHash >= 8 caracteres.
        account.setPasswordHash(
                anonymizationService.anonymize()
        );
        // Remove o histórico de último acesso,
        // pois ele não é necessário após a exclusão.
        account.setLastLogin(null);
        // O vínculo com Employee é mantido como referência histórica
        // e também porque EmployeeId é obrigatório no banco.
        account.setActive(false);
        account.setDeletedAt(LocalDateTime.now());
        userAccountDao.save(account);
    }

    // RESPOSTA SEM SENHA OU HASH
    // Email é descriptografado somente enquanto não foi anonimizado; o nome AES do Employee compõe a referência histórica.
    private UserAccountResponseDTO toResponseDTO(
            UserAccount account) {

        UserAccountResponseDTO dto =
                new UserAccountResponseDTO();

        dto.setUserId(account.getUserId());

        boolean deleted =
                account.getDeletedAt() != null;

        if (deleted) {

            dto.setEmail("Anonimizado");

        } else {

            dto.setEmail(
                    encryptionService.decrypt(
                            account.getEmail()
                    )
            );
        }

        dto.setLastLogin(
                account.getLastLogin()
        );

        dto.setActive(
                account.isActive()
        );

        dto.setCreatedAt(
                account.getCreatedAt()
        );

        dto.setUpdatedAt(
                account.getUpdatedAt()
        );

        dto.setDeletedAt(
                account.getDeletedAt()
        );

        Employee employee =
                account.getEmployee();

        dto.setEmployeeId(
                employee.getEmployeeId()
        );

        dto.setEmployeeName(
                encryptionService.decrypt(
                        employee.getName()
                )
        );

        dto.setStoreName(
                employee.getStore().getName()
        );

        dto.setRoleName(
                employee.getRole().getName()
        );

        return dto;
    }

    // DISPONIBILIDADE DO FUNCIONÁRIO
    // O cadastro exige ausência de conta; na edição, UserIdNot preserva o vínculo da própria conta.
    public boolean isEmployeeAvailable(
            Integer employeeId,
            Integer currentUserId) {

        // Durante o cadastro, o funcionário
        // não pode possuir nenhuma conta.
        if (currentUserId == null) {
            return !userAccountDao
                    .existsByEmployee_EmployeeId(
                            employeeId
                    );
        }

        // Durante a edição, a conta atual deve
        // continuar podendo usar seu funcionário.
        return !userAccountDao
                .existsByEmployee_EmployeeIdAndUserIdNot(
                        employeeId,
                        currentUserId
                );
    }
}