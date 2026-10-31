package com.mottainai.crudinicial.service;

import com.mottainai.crudinicial.dao.EmployeeDao;
import com.mottainai.crudinicial.security.DataEncryptionService;
import com.mottainai.crudinicial.dao.EmployeeRoleDao;
import com.mottainai.crudinicial.dao.StoreDao;
import com.mottainai.crudinicial.dto.EmployeeRequestDTO;
import com.mottainai.crudinicial.dto.EmployeeResponseDTO;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.model.Employee;
import com.mottainai.crudinicial.model.EmployeeRole;
import com.mottainai.crudinicial.model.Store;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;
import com.mottainai.crudinicial.security.DataAnonymizationService;
import com.mottainai.crudinicial.dao.UserAccountDao;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import com.mottainai.crudinicial.exception.BusinessRuleException;

// REGRAS E DADOS PESSOAIS DO FUNCIONÁRIO
// Centraliza AES e a cascata para a conta vinculada, usando loja/cargo técnicos para preservar as FKs.

@Service
public class EmployeeService {

    private final EmployeeDao employeeDao;
    private final StoreDao storeDao;
    private final EmployeeRoleDao employeeRoleDao;
    // DAO usado para localizar a conta vinculada ao funcionário
    // durante o processo de exclusão.
    private final UserAccountDao userAccountDao;

    // Serviço responsável por criptografar e descriptografar
    // os dados pessoais dos funcionários.
    private final DataEncryptionService encryptionService;

    // Serviço responsável por substituir dados por valores
    // aleatórios e irreversíveis durante uma exclusão.
    private final DataAnonymizationService anonymizationService;

    public EmployeeService(EmployeeDao employeeDao, StoreDao storeDao, EmployeeRoleDao employeeRoleDao, DataEncryptionService encryptionService, DataAnonymizationService anonymizationService, UserAccountDao userAccountDao) {
        this.employeeDao = employeeDao;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;

        // Guarda o DAO para podermos tratar a conta do usuário
        // junto da exclusão do funcionário.
        this.userAccountDao = userAccountDao;

        // O Spring fornece automaticamente a instância do serviço
        // de criptografia por meio da injeção de dependências.
        this.encryptionService = encryptionService;

        // O Spring também injeta automaticamente o serviço
        // responsável pela anonimização irreversível.
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO COM LOJA E CARGO EXISTENTES
    // Vínculos apagados são rejeitados; CPF, nome, email e telefone entram no banco somente após AES.
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada."));

        // Uma loja excluída não pode receber novos funcionários.
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível cadastrar um funcionário em uma loja excluída."
            );
        }

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado."));

        // Um cargo excluído não pode ser utilizado
        // em um novo vínculo de funcionário.
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um cargo excluído."
            );
        }

        Employee employee = new Employee();
        // Define a situação inicial do funcionário conforme
        // o valor enviado pelo formulário (ativo ou inativo).
        employee.setActive(dto.getActive());
        // Criptografa os dados pessoais antes de atribuí-los
        // à entidade que será salva no banco de dados.
        // O DTO continua recebendo os valores originais do formulário.
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

    // LISTAGEM POR DTO
    // A situação e os relacionamentos são apresentados com dados descriptografados ou mascarados, conforme DeletedAt.
    public List<EmployeeResponseDTO> findAll() {
        return employeeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // Agora busca pelo employeeId (PK), não mais pelo CPF
    public EmployeeResponseDTO findById(Integer employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));
        return toResponseDTO(employee);
    }

    // EDIÇÃO DOS DADOS PESSOAIS
    // Bloqueia funcionário/vínculos excluídos; os dados atualizados recebem nova criptografia antes de salvar.
    public EmployeeResponseDTO update(Integer employeeId, EmployeeRequestDTO dto) {
        Employee employee = employeeDao.findById(employeeId).orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));

        // Um funcionário excluído logicamente não pode
        // voltar a ser alterado pelo CRUD.
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar um funcionário excluído."
            );
        }

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada."));

        // Impede mover o funcionário para uma loja excluída.
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível vincular o funcionário a uma loja excluída."
            );
        }

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado."));

        // Impede atribuir um cargo já excluído.
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um cargo excluído."
            );
        }

        // Criptografa os dados pessoais atualizados antes de salvá-los.
        // O DTO recebe os dados originais, mas a entidade armazena
        // os valores criptografados que serão enviados ao PostgreSQL.
        employee.setCpf(encryptionService.encrypt(dto.getCpf()));
        employee.setName(encryptionService.encrypt(dto.getName()));
        employee.setEmail(encryptionService.encrypt(dto.getEmail()));
        employee.setPhone(encryptionService.encrypt(dto.getPhone()));
        employee.setHireDate(dto.getHireDate());
        employee.setStore(store);
        employee.setRole(role);
        // Atualiza a situação do funcionário sem excluir seu cadastro.
        // Um funcionário inativo continua armazenado normalmente.
        employee.setActive(dto.getActive());

        Employee updated = employeeDao.save(employee);
        return toResponseDTO(updated);
    }


    @Transactional
    // ANONIMIZAÇÃO E CASCATA PARA A CONTA
    // Preserva o nome em AES e substitui os outros dados pessoais; loja/cargo passam a registros técnicos.
    public void delete(Integer employeeId) {

        // Busca o funcionário que será excluído.
        // Diferente do deleteById(), precisamos carregar a entidade
        // porque vamos alterar seus dados antes de salvá-la novamente.
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Funcionário não encontrado."
                        )
                );

        // Impede uma segunda exclusão do mesmo funcionário.
        if (employee.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Este funcionário já foi excluído."
            );
        }

        // Substitui os dados pessoais por valores aleatórios
        // que não possuem nenhuma relação com os valores originais.
        // Como não existe chave ou tabela de correspondência,
        // os dados anteriores não podem ser recuperados.
        employee.setCpf(
                anonymizationService.anonymize()
        );

        employee.setEmail(
                anonymizationService.anonymize()
        );

        employee.setPhone(
                anonymizationService.anonymize()
        );

        // O nome NÃO é anonimizado.
        // Ele continua criptografado com AES para que possa
        // permanecer como identificação histórica do funcionário.

        // Remove de forma irreversível a data real de contratação.
        // O banco continua recebendo uma LocalDate válida.
        employee.setHireDate(
                anonymizationService.anonymizeDate()
        );

        // Marca o registro como excluído sem removê-lo fisicamente.
        employee.setActive(false);

        // Registra o momento em que a exclusão aconteceu.
        employee.setDeletedAt(
                LocalDateTime.now()
        );

        // Localiza o cargo genérico reservado para registros excluídos.
        // Dessa forma, o cargo real do funcionário deixa de permanecer
        // associado ao registro após a anonimização.
        // BUSCA DO REGISTRO TÉCNICO PELO NOME
        // Registro anonimizado é obrigatório para preservar a FK; sua ausência é falha de configuração, não conflito do usuário.
        EmployeeRole anonymizedRole = employeeRoleDao.findByName(TECHNICAL_ROLE_NAME).orElseThrow(() -> new IllegalStateException(
                                        "Cargo de anonimização não encontrado."
                                )
                        );

        employee.setRole(anonymizedRole);

        // Localiza a loja técnica criada exclusivamente
        // para registros de funcionários anonimizados.
        // BUSCA DA LOJA TÉCNICA PELO CNPJ
        // O vínculo substituto deve existir previamente; IllegalStateException mantém essa ausência como falha técnica.
        Store anonymizedStore =
                storeDao
                        .findByCnpj(TECHNICAL_STORE_CNPJ)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Loja de anonimização não encontrada."
                                )
                        );

        // Remove a associação com a loja real do funcionário
        // sem quebrar a foreign key obrigatória do banco.
        employee.setStore(anonymizedStore);

        // Procura uma conta vinculada ao funcionário.
        // Nem todo funcionário precisa obrigatoriamente ter uma conta,
        // por isso usamos ifPresent().
        userAccountDao.findByEmployee(employee)
                .filter(userAccount -> userAccount.getDeletedAt() == null)
                .ifPresent(userAccount -> {
                    // Substitui o e-mail por um valor aleatório
                    // sem relação com o valor original.
                    userAccount.setEmail(
                            anonymizationService.anonymize()
                    );

                    // O hash antigo da senha também é descartado.
                    // Não precisamos recuperar ou descriptografar a senha,
                    // pois hashes são irreversíveis.
                    userAccount.setPasswordHash(
                            anonymizationService.anonymize()
                    );

                    // Remove o histórico de último acesso,
                    // pois ele não é necessário após a exclusão.
                    userAccount.setLastLogin(null);

                    // A conta deixa de poder ser utilizada.
                    userAccount.setActive(false);

                    // Registra que a conta foi excluída junto do funcionário.
                    userAccount.setDeletedAt(
                            LocalDateTime.now()
                    );

                    // Mantém a linha da conta no banco,
                    // mas com seus dados pessoais irreversivelmente substituídos.
                    userAccountDao.save(userAccount);
                });

        // Salva as alterações no mesmo registro.
        employeeDao.save(employee);
    }

    // SAÍDA DOS DADOS PESSOAIS
    // O nome histórico permanece AES; após soft delete, CPF/email/telefone são marcados como anonimizados, não descriptografados.
    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        // O nome continua criptografado mesmo após a exclusão,
        // pois é o único dado pessoal mantido para identificação histórica.
        dto.setName(
                encryptionService.decrypt(employee.getName())
        );

        // Se o funcionário já foi excluído, CPF, e-mail e telefone
        // foram substituídos por valores irreversíveis.
        // Por isso eles não podem ser enviados ao decrypt().
        if (employee.getDeletedAt() != null) {
            // Não expõe nem mesmo o valor aleatório armazenado no banco.
            // A interface recebe apenas a informação de que o dado foi anonimizado.
            dto.setCpf("Anonimizado");
            dto.setEmail("Anonimizado");
            dto.setPhone("Anonimizado");
        } else {
            // Enquanto o funcionário não foi excluído,
            // esses campos continuam protegidos com AES e podem
            // ser recuperados para operações autorizadas do CRUD.
            dto.setCpf(
                    encryptionService.decrypt(employee.getCpf())
            );

            dto.setEmail(
                    encryptionService.decrypt(employee.getEmail())
            );

            dto.setPhone(
                    encryptionService.decrypt(employee.getPhone())
            );
        }
        // Os demais dados não utilizam criptografia AES
        // e podem ser transferidos diretamente para o DTO.

        // Funcionários excluídos não possuem mais uma data
        // de contratação real disponível.
        if (employee.getDeletedAt() != null) {
            dto.setHireDate(null);
        } else {
            dto.setHireDate(employee.getHireDate());
        }
        dto.setStoreId(employee.getStore().getStoreId());
        dto.setRoleId(employee.getRole().getRoleId());
        // Recupera os nomes da loja e do cargo associados ao funcionário.
        // Esses nomes serão usados na listagem e nos detalhes do CRUD.
        dto.setStoreName(employee.getStore().getName());
        dto.setRoleName(employee.getRole().getName());

        // Recupera a situação atual do funcionário (ativo ou inativo).
        dto.setActive(employee.isActive());

        // Recupera as datas de controle herdadas da classe Registrated.
        // CreatedAt e UpdatedAt são gerenciadas pelo Hibernate.
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());

        // A data de exclusão permanece nula enquanto
        // o funcionário não tiver sido excluído.
        dto.setDeletedAt(employee.getDeletedAt());
        return dto;
    }
}