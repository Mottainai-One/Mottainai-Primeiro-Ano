package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AddressDao;
import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.StoreRequestDTO;
import com.mottainai.mottainaicrud.dto.StoreResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Address;
import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.Store;
import org.springframework.stereotype.Service;

@Service
public class StoreService {

    private final StoreDao storeDao;
    private final CompanyDao companyDao;
    private final AddressDao addressDao;

    public StoreService(
            StoreDao storeDao,
            CompanyDao companyDao,
            AddressDao addressDao
    ) {
        this.storeDao = storeDao;
        this.companyDao = companyDao;
        this.addressDao = addressDao;
    }

    public List<StoreResponseDTO> findAll() {
        return storeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public StoreResponseDTO findById(int storeId) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        return toResponseDTO(store);
    }

    private StoreResponseDTO toResponseDTO(Store store) {
        StoreResponseDTO dto = new StoreResponseDTO();
        dto.setStoreId(store.getStoreId());
        dto.setName(store.getName());
        dto.setCnpj(store.getCnpj());
        dto.setEmail(store.getEmail());
        dto.setPhone(store.getPhone());
        dto.setCompanyId(store.getCompany().getCompanyId());
        dto.setAddressId(store.getAddress().getAddressId());
        return dto;
    }

    public StoreResponseDTO create(StoreRequestDTO dto) {
        if (storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma loja cadastrada com esse CNPJ."
            );
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa não encontrada."
                ));

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        Store store = new Store();
        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        store.setEmail(dto.getEmail());
        store.setPhone(dto.getPhone());
        store.setCompany(company);
        store.setAddress(address);

        Store saved = storeDao.save(store);
        return toResponseDTO(saved);
    }

    public StoreResponseDTO update(int storeId, StoreRequestDTO dto) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        if (!store.getCnpj().equals(dto.getCnpj())
                && storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma loja cadastrada com esse CNPJ."
            );
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa não encontrada."
                ));

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        if (storeDao.existsByAddressAndStoreIdNot(address, storeId)) {
            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        store.setEmail(dto.getEmail());
        store.setPhone(dto.getPhone());
        store.setCompany(company);
        store.setAddress(address);

        Store updated = storeDao.save(store);
        return toResponseDTO(updated);
    }

    public void delete(int storeId) {
        if (!storeDao.existsById(storeId)) {
            throw new ResourceNotFoundException(
                    "Loja não encontrada."
            );
        }

        storeDao.deleteById(storeId);
    }
}
