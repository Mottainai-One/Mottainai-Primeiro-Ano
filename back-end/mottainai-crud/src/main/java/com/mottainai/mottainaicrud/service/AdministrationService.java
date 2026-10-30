package com.mottainai.mottainaicrud.service;

import org.springframework.stereotype.Service;
import com.mottainai.mottainaicrud.model.Administration;
import com.mottainai.mottainaicrud.dao.AdministrationDao;
import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.GlobalExceptionHandler;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;

import java.util.List;


@Service
public class AdministrationService {

    // ACESSO AOS DADOS DE ADMINISTRACAO
    private final AdministrationDao administrationDao;

    public AdministrationService(AdministrationDao administrationDao) {
        this.administrationDao = administrationDao;
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private AdministrationResponseDTO toResponseDTO(Administration admin) {
        AdministrationResponseDTO dto = new AdministrationResponseDTO();
        dto.setAdministrationId(admin.getAdministrationId());
        dto.setEmail(admin.getEmail());
        return dto;
    }

    // LISTA TODAS AS ADMINISTRACOES
    public List<AdministrationResponseDTO> findAll() {
        return administrationDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA ADMINISTRACAO PELO ID
    public AdministrationResponseDTO findById(int id) {
        // BUSCA O REGISTRO EXISTENTE
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Administração não encontrada."
                ));

        return toResponseDTO(admin);
    }

    public AdministrationResponseDTO create(AdministrationRequestDTO dto) {
        // VALIDA SENHA OBRIGATORIA
        if (dto.getPasswordHash() == null || dto.getPasswordHash().isBlank()) {
            throw new IllegalArgumentException("Senha obrigatória.");
        }

        // EVITA EMAIL DUPLICADO
        if (administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Já existe uma administração com este e-mail."
            );
        }

        // MONTA A ENTIDADE PARA SALVAR
        Administration admin = new Administration();
        admin.setEmail(dto.getEmail());
        // TODO: APLICAR HASH NA SENHA
        admin.setPasswordHash(dto.getPasswordHash());

        // SALVA E RETORNA RESPOSTA
        Administration saved = administrationDao.save(admin);
        return toResponseDTO(saved);
    }

    public AdministrationResponseDTO update(int id, AdministrationRequestDTO dto) {
        Administration admin = administrationDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Administração não encontrada."
                ));

        // VALIDA EMAIL AO ALTERAR
        if (!admin.getEmail().equals(dto.getEmail())
                && administrationDao.existsByEmail(dto.getEmail())) {
            throw new DuplicateResourceException(
                    "Já existe uma administração com este e-mail."
            );
        }

        admin.setEmail(dto.getEmail());

        // ALTERA A SENHA SE INFORMADA
        if (dto.getPasswordHash() != null && !dto.getPasswordHash().isBlank()) {
            admin.setPasswordHash(dto.getPasswordHash());
        }

        // SALVA AS ALTERACOES
        Administration updated = administrationDao.save(admin);
        return toResponseDTO(updated);
    }

    // REMOVE ADMINISTRACAO PELO ID
    public void delete(int id) {
        if (!administrationDao.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Administração não encontrada."
            );
        }

        administrationDao.deleteById(id);
    }
}
