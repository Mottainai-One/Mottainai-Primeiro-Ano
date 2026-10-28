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
public class EmployeeService {

    private final EmployeeDao employeeDao;
    private final StoreDao storeDao;
    private final EmployeeRoleDao employeeRoleDao;

    public EmployeeService(
            EmployeeDao employeeDao,
            StoreDao storeDao,
            EmployeeRoleDao employeeRoleDao
    ) {
        this.employeeDao = employeeDao;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;
    }

    public List<EmployeeResponseDTO> findAll() {
        return employeeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public EmployeeResponseDTO findById(Integer employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionário não encontrado."
                ));

        return toResponseDTO(employee);
    }

    private EmployeeResponseDTO toResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setEmployeeId(employee.getEmployeeId());
        dto.setCpf(employee.getCpf());
        dto.setName(employee.getName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        dto.setHireDate(employee.getHireDate());
        dto.setStoreId(employee.getStore().getStoreId());
        dto.setRoleId(employee.getRole().getRoleId());
        return dto;
    }
}
