package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.EmployeeRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
import com.mottainai.mottainaicrud.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // CREATE
    // POST /api/employees
    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> create(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO created = employeeService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ (TODOS)
    // GET /api/employees
    @GetMapping
    public ResponseEntity<List<EmployeeResponseDTO>> findAll() {
        List<EmployeeResponseDTO> employees = employeeService.findAll();
        return ResponseEntity.ok(employees);
    }

    // READ (POR EMPLOYEEID)
    // GET /api/employees/{employeeId}
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> findById(@PathVariable Integer employeeId) {
        EmployeeResponseDTO employee = employeeService.findById(employeeId);
        return ResponseEntity.ok(employee);
    }

    // UPDATE
    // PUT /api/employees/{employeeId}
    @PutMapping("/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> update(@PathVariable Integer employeeId, @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO updated = employeeService.update(employeeId, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    // DELETE /api/employees/{employeeId}
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<Void> delete(@PathVariable Integer employeeId) {
        employeeService.delete(employeeId);
        return ResponseEntity.noContent().build();
    }
}