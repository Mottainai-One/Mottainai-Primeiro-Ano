package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.EmployeeRoleRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeRoleResponseDTO;
import com.mottainai.mottainaicrud.service.EmployeeRoleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE CARGOS — /api/employee-roles
// Encaminha o cadastro ao Service, que protege o cargo técnico e os vínculos com funcionários.

@RestController
@RequestMapping("/api/employee-roles")
public class EmployeeRoleController {

    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleController(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }

    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<EmployeeRoleResponseDTO> create(@Valid @RequestBody EmployeeRoleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeRoleService.create(dto));
    }

    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<EmployeeRoleResponseDTO>> findAll() {
        return ResponseEntity.ok(employeeRoleService.findAll());
    }

    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{roleId}")
    public ResponseEntity<EmployeeRoleResponseDTO> findById(@PathVariable int roleId) {
        return ResponseEntity.ok(employeeRoleService.findById(roleId));
    }

    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{roleId}")
    public ResponseEntity<EmployeeRoleResponseDTO> update(@PathVariable int roleId,
                                                          @Valid @RequestBody EmployeeRoleRequestDTO dto) {
        return ResponseEntity.ok(employeeRoleService.update(roleId, dto));
    }

    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{roleId}")
    public ResponseEntity<Void> delete(@PathVariable int roleId) {
        employeeRoleService.delete(roleId);
        return ResponseEntity.noContent().build();
    }
}