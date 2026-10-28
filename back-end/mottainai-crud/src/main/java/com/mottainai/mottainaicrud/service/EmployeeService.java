package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.EmployeeRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Employee;
import com.mottainai.mottainaicrud.model.EmployeeRole;
import com.mottainai.mottainaicrud.model.Store;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeRoleService {

    // ACESSO AOS DADOS DE CARGOS
    private final EmployeeRoleDao employeeRoleDao;

    // ACESSO AOS DADOS DE FUNCIONARIOS
    private final EmployeeDao employeeDao;

    public EmployeeRoleService(
            EmployeeRoleDao employeeRoleDao,
            EmployeeDao employeeDao
    ) {
        this.employeeRoleDao = employeeRoleDao;
        this.employeeDao = employeeDao;
    }

    // LISTA TODOS OS CARGOS
    public List<EmployeeRoleResponseDTO> findAll() {
        return employeeRoleDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA CARGO PELO ID
    public EmployeeRoleResponseDTO findById(int roleId) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        return toResponseDTO(role);
    }

    // CONVERTE DTO PARA ENTIDADE
    private EmployeeRole toEntity(EmployeeRoleRequestDTO dto) {
        EmployeeRole role = new EmployeeRole();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        return role;
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private EmployeeRoleResponseDTO toResponseDTO(EmployeeRole role) {
        EmployeeRoleResponseDTO dto = new EmployeeRoleResponseDTO();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissionLevel(role.getPermissionLevel());
        return dto;
    }

    // CADASTRA UM NOVO CARGO
    public EmployeeRoleResponseDTO create(EmployeeRoleRequestDTO dto) {

        // VALIDA NOME UNICO
        if (employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Já existe um cargo com esse nome."
            );
        }

        // SALVA E RETORNA A RESPOSTA
        EmployeeRole saved = employeeRoleDao.save(toEntity(dto));
        return toResponseDTO(saved);
    }

    // ATUALIZA CARGO PELO ID
    public EmployeeRoleResponseDTO update(
            int roleId,
            EmployeeRoleRequestDTO dto
    ) {
        // BUSCA O CARGO EXISTENTE
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        // VALIDA NOME AO ALTERAR
        if (!role.getName().equals(dto.getName())
                && employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException(
                    "Já existe um cargo com esse nome."
            );
        }

        // ATUALIZA OS DADOS DO CARGO
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());

        // SALVA AS ALTERACOES
        EmployeeRole updated = employeeRoleDao.save(role);
        return toResponseDTO(updated);
    }

    // REMOVE CARGO PELO ID
    public void delete(int roleId) {

        // BUSCA O CARGO EXISTENTE
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        // VALIDA VINCULO COM FUNCIONARIOS
        if (employeeDao.existsByRole(role)) {
            throw new DuplicateResourceException(
                    "Não é possível excluir um cargo associado a funcionários."
            );
        }

        employeeRoleDao.delete(role);
    }
}