package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.StoreRequestDTO;
import com.mottainai.mottainaicrud.dto.StoreResponseDTO;
import com.mottainai.mottainaicrud.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE LOJAS — /api/stores
// Recebe o RequestDTO e delega ao Service a validação de empresa, endereço e exclusão em cascata.

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<StoreResponseDTO> create(@Valid @RequestBody StoreRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(dto));
    }

    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> findAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> findById(@PathVariable int storeId) {
        return ResponseEntity.ok(storeService.findById(storeId));
    }

    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> update(@PathVariable int storeId,
                                                   @Valid @RequestBody StoreRequestDTO dto) {
        return ResponseEntity.ok(storeService.update(storeId, dto));
    }

    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable int storeId) {
        storeService.delete(storeId);
        return ResponseEntity.noContent().build();
    }
}