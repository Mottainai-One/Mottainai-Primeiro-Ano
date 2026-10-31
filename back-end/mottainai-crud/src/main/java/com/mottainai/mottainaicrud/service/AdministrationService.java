package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AdministrationDao;
import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Administration;
import com.mottainai.mottainaicrud.security.PasswordHashService;

import org.springframework.stereotype.Service;

import java.util.List;

// GESTÃO RESTRITA: cadastro/edição e exclusão de outras contas preservam o login existente.

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class AdministrationService {

    private final AdministrationDao administrationDao;
    private final PasswordHashService passwordHashService;

    public AdministrationService(AdministrationDao administrationDao, PasswordHashService passwordHashService) {
        this.administrationDao = administrationDao;
        // Serviço responsável por gerar e validar
        // hashes irreversíveis das senhas dos administradores.
        this.passwordHashService = passwordHashService;
    }

    // CADASTRO COM BCRYPT: somente o hash chega à entidade e ao banco.
    @org.springframework.transaction.annotation.Transactional
    public AdministrationResponseDTO create(AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());
        if (dto.getPassword() == null
                || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Senha obrigatória."
            );
        }
        if (administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Já existe uma administração com este e-mail.");
        }

        Administration admin = new Administration();
        admin.setEmail(dto.getEmail());
        // A senha nunca é persistida em texto puro.
        // Apenas o hash BCrypt fica armazenado no banco.
        admin.setPasswordHash(
                passwordHashService.hash(dto.getPassword())
        );

        Administration saved = administrationDao.save(admin);
        return toResponseDTO(saved);
    }

    // CONSULTA SEM HASH
    // As entidades são convertidas para AdministrationResponseDTO, que entrega somente identificação e email.
    public List<AdministrationResponseDTO> findAll() {
        return administrationDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE SEM CREDENCIAIS
    // A autenticação continua exclusivamente no AdministrationAuthService.
    public AdministrationResponseDTO findById(int id) {
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));
        return toResponseDTO(admin);
    }

    // EDIÇÃO DAS CREDENCIAIS
    // Email duplicado é rejeitado e senha vazia preserva o hash; uma nova senha recebe BCrypt.
    @org.springframework.transaction.annotation.Transactional
    public AdministrationResponseDTO update(int id, AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));

        if (!admin.getEmail().equals(dto.getEmail())
                && administrationDao.existsByEmail(dto.getEmail())) {

            throw new DuplicateResourceException(
                    "Já existe uma administração com este e-mail."
            );
        }

        admin.setEmail(dto.getEmail());
        if (dto.getPassword() != null
                && !dto.getPassword().isBlank()) {

            admin.setPasswordHash(
                    passwordHashService.hash(dto.getPassword())
            );
        }

        Administration updated = administrationDao.save(admin);
        return toResponseDTO(updated);
    }

    // A sessão de uma conta removida deixa de liberar os próximos acessos protegidos.
    public boolean exists(int id) {
        return administrationDao.existsById(id);
    }

    public AdministrationResponseDTO findForDeletion(int id, Integer authenticatedId) {
        return toResponseDTO(deletionTarget(id, authenticatedId));
    }

    // Exclusão física autorizada somente para Administration, sem alterar o schema.
    @org.springframework.transaction.annotation.Transactional
    public void delete(int id, Integer authenticatedId) {
        administrationDao.delete(deletionTarget(id, authenticatedId));
    }

    // O ID de quem solicita vem da sessão, nunca de parâmetro enviado pelo formulário.
    private Administration deletionTarget(int id, Integer authenticatedId) {
        if (authenticatedId == null) {
            throw new com.mottainai.mottainaicrud.exception.BusinessRuleException("Sessão administrativa inválida.");
        }
        if (Integer.valueOf(id).equals(authenticatedId)) {
            throw new com.mottainai.mottainaicrud.exception.BusinessRuleException("Você não pode excluir a própria conta de administrador.");
        }
        return administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));
    }

    // BCrypt limita a entrada em bytes; senhas multibyte não devem ser truncadas.
    private void validatePassword(String password) {
        if (password != null && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Senha deve ter até 72 bytes em UTF-8.");
        }
    }

    // LIMITE DOS DADOS DE SAÍDA
    // O hash é dado de credencial e não é copiado da entidade para a resposta.
    private AdministrationResponseDTO toResponseDTO(Administration admin) {
        AdministrationResponseDTO dto = new AdministrationResponseDTO();

        dto.setAdministrationId(admin.getAdministrationId());
        dto.setEmail(admin.getEmail());

        return dto;
    }
}package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AdministrationDao;
import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Administration;
import com.mottainai.mottainaicrud.security.PasswordHashService;

import org.springframework.stereotype.Service;

import java.util.List;

// GESTÃO RESTRITA: cadastro/edição e exclusão de outras contas preservam o login existente.

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class AdministrationService {

    private final AdministrationDao administrationDao;
    private final PasswordHashService passwordHashService;

    public AdministrationService(AdministrationDao administrationDao, PasswordHashService passwordHashService) {
        this.administrationDao = administrationDao;
        // Serviço responsável por gerar e validar
        // hashes irreversíveis das senhas dos administradores.
        this.passwordHashService = passwordHashService;
    }

    // CADASTRO COM BCRYPT: somente o hash chega à entidade e ao banco.
    @org.springframework.transaction.annotation.Transactional
    public AdministrationResponseDTO create(AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());
        if (dto.getPassword() == null
                || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException(
                    "Senha obrigatória."
            );
        }
        if (administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException("Já existe uma administração com este e-mail.");
        }

        Administration admin = new Administration();
        admin.setEmail(dto.getEmail());
        // A senha nunca é persistida em texto puro.
        // Apenas o hash BCrypt fica armazenado no banco.
        admin.setPasswordHash(
                passwordHashService.hash(dto.getPassword())
        );

        Administration saved = administrationDao.save(admin);
        return toResponseDTO(saved);
    }

    // CONSULTA SEM HASH
    // As entidades são convertidas para AdministrationResponseDTO, que entrega somente identificação e email.
    public List<AdministrationResponseDTO> findAll() {
        return administrationDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE SEM CREDENCIAIS
    // A autenticação continua exclusivamente no AdministrationAuthService.
    public AdministrationResponseDTO findById(int id) {
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));
        return toResponseDTO(admin);
    }

    // EDIÇÃO DAS CREDENCIAIS
    // Email duplicado é rejeitado e senha vazia preserva o hash; uma nova senha recebe BCrypt.
    @org.springframework.transaction.annotation.Transactional
    public AdministrationResponseDTO update(int id, AdministrationRequestDTO dto) {
        validatePassword(dto.getPassword());
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));

        if (!admin.getEmail().equals(dto.getEmail())
                && administrationDao.existsByEmail(dto.getEmail())) {

            throw new DuplicateResourceException(
                    "Já existe uma administração com este e-mail."
            );
        }

        admin.setEmail(dto.getEmail());
        if (dto.getPassword() != null
                && !dto.getPassword().isBlank()) {

            admin.setPasswordHash(
                    passwordHashService.hash(dto.getPassword())
            );
        }

        Administration updated = administrationDao.save(admin);
        return toResponseDTO(updated);
    }

    // A sessão de uma conta removida deixa de liberar os próximos acessos protegidos.
    public boolean exists(int id) {
        return administrationDao.existsById(id);
    }

    public AdministrationResponseDTO findForDeletion(int id, Integer authenticatedId) {
        return toResponseDTO(deletionTarget(id, authenticatedId));
    }

    // Exclusão física autorizada somente para Administration, sem alterar o schema.
    @org.springframework.transaction.annotation.Transactional
    public void delete(int id, Integer authenticatedId) {
        administrationDao.delete(deletionTarget(id, authenticatedId));
    }

    // O ID de quem solicita vem da sessão, nunca de parâmetro enviado pelo formulário.
    private Administration deletionTarget(int id, Integer authenticatedId) {
        if (authenticatedId == null) {
            throw new com.mottainai.mottainaicrud.exception.BusinessRuleException("Sessão administrativa inválida.");
        }
        if (Integer.valueOf(id).equals(authenticatedId)) {
            throw new com.mottainai.mottainaicrud.exception.BusinessRuleException("Você não pode excluir a própria conta de administrador.");
        }
        return administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Administração não encontrada."));
    }

    // BCrypt limita a entrada em bytes; senhas multibyte não devem ser truncadas.
    private void validatePassword(String password) {
        if (password != null && password.getBytes(java.nio.charset.StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Senha deve ter até 72 bytes em UTF-8.");
        }
    }

    // LIMITE DOS DADOS DE SAÍDA
    // O hash é dado de credencial e não é copiado da entidade para a resposta.
    private AdministrationResponseDTO toResponseDTO(Administration admin) {
        AdministrationResponseDTO dto = new AdministrationResponseDTO();

        dto.setAdministrationId(admin.getAdministrationId());
        dto.setEmail(admin.getEmail());

        return dto;
    }
}