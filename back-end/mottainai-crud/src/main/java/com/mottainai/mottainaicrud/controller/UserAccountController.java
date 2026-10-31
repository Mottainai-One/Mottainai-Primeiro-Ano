package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.UserAccountRequestDTO;
import com.mottainai.crudinicial.dto.UserAccountResponseDTO;
import com.mottainai.crudinicial.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE CONTAS DE FUNCIONÁRIOS — /api/user-accounts
// Mantém o CRUD atual de UserAccount; senhas entram no RequestDTO e hashes não saem no ResponseDTO.

@RestController
@RequestMapping("/api/user-accounts")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<UserAccountResponseDTO> create(@Valid @RequestBody UserAccountRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAccountService.create(dto));
    }

    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<UserAccountResponseDTO>> findAll() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{userId}")
    public ResponseEntity<UserAccountResponseDTO> findById(@PathVariable int userId) {
        return ResponseEntity.ok(userAccountService.findById(userId));
    }

    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{userId}")
    public ResponseEntity<UserAccountResponseDTO> update(@PathVariable int userId,
                                                         @Valid @RequestBody UserAccountRequestDTO dto) {
        return ResponseEntity.ok(userAccountService.update(userId, dto));
    }

    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable int userId) {
        userAccountService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}