package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.StoreRequestDTO;
import com.mottainai.mottainaicrud.dto.StoreResponseDTO;
import com.mottainai.mottainaicrud.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    private final StoreService storeService;

    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // CREATE - POST /api/stores
    @PostMapping
    public ResponseEntity<StoreResponseDTO> create(@Valid @RequestBody StoreRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(storeService.create(dto));
    }

    // READ (TODOS) - GET /api/stores
    @GetMapping
    public ResponseEntity<List<StoreResponseDTO>> findAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    // READ (POR STORE ID) - GET /api/stores/{storeId}
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> findById(@PathVariable int storeId) {
        return ResponseEntity.ok(storeService.findById(storeId));
    }

    // UPDATE - PUT /api/stores/{storeId}
    @PutMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> update(@PathVariable int storeId,
                                                   @Valid @RequestBody StoreRequestDTO dto) {
        return ResponseEntity.ok(storeService.update(storeId, dto));
    }

    // DELETE - DELETE /api/stores/{storeId}
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> delete(@PathVariable int storeId) {
        storeService.delete(storeId);
        return ResponseEntity.noContent().build();
    }
}