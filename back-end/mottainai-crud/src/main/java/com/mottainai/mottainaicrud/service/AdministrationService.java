package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AdministrationDao;
import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Administration;
import com.mottainai.mottainaicrud.security.PasswordHashService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// GESTAO RESTRITA DE CONTAS ADMINISTRATIVAS
// CRUD COM BCRYPT; EXCLUSAO FISICA AUTORIZADA APENAS PARA ADMINS

@Service
@Transactional(readOnly = true)
public class AdministrationService {

    private final AdministrationDao administrationDao;
    private final PasswordHashService passwordHashService;

    public AdministrationService(AdministrationDao administrationDao,
                                 PasswordHashService passwordHashService) {
        this.administrationDao = administrationDao;
        // SERVICO DE HASH IRREVERSIVEL PARA SENHAS DE ADMIN
        this.passwordHashService = passwordHashService;
    }

    // CADASTRO COM BCRYPT — SOMENTE O HASH CHEGA AO BANCO
    @Transactional
    public AdministrationResponseDTO create(AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatoria.");
        }

        if (administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Ja existe uma administracao com este e-mail.");
        }

        Administration admin = new Administration();
        admin.setEmail(dto.getEmail());
        // SENHA NUNCA PERSISTIDA EM TEXTO PURO — APENAS O HASH BCRYPT
        admin.setPasswordHash(passwordHashService.hash(dto.getPassword()));

        Administration saved = administrationDao.save(admin);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM HASH NAS RESPOSTAS
    public List<AdministrationResponseDTO> findAll() {
        return administrationDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE SEM CREDENCIAIS — AUTENTICACAO FICA NO AUTH SERVICE
    public AdministrationResponseDTO findById(int id) {
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administracao nao encontrada."));
        return toResponseDTO(admin);
    }

    // EDICAO DE EMAIL E SENHA — SENHA VAZIA PRESERVA O HASH ATUAL
    @Transactional
    public AdministrationResponseDTO update(int id, AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());

        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administracao nao encontrada."));

        if (!admin.getEmail().equals(dto.getEmail())
                && administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Ja existe uma administracao com este e-mail.");
        }

        admin.setEmail(dto.getEmail());

        // TROCA O HASH SOMENTE SE UMA NOVA SENHA FOR INFORMADA
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            admin.setPasswordHash(passwordHashService.hash(dto.getPassword()));
        }

        Administration updated = administrationDao.save(admin);
        return toResponseDTO(updated);
    }

    // VERIFICA SE A CONTA AINDA EXISTE PARA VALIDAR SESSAO
    public boolean exists(int id) {
        return administrationDao.existsById(id);
    }

    public AdministrationResponseDTO findForDeletion(int id, Integer authenticatedId) {
        return toResponseDTO(deletionTarget(id, authenticatedId));
    }

    // EXCLUSAO FISICA — PERMITIDA APENAS PARA ADMINS E NAO PARA SI MESMO
    @Transactional
    public void delete(int id, Integer authenticatedId) {
        administrationDao.delete(deletionTarget(id, authenticatedId));
    }
    @Transactional
    public void delete(int id) {
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administracao nao encontrada."));
        administrationDao.delete(admin);
    }

    // ID DO SOLICITANTE VEM DA SESSAO, NUNCA DO FORMULARIO
    private Administration deletionTarget(int id, Integer authenticatedId) {
        if (authenticatedId == null) {
            throw new BusinessRuleException("Sessao administrativa invalida.");
        }
        if (Integer.valueOf(id).equals(authenticatedId)) {
            throw new BusinessRuleException("Voce nao pode excluir a propria conta de administrador.");
        }
        return administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administracao nao encontrada."));
    }

    // BCRYPT LIMITA A ENTRADA EM 72 BYTES — SENHAS MAIORES SERIAM TRUNCADAS
    private void validatePassword(String password) {
        if (password != null
                && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Senha deve ter ate 72 bytes em UTF-8.");
        }
    }

    // HASH NAO E COPIADO PARA A RESPOSTA
    private AdministrationResponseDTO toResponseDTO(Administration admin) {
        AdministrationResponseDTO dto = new AdministrationResponseDTO();
        dto.setAdministrationId(admin.getAdministrationId());
        dto.setEmail(admin.getEmail());
        return dto;
    }
}
