package com.mottainai.crudinicial.service;

import com.mottainai.crudinicial.dao.EmployeeDao;
import com.mottainai.crudinicial.dao.EmployeeRoleDao;
import com.mottainai.crudinicial.dto.EmployeeRoleRequestDTO;
import com.mottainai.crudinicial.dto.EmployeeRoleResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.model.EmployeeRole;
import com.mottainai.crudinicial.security.DataAnonymizationService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import com.mottainai.crudinicial.exception.BusinessRuleException;

// REGRAS DO CADASTRO DE CARGOS
// Protege o cargo técnico e os cargos em uso; fornece contagens agregadas de funcionários para a listagem.

@Service
public class EmployeeRoleService {

    private final EmployeeRoleDao employeeRoleDao;
    private final EmployeeDao employeeDao;
    private final DataAnonymizationService anonymizationService;

    public EmployeeRoleService(EmployeeRoleDao employeeRoleDao, EmployeeDao employeeDao, DataAnonymizationService anonymizationService) {
        this.employeeRoleDao = employeeRoleDao;
        this.employeeDao = employeeDao;
        // Serviço usado quando o cargo puder ser excluído logicamente
        // e seus dados precisarem perder relação com o valor original.
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO SEM NOME DUPLICADO
    // Confere a existência do nome antes de persistir o cargo e retornar seus dados de consulta.
    public EmployeeRoleResponseDTO create(EmployeeRoleRequestDTO dto) {
        if (employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Já existe um cargo com esse nome.");
        }

        EmployeeRole role = toEntity(dto);
        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        role.setActive(dto.getActive());
        EmployeeRole saved = employeeRoleDao.save(role);
        return toResponseDTO(saved);
    }

    // LISTAGEM COM CONTAGEM AGREGADA
    // O mapa por roleId evita countByRole em cada linha; o cargo técnico é retirado por ID/nome.
    public List<EmployeeRoleResponseDTO> findAll() {
        var counts = employeeDao.countGroupedByRole().stream().collect(java.util.stream.Collectors.toMap(
                com.mottainai.crudinicial.dao.GroupedCount::getId,
                com.mottainai.crudinicial.dao.GroupedCount::getTotal));
        return employeeRoleDao.findAll()
                .stream()
                .filter(role ->
                        role.getRoleId() != TECHNICAL_RECORD_ID
                                && !TECHNICAL_ROLE_NAME.equals(
                                role.getName()
                        )
                )
                .map(role -> toResponseDTO(role, counts.getOrDefault(role.getRoleId(), 0L)))
                .toList();
    }

    // CONSULTA COM PROTEÇÃO DO CARGO TÉCNICO
    // O registro reservado não é oferecido como detalhe de cargo comum, mesmo quando seu ID é informado.
    public EmployeeRoleResponseDTO findById(int roleId) {

        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cargo não encontrado."
                        )
                );

        if (roleId == TECHNICAL_RECORD_ID
                || TECHNICAL_ROLE_NAME.equals(role.getName())) {

            throw new ResourceNotFoundException(
                    "Cargo não encontrado."
            );
        }

        return toResponseDTO(role);
    }

    // EDIÇÃO PROTEGIDA DO CARGO
    // Bloqueia o nome reservado e o histórico excluído; verifica duplicidade se o nome for alterado.
    public EmployeeRoleResponseDTO update(int roleId, EmployeeRoleRequestDTO dto) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cargo não encontrado."));

        // Este cargo é reservado para funcionários
        // que passaram pelo processo de anonimização.
        if (TECHNICAL_ROLE_NAME.equals(
                role.getName()
        )) {
            throw new BusinessRuleException(
                    "O cargo técnico de anonimização não pode ser editado."
            );
        }

        // Um cargo excluído logicamente não pode ser editado novamente.
        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar um cargo excluído."
            );
        }

        if (!role.getName().equals(dto.getName()) && employeeRoleDao.existsByName(dto.getName())) {
            throw new DuplicateResourceException("Já existe um cargo com esse nome.");
        }

        role.setName(dto.getName());
        role.setDescription(dto.getDescription());
        role.setPermissionLevel(dto.getPermissionLevel());
        role.setActive(dto.getActive());

        EmployeeRole updated = employeeRoleDao.save(role);
        return toResponseDTO(updated);
    }

    @Transactional
    // EXCLUSÃO LÓGICA DO CARGO
    // Não exclui cargo técnico ou ainda usado por Employee; preserva o nome e substitui descrição/valores operacionais.
    public void delete(int roleId) {
        EmployeeRole role = employeeRoleDao.findById(roleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cargo não encontrado."
                        )
                );

        if (role.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Este cargo já foi excluído."
            );
        }

        // O cargo técnico precisa permanecer no banco
        // para receber funcionários anonimizados.
        if (TECHNICAL_ROLE_NAME.equals(
                role.getName()
        )) {
            throw new BusinessRuleException(
                    "O cargo técnico de anonimização não pode ser excluído."
            );
        }

        // Mantém a proteção que já existia:
        // um cargo ainda utilizado por funcionários não pode
        // ser excluído diretamente.
        if (employeeDao.existsByRole(role)) {
            throw new DuplicateResourceException(
                    "Não é possível excluir um cargo associado a funcionários."
            );
        }
        // O nome é mantido como referência histórica.
        // A descrição deixa de guardar o conteúdo original.
        role.setDescription(anonymizationService.anonymize());
        // O nível de permissão perde significado operacional,
        // mas continua respeitando a constraint PermissionLevel > 0.
        role.setPermissionLevel(1);
        // Exclusão lógica: a linha continua existindo no banco.
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

    // CONVERSÃO DO DETALHE COM CONTAGEM
    // No detalhe há uma única contagem; a listagem passa as contagens já agregadas para a sobrecarga abaixo.
    private EmployeeRoleResponseDTO toResponseDTO(
            EmployeeRole role) {
        return toResponseDTO(role, employeeDao.countByRole(role));
    }

    // DTO COM CONTAGEM RESOLVIDA
    // Usa employeeCount recebido para impedir nova consulta dentro do mapeamento de cada cargo.
    private EmployeeRoleResponseDTO toResponseDTO(EmployeeRole role, long employeeCount) {

        EmployeeRoleResponseDTO dto =
                new EmployeeRoleResponseDTO();

        dto.setRoleId(role.getRoleId());

        dto.setName(role.getName());
        dto.setDescription(role.getDescription());

        dto.setPermissionLevel(
                role.getPermissionLevel()
        );

        dto.setActive(role.isActive());

        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        dto.setDeletedAt(role.getDeletedAt());

        dto.setEmployeeCount(
                employeeCount
        );

        return dto;
    }
}
