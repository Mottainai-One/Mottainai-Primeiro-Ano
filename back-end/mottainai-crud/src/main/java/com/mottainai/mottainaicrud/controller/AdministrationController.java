package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.service.AdministrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// CONTROLADOR REST RESPONSAVEL POR EXPOR ENDPOINTS DE API EM FORMATO JSON
@RestController
@RequestMapping("/api/administrations")
public class AdministrationController {
    // INJECAO DE DEPENDENCIA DA CAMADA DE SERVICO
    private final AdministrationService administrationService;

    // CONSTRUTOR COM INJECAO DE DEPENDENCIA
    public AdministrationController(AdministrationService administrationService) {this.administrationService = administrationService;}

    //CREATE
    //POST /api/administrations
    @PostMapping
    public ResponseEntity<AdministrationResponseDTO> create(@Valid @RequestBody AdministrationRequestDTO dto) {
        AdministrationResponseDTO created = administrationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    //READ (todos)
    //GET /api/administrations
    @GetMapping
    public ResponseEntity<List<AdministrationResponseDTO>> findAll() {
        List<AdministrationResponseDTO> administrations = administrationService.findAll();
        return ResponseEntity.ok(administrations);
    }

    // ENDPOINT PARA BUSCA DE UM ADMINISTRADOR POR IDENTIFICADOR
    // RETORNA STATUS HTTP 200 OK OU EXCECAO SE NAO ENCONTRADO
    @GetMapping("/{administrationId}")
    public ResponseEntity<AdministrationResponseDTO> findById(
            @PathVariable int administrationId) {

        return ResponseEntity.ok(
                administrationService.findById(administrationId)
        );
    }

    //UPDATE
    // PUT /api/administrations/{administrationId}

    @PutMapping("/{administrationId}")
    public ResponseEntity<AdministrationResponseDTO> update(@PathVariable int administrationId, @Valid @RequestBody AdministrationRequestDTO dto) {
        AdministrationResponseDTO updated =
                administrationService.update(administrationId, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/administrations/{administrationId}
    @DeleteMapping("/{administrationId}")
    public ResponseEntity<Void> delete(@PathVariable int administrationId) {
        administrationService.delete(administrationId);
        return ResponseEntity.noContent().build();
    }
}