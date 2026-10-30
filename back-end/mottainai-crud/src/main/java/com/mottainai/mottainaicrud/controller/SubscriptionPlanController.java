package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subscription-plans")
public class SubscriptionPlanController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    // CREATE - POST /api/subscription-plans
    @PostMapping
    public ResponseEntity<SubscriptionPlanResponseDTO> create(@Valid @RequestBody SubscriptionPlanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(subscriptionPlanService.create(dto));
    }

    // READ (TODOS) - GET /api/subscription-plans
    @GetMapping
    public ResponseEntity<List<SubscriptionPlanResponseDTO>> findAll() {
        return ResponseEntity.ok(subscriptionPlanService.findAll());
    }

    // READ (POR PLAN ID) - GET /api/subscription-plans/{planId}
    @GetMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> findById(@PathVariable int planId) {
        return ResponseEntity.ok(subscriptionPlanService.findById(planId));
    }

    // UPDATE - PUT /api/subscription-plans/{planId}
    @PutMapping("/{planId}")
    public ResponseEntity<SubscriptionPlanResponseDTO> update(@PathVariable int planId,
                                                              @Valid @RequestBody SubscriptionPlanRequestDTO dto) {
        return ResponseEntity.ok(subscriptionPlanService.update(planId, dto));
    }

    // DELETE - DELETE /api/subscription-plans/{planId}
    @DeleteMapping("/{planId}")
    public ResponseEntity<Void> delete(@PathVariable int planId) {
        subscriptionPlanService.delete(planId);
        return ResponseEntity.noContent().build();
    }
}