package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.UserAccountDao;
import com.mottainai.mottainaicrud.dto.UserAccountRequestDTO;
import com.mottainai.mottainaicrud.dto.UserAccountResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAccountService {

    // ACESSO AOS DADOS DE CONTAS
    private final UserAccountDao userAccountDao;

    // ACESSO AOS DADOS DE FUNCIONARIOS
    private final EmployeeDao employeeDao;

    public UserAccountService(
            UserAccountDao userAccountDao,
            EmployeeDao employeeDao
    ) {
        this.userAccountDao = userAccountDao;
        this.employeeDao = employeeDao;
    }

    // LISTA TODAS AS CONTAS
    public List<UserAccountResponseDTO> findAll() {
        return userAccountDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA CONTA PELO ID
    public UserAccountResponseDTO findById(int userId) {
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conta não encontrada."
                ));

        return toResponseDTO(account);
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private UserAccountResponseDTO toResponseDTO(UserAccount account) {
        UserAccountResponseDTO dto = new UserAccountResponseDTO();
        dto.setUserId(account.getUserId());
        dto.setEmail(account.getEmail());
        dto.setLastLogin(account.getLastLogin());
        dto.setEmployeeId(account.getEmployee().getEmployeeId());
        return dto;
    }

    public UserAccountResponseDTO create(UserAccountRequestDTO dto) {

        // VALIDA SENHA OBRIGATORIA
        if (dto.getPasswordHash() == null || dto.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória.");
        }

        // VALIDA TAMANHO DA SENHA
        if (dto.getPasswordHash().length() < 8) {
            throw new IllegalArgumentException(
                    "Senha deve ter pelo menos 8 caracteres."
            );
        }

        // BUSCA O FUNCIONARIO VINCULADO
        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionário não encontrado."
                ));

        // VALIDA CONTA UNICA POR FUNCIONARIO
        if (userAccountDao.existsByEmployee(employee)) {
            throw new DuplicateResourceException(
                    "Esse funcionário já possui uma conta de usuário."
            );
        }

        // MONTA A CONTA PARA SALVAR
        UserAccount account = new UserAccount();
        account.setEmail(dto.getEmail());
        account.setPasswordHash(dto.getPasswordHash()); // TODO: APLICAR HASH
        account.setEmployee(employee);

        // SALVA E RETORNA A RESPOSTA
        UserAccount saved = userAccountDao.save(account);
        return toResponseDTO(saved);
    }

    public UserAccountResponseDTO update(
            int userId,
            UserAccountRequestDTO dto
    ) {
        // BUSCA A CONTA EXISTENTE
        UserAccount account = userAccountDao.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Conta não encontrada."
                ));

        // BUSCA O FUNCIONARIO INFORMADO
        Employee employee = employeeDao.findById(dto.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionário não encontrado."
                ));

        // VALIDA CONTA UNICA POR FUNCIONARIO
        if (userAccountDao.existsByEmployeeAndUserIdNot(employee, userId)) {
            throw new DuplicateResourceException(
                    "Esse funcionário já possui uma conta de usuário."
            );
        }

        account.setEmail(dto.getEmail());

        // ALTERA A SENHA SE INFORMADA
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {

            // VALIDA TAMANHO DA SENHA
            if (dto.getPasswordHash().length() < 8) {
                throw new IllegalArgumentException(
                        "Senha deve ter pelo menos 8 caracteres."
                );
            }

            account.setPasswordHash(dto.getPasswordHash());
        }

        account.setEmployee(employee);

        // SALVA AS ALTERACOES
        UserAccount updated = userAccountDao.save(account);
        return toResponseDTO(updated);
    }

    // REMOVE CONTA PELO ID
    public void delete(int userId) {
        if (!userAccountDao.existsById(userId)) {
            throw new ResourceNotFoundException(
                    "Conta não encontrada."
            );
        }

        userAccountDao.deleteById(userId);
    }
}
