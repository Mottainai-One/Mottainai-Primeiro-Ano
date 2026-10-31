package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.service.AdministrationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GESTÃO AUTENTICADA: WebConfig já protege /api/**; as respostas contêm apenas ID e email.
@RestController
@RequestMapping("/api/administrations")
public class AdministrationController {
    private final AdministrationService administrationService;

    public AdministrationController(AdministrationService administrationService) {this.administrationService = administrationService;}

    // CADASTRO PRIVADO
    @PostMapping
    public ResponseEntity<AdministrationResponseDTO> create(@Valid @RequestBody AdministrationRequestDTO dto) {
        AdministrationResponseDTO created = administrationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // LISTAGEM SEM CREDENCIAIS
    @GetMapping
    public ResponseEntity<List<AdministrationResponseDTO>> findAll() {
        List<AdministrationResponseDTO> administrations = administrationService.findAll();
        return ResponseEntity.ok(administrations);
    }

    @GetMapping("/{administrationId}")
    public ResponseEntity<AdministrationResponseDTO> findById(
            @PathVariable int administrationId) {

        return ResponseEntity.ok(
                administrationService.findById(administrationId)
        );
    }

    // EDIÇÃO SEM ALTERAR O FLUXO DE AUTENTICAÇÃO

    @PutMapping("/{administrationId}")
    public ResponseEntity<AdministrationResponseDTO> update(@PathVariable int administrationId, @Valid @RequestBody AdministrationRequestDTO dto,
            jakarta.servlet.http.HttpSession session) {
        AdministrationResponseDTO updated =
                administrationService.update(administrationId, dto);
        // Atualiza o perfil da própria sessão sem consultar novamente as credenciais.
        if (Integer.valueOf(administrationId).equals(session.getAttribute("administrationId"))) {
            session.setAttribute("administrationEmail", updated.getEmail());
        }
        return ResponseEntity.ok(updated);
    }

    // O Service bloqueia a própria conta tanto para a API quanto para o formulário JSP.
    @DeleteMapping("/{administrationId}")
    public ResponseEntity<Void> delete(@PathVariable int administrationId,
                                       jakarta.servlet.http.HttpSession session) {
        administrationService.delete(administrationId, (Integer) session.getAttribute("administrationId"));
        return ResponseEntity.noContent().build();
    }

}
