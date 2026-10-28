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
}
