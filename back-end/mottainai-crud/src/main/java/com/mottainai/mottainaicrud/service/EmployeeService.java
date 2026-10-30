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
public class EmployeeService {

    // ACESSO AOS DADOS DE FUNCIONARIOS
    private final EmployeeDao employeeDao;

    // ACESSO AOS DADOS DE LOJAS
    private final StoreDao storeDao;

    // ACESSO AOS DADOS DE CARGOS
    private final EmployeeRoleDao employeeRoleDao;

    public EmployeeService(EmployeeDao employeeDao, StoreDao storeDao, EmployeeRoleDao employeeRoleDao) {
        this.employeeDao = employeeDao;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;
    }

    // CADASTRA NOVO FUNCIONARIO
    public EmployeeResponseDTO create(EmployeeRequestDTO dto) {

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada."));
        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado."));

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

    // LISTA TODOS OS FUNCIONARIOS
    public List<EmployeeResponseDTO> findAll() {
        return employeeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA FUNCIONARIO PELO ID
    public EmployeeResponseDTO findById(Integer employeeId) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));
        return toResponseDTO(employee);
    }

    // ATUALIZA DADOS DO FUNCIONARIO
    public EmployeeResponseDTO update(Integer employeeId, EmployeeRequestDTO dto) {
        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Funcionário não encontrado."));

        Store store = storeDao.findById(dto.getStoreId())
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada."));
        EmployeeRole role = employeeRoleDao.findById(dto.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado."));

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

    // REMOVE FUNCIONARIO PELO ID
    public void delete(Integer employeeId) {
        if (!employeeDao.existsById(employeeId)) {
            throw new ResourceNotFoundException("Funcionário não encontrado.");
        }
        employeeDao.deleteById(employeeId);
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
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
