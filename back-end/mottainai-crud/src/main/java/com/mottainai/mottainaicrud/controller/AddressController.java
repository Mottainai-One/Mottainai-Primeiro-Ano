package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE ENDEREÇOS — /api/addresses
// Recebe JSON validado e IDs pela URL; o Service concentra vínculos, anonimização e exclusão lógica.

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<AddressResponseDTO> create(@Valid @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.create(dto));
    }

    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<AddressResponseDTO>> findAll() {
        return ResponseEntity.ok(addressService.findAll());
    }

    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> findById(@PathVariable Integer addressId) {
        return ResponseEntity.ok(addressService.findById(addressId));
    }

    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDTO> update(@PathVariable Integer addressId,
                                                     @Valid @RequestBody AddressRequestDTO dto) {
        return ResponseEntity.ok(addressService.update(addressId, dto));
    }

    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> delete(@PathVariable Integer addressId) {
        addressService.delete(addressId);
        return ResponseEntity.noContent().build();
    }
}