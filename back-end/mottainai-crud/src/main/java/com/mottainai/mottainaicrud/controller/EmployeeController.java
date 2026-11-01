package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.EmployeeRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
import com.mottainai.mottainaicrud.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE FUNCIONÁRIOS — /api/employees
// Os DTOs separam a entrada validada das entidades que armazenam dados pessoais protegidos por AES.

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // CREATE
    // POST /api/employees
    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO created = employeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ (todos)
    // GET /api/employees
    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> findAll() {
        List<EmployeeResponseDTO> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }

    // READ (por employeeId)
    // GET /api/employees/{employeeId}
    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> findById(@PathVariable Integer employeeId) {
        EmployeeResponseDTO employee = employeeService.findById(employeeId);
        return ResponseEntity.ok(employee);
    }

    // UPDATE
    // PUT /api/employees/{employeeId}
    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Integer employeeId, @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updated = employeeService.update(employeeId, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    // DELETE /api/employees/{employeeId}
    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable Integer employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}