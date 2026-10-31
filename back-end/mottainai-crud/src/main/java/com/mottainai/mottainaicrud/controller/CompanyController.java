package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.CompanyRequestDTO;
import com.mottainai.mottainaicrud.dto.CompanyResponseDTO;
import com.mottainai.mottainaicrud.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // CREATE
    // POST /api/companies
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> create(@Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO created = companyService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ (TODOS)
    // GET /api/companies
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> findAll() {
        List<CompanyResponseDTO> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    // READ (por company id)
    // GET /api/companies/{companyId}
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> findById(@PathVariable int companyId) {
        return ResponseEntity.ok(
                companyService.findByCompanyId(companyId)
        );
    }

    // READ (POR OFFICIAL NAME)
    // GET /api/companies/official-name/{officialName}
    @GetMapping("/official-name/{officialName}")
    public ResponseEntity<List<CompanyResponseDTO>> findByOfficialName(
            @PathVariable String officialName) {

        List<CompanyResponseDTO> companies =
                companyService.findByOfficialName(officialName);

        return ResponseEntity.ok(companies);
    }

    //READ (por trade name)
    // GET /api/companies/trade-name/{tradeName}
    @GetMapping("/trade-name/{tradeName}")
    public ResponseEntity<List<CompanyResponseDTO>> findByTradeName(@PathVariable String tradeName) {
        List<CompanyResponseDTO> companies = companyService.findByTradeName(tradeName);
        return ResponseEntity.ok(companies);
    }

    // UPDATE
    // PUT /api/companies/{companyId}
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> update(@PathVariable int companyId, @Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO updated = companyService.update(companyId, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/companies/{companyId}
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> delete(@PathVariable int companyId) {
        companyService.delete(companyId);
        return ResponseEntity.noContent().build();
    }

}