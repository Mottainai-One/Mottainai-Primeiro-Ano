package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.CompanyRequestDTO;
import com.mottainai.mottainaicrud.dto.CompanyResponseDTO;
import com.mottainai.mottainaicrud.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// API REST DE EMPRESAS — /api/companies
// Expõe o cadastro e consultas por ID, razão social ou nome fantasia; as respostas usam DTOs.

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // CREATE
    // POST /api/companies
    // CADASTRO COM ENTRADA VALIDADA
    // O RequestDTO recebe JSON e passa pelo Bean Validation; o Service aplica as regras antes da resposta 201.
    @PostMapping
    public ResponseEntity<CompanyResponseDTO> create(@Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO created = companyService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ (todos)
    // GET /api/companies
    // CONSULTA DA LISTAGEM
    // Retorna os DTOs preparados pelo Service; a política de exclusão/anonimização não é definida no Controller.
    @GetMapping
    public ResponseEntity<List<CompanyResponseDTO>> findAll() {
        List<CompanyResponseDTO> companies = companyService.findAll();
        return ResponseEntity.ok(companies);
    }

    // READ (por company id)
    // GET /api/companies/{companyId}
    // CONSULTA POR IDENTIFICADOR
    // O ID vem da URL; ausência de cadastro é comunicada por ResourceNotFoundException e tratada como 404.
    @GetMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> findById(@PathVariable int companyId) {
        return ResponseEntity.ok(
                companyService.findByCompanyId(companyId)
        );
    }

    // READ (por offical name)
    // GET /api/companies/official-name/{officialName}
    // CONSULTA ESPECÍFICA POR RAZÃO SOCIAL
    // Mantém a busca exata do Repository, distinta do filtro textual genérico da tela.
    @GetMapping("/official-name/{officialName}")
    public ResponseEntity<List<CompanyResponseDTO>> findByOfficialName(
            @PathVariable String officialName) {

        List<CompanyResponseDTO> companies =
                companyService.findByOfficialName(officialName);

        return ResponseEntity.ok(companies);
    }

    //READ (por trade name)
    // GET /api/companies/trade-name/{tradeName}
    // CONSULTA ESPECÍFICA POR NOME FANTASIA
    // A correlação com o plano é acrescentada pelo Service ao preparar cada DTO.
    @GetMapping("/trade-name/{tradeName}")
    public ResponseEntity<List<CompanyResponseDTO>> findByTradeName(@PathVariable String tradeName) {
        List<CompanyResponseDTO> companies = companyService.findByTradeName(tradeName);
        return ResponseEntity.ok(companies);
    }

    // UPDATE
    // PUT /api/companies/{companyId}
    // EDIÇÃO DO CADASTRO EXISTENTE
    // Combina o ID da URL com os dados validados; bloqueios de negócio continuam sob responsabilidade do Service.
    @PutMapping("/{companyId}")
    public ResponseEntity<CompanyResponseDTO> update(@PathVariable int companyId, @Valid @RequestBody CompanyRequestDTO dto) {
        CompanyResponseDTO updated = companyService.update(companyId, dto);
        return ResponseEntity.ok(updated);
    }

    // DELETE /api/companies/{companyId}
    // EXCLUSÃO LÓGICA PELO SERVICE
    // O verbo HTTP DELETE não remove a linha diretamente; o Service executa as proteções e retorna sem corpo (204).
    @DeleteMapping("/{companyId}")
    public ResponseEntity<Void> delete(@PathVariable int companyId) {
        companyService.delete(companyId);
        return ResponseEntity.noContent().build();
    }

}