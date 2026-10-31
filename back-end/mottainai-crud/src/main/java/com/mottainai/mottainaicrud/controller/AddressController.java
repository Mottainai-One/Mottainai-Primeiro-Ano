package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // CREATE - POST /api/addresses
    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@Valid @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(dto));
    }

    // READ (TODOS) - GET /api/addresses
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    // READ (POR ADDRESS ID) - GET /api/addresses/{addressId}
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable Integer addressId) {
        return ResponseEntity.ok(addressService.findById(addressId));
    }

    // UPDATE - PUT /api/addresses/{addressId}
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> update(@PathVariable Integer addressId,
                                                     @Valid @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.update(addressId, dto));
    }

    // DELETE - DELETE /api/addresses/{addressId}
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Integer addressId) {
        addressService.delete(addressId);
        return ResponseEntity.noContent().build();
    }
}