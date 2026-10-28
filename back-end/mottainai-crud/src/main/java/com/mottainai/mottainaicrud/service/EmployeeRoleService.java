package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.model.EmployeeRoleService;
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dto.EmployeeRoleResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.GlobalExceptionHandler;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class EmployeeRoleService {

    private final EmployeeRoleDao employeeRoleDao;
    private final EmployeeDao employeeDao;

    public EmployeeRoleService(
            EmployeeRoleDao employeeRoleDao,
            EmployeeDao employeeDao
    ) {
        this.employeeRoleDao = employeeRoleDao;
        this.employeeDao = employeeDao;
    }

    public List<EmployeeRoleResponseDTO> findAll() {
        return employeeRoleDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EmployeeRoleResponseDTO findById(int roleId) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        return toResponseDTO(role);
    }

    private EmployeeRole toEntity(EmployeeRoleRequestDTO dto) {
        EmployeeRole role = new EmployeeRole();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        return role;
    }

    private EmployeeRoleResponseDTO toResponseDTO(EmployeeRole role) {
        EmployeeRoleResponseDTO dto = new EmployeeRoleResponseDTO();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissionLevel(role.getPermissionLevel());
        return dto;
    }
}
