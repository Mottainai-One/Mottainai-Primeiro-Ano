package com.mottainai.mottainaicrud.service;

import org.springframework.stereotype.Service;
import com.mottainai.mottainaicrud.model.Administration;
import com.mottainai.mottainaicrud.dao.AdministrationDao;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.GlobalExceptionHandler;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;


@Service
public class AdministrationService {

    private final AdministrationDao administrationDao;

    public AdministrationService(AdministrationDao administrationDao) {
        this.administrationDao = administrationDao;
    }

    private AdministrationResponseDTO toResponseDTO(Administration admin) {
        AdministrationResponseDTO dto = new AdministrationResponseDTO();
        dto.setAdministrationId(admin.getAdministrationId());
        dto.setEmail(admin.getEmail());
        return dto;
    }
}
