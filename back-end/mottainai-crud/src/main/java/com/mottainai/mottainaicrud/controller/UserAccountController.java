package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.UserAccountRequestDTO;
import com.mottainai.mottainaicrud.dto.UserAccountResponseDTO;
import com.mottainai.mottainaicrud.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user-accounts")
public class UserAccountController {

    private final UserAccountService userAccountService;

    public UserAccountController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // CREATE - POST /api/user-accounts
    @PostMapping
    public ResponseEntity<UserAccountResponseDTO> create(@Valid @RequestBody UserAccountRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userAccountService.create(dto));
    }

    // READ (TODOS) - GET /api/user-accounts
    @GetMapping
    public ResponseEntity<List<UserAccountResponseDTO>> findAll() {
        return ResponseEntity.ok(userAccountService.findAll());
    }

    // READ (POR USER ID) - GET /api/user-accounts/{userId}
    @GetMapping("/{userId}")
    public ResponseEntity<UserAccountResponseDTO> findById(@PathVariable int userId) {
        return ResponseEntity.ok(userAccountService.findById(userId));
    }

    // UPDATE - PUT /api/user-accounts/{userId}
    @PutMapping("/{userId}")
    public ResponseEntity<UserAccountResponseDTO> update(@PathVariable int userId,
                                                         @Valid @RequestBody UserAccountRequestDTO dto) {
        return ResponseEntity.ok(userAccountService.update(userId, dto));
    }

    // DELETE - DELETE /api/user-accounts/{userId}
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable int userId) {
        userAccountService.delete(userId);
        return ResponseEntity.noContent().build();
    }
}