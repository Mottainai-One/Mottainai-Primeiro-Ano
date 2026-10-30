package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dto.EmployeeRoleRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeRoleResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.EmployeeRole;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE CARGOS
// PROTEGE O CARGO TECNICO E OS CARGOS EM USO; CONTAGEM AGREGADA DE FUNCIONARIOS NA LISTAGEM

@Service
public class EmployeeRoleService {

    private final EmployeeRoleDao employeeRoleDao;
    private final EmployeeDao employeeDao;
    private final DataAnonymizationService anonymizationService;

    public EmployeeRoleService(EmployeeRoleDao employeeRoleDao,
                               EmployeeDao employeeDao,
                               DataAnonymizationService anonymizationService) {
        this.employeeRoleDao = employeeRoleDao;
        this.employeeDao = employeeDao;
        // SERVICO USADO NO SOFT DELETE DO CARGO
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO — NOME DUPLICADO GERA CONFLITO
    public EmployeeRoleResponseDTO create(EmployeeRoleRequestDTO dto) {
        if (employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ja existe um cargo com esse nome.");
        }

        EmployeeRole role = toEntity(dto);
        role.setActive(dto.getActive());
        EmployeeRole saved = employeeRoleDao.save(role);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM O CARGO TECNICO; CONTAGENS AGREGADAS PARA EVITAR N+1
    public List<EmployeeRoleResponseDTO> findAll() {
        var counts = employeeDao.countGroupedByRole()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.mottainai.mottainaicrud.dao.GroupedCount::getId,
                        com.mottainai.mottainaicrud.dao.GroupedCount::getTotal));
        return employeeRoleDao.findAll()
                .stream()
                .filter(role ->
                        role.getRoleId() != TECHNICAL_RECORD_ID
                                && !TECHNICAL_ROLE_NAME.equals(role.getName())
                )
                .map(role -> toResponseDTO(role, counts.getOrDefault(role.getRoleId(), 0L)))
                .toList();
    }

    // CONSULTA — CARGO TECNICO TRATADO COMO NAO ENCONTRADO
    public EmployeeRoleResponseDTO findById(int roleId) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo nao encontrado."));

        // CARGO TECNICO NAO E EXPOSTO COMO CARGO COMUM
        if (roleId == TECHNICAL_RECORD_ID || TECHNICAL_ROLE_NAME.equals(role.getName())) {
            throw new ResourceNotFoundException("Cargo nao encontrado.");
        }

        return toResponseDTO(role);
    }

    // EDICAO — BLOQUEIA CARGO TECNICO E JA EXCLUIDOS; VERIFICA NOME DUPLICADO SE ALTERADO
    public EmployeeRoleResponseDTO update(int roleId, EmployeeRoleRequestDTO dto) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo nao encontrado."));

        // CARGO TECNICO DE ANONIMIZACAO NAO PODE SER EDITADO
        if (TECHNICAL_ROLE_NAME.equals(role.getName())) {
            throw new BusinessRuleException("O cargo tecnico de anonimizacao nao pode ser editado.");
        }

        // CARGO COM SOFT DELETE NAO PODE SER EDITADO NOVAMENTE
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar um cargo excluido.");
        }

        if (!role.getName().equals(dto.getName())
                && employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Ja existe um cargo com esse nome.");
        }

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        role.setActive(dto.getActive());

        EmployeeRole updated = employeeRoleDao.save(role);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE — CARGO TECNICO E CARGO EM USO BLOQUEADOS; NOME PERMANECE COMO HISTORICO
    public void delete(int roleId) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo nao encontrado."));

        // IMPEDE DUPLA EXCLUSAO
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException("Este cargo ja foi excluido.");
        }

        // CARGO TECNICO PRECISA PERMANECER PARA RECEBER FUNCIONARIOS ANONIMIZADOS
        if (TECHNICAL_ROLE_NAME.equals(role.getName())) {
            throw new BusinessRuleException("O cargo tecnico de anonimizacao nao pode ser excluido.");
        }

        // CARGO AINDA UTILIZADO POR FUNCIONARIOS NAO PODE SER EXCLUIDO
        if (employeeDao.existsByRole(role)) {
            throw new DuplicateResourceException("Nao e possivel excluir um cargo associado a funcionarios.");
        }

        // NOME MANTIDO COMO REFERENCIA HISTORICA; DESCRICAO ANONIMIZADA
        role.setDescription(anonymizationService.anonymize());
        // NIVEL MINIMO QUE RESPEITA A CONSTRAINT PERMISSIONLEVEL > 0
        role.setPermissionLevel(1);
        role.setActive(false);
        role.setDeletedAt(LocalDateTime.now());
        employeeRoleDao.save(role);
    }

    private EmployeeRole toEntity(EmployeeRoleRequestDTO dto) {
        EmployeeRole role = new EmployeeRole();
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        return role;
    }

    // DETALHE: CONTAGEM INDIVIDUAL DE FUNCIONARIOS DO CARGO
    private EmployeeRoleResponseDTO toResponseDTO(EmployeeRole role) {
        return toResponseDTO(role, employeeDao.countByRole(role));
    }

    // DTO COM CONTAGEM JA CALCULADA — EVITA CONSULTA ADICIONAL NA LISTAGEM
    private EmployeeRoleResponseDTO toResponseDTO(EmployeeRole role, long employeeCount) {
        EmployeeRoleResponseDTO dto = new EmployeeRoleResponseDTO();
        dto.setRoleId(role.getRoleId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissionLevel(role.getPermissionLevel());
        dto.setActive(role.isActive());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        dto.setDeletedAt(role.getDeletedAt());
        dto.setEmployeeCount(employeeCount);
        return dto;
    }
}
