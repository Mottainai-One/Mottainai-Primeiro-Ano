package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.SubscriptionPlanRequestDTO;
import com.mottainai.crudinicial.dto.SubscriptionPlanResponseDTO;
import com.mottainai.crudinicial.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE PLANOS — /api/subscription-plans
// O Service controla duplicidade, vínculos com empresas e exclusão lógica; o Controller retorna HTTP/DTO.

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<SubscriptionPlanResponseDTO> create(@Valid @RequestBody SubscriptionPlanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionPlanService.create(dto));
    }

    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponseDTO>> findAll() {
        return ResponseEntity.ok(subscriptionPlanService.findAll());
    }

    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> findById(@PathVariable int planId) {
        return ResponseEntity.ok(subscriptionPlanService.findById(planId));
    }

    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> update(@PathVariable int planId,
                                                              @Valid @RequestBody SubscriptionPlanRequestDTO dto) {
        return ResponseEntity.ok(subscriptionPlanService.update(planId, dto));
    }

    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> delete(@PathVariable int planId) {
        subscriptionPlanService.delete(planId);
        return ResponseEntity.noContent().build();
    }
}