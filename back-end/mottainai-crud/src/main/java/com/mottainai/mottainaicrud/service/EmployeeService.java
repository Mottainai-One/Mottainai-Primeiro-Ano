package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.model.EmployeeRoleService;
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
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

    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {
        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        Employee employee = new Employee();
        employee.setCpf(dto.getCpf());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setHireDate(dto.getHireDate());
        employee.setStore(store);
        employee.setRole(role);

        Employee saved = employeeDao.save(employee);
        return toResponseDTO(saved);
    }

    public EmployeeResponseDTO update(Integer employeeId, EmployeeRequestDTO dto) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Funcionário não encontrado."
                ));

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cargo não encontrado."
                ));

        employee.setCpf(dto.getCpf());
        employee.setName(dto.getName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setHireDate(dto.getHireDate());
        employee.setStore(store);
        employee.setRole(role);

        Employee updated = employeeDao.save(employee);
        return toResponseDTO(updated);
    }

    public void delete(Integer employeeId) {
        if (!employeeDao.existsById(employeeId)) {
            throw new ResourceNotFoundException(
                    "Funcionário não encontrado."
            );
        }

        employeeDao.deleteById(employeeId);
    }
}
