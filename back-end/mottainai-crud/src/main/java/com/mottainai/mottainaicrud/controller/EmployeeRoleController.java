package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.EmployeeRoleRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeRoleResponseDTO;
import com.mottainai.mottainaicrud.service.EmployeeRoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee-roles")
public class EmployeeRoleController {

    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleController(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }

    // CREATE - POST /api/employee-roles
    @PostMapping
    public ResponseEntity<EmployeeRoleResponseDTO> create(@Valid @RequestBody EmployeeRoleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeRoleService.create(dto));
    }

    // READ (TODOS) - GET /api/employee-roles
    @GetMapping
    public ResponseEntity<List<EmployeeRoleResponseDTO>> findAll() {
        return ResponseEntity.ok(employeeRoleService.findAll());
    }

    // READ (POR ROLE ID) - GET /api/employee-roles/{roleId}
    @GetMapping("/{roleId}")
    public ResponseEntity<EmployeeRoleResponseDTO> findById(@PathVariable int roleId) {
        return ResponseEntity.ok(employeeRoleService.findById(roleId));
    }

    // UPDATE - PUT /api/employee-roles/{roleId}
    @PutMapping("/{roleId}")
    public ResponseEntity<EmployeeRoleResponseDTO> update(@PathVariable int roleId,
                                                          @Valid @RequestBody EmployeeRoleRequestDTO dto) {
        return ResponseEntity.ok(employeeRoleService.update(roleId, dto));
    }

    // DELETE - DELETE /api/employee-roles/{roleId}
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> delete(@PathVariable int roleId) {
        employeeRoleService.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}