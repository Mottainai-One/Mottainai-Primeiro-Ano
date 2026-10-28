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

    // ACESSO AOS DADOS DE LOJAS
    private final StoreDao storeDao;
    // ACESSO AOS DADOS DE EMPRESAS
    private final CompanyDao companyDao;
    // ACESSO AOS DADOS DE ENDERECOS
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

    // LISTA TODAS AS LOJAS
    public List<StoreResponseDTO> findAll() {
        return storeDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA LOJA PELO ID
    public StoreResponseDTO findById(int storeId) {
        // BUSCA A LOJA EXISTENTE
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        return toResponseDTO(store);
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
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
        // VALIDA CNPJ UNICO
        if (storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma loja cadastrada com esse CNPJ."
            );
        }

        // BUSCA A EMPRESA VINCULADA
        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa não encontrada."
                ));

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        // VALIDA ENDERECO EXCLUSIVO
        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        // MONTA A ENTIDADE PARA SALVAR
        Store store = new Store();
        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        store.setEmail(dto.getEmail());
        store.setPhone(dto.getPhone());
        store.setCompany(company);
        store.setAddress(address);

        // SALVA E RETORNA A RESPOSTA
        Store saved = storeDao.save(store);
        return toResponseDTO(saved);
    }

    public StoreResponseDTO update(int storeId, StoreRequestDTO dto) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Loja não encontrada."
                ));

        // VALIDA CNPJ AO ALTERAR
        if (!store.getCnpj().equals(dto.getCnpj())
                && storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException(
                    "Já existe uma loja cadastrada com esse CNPJ."
            );
        }

        // BUSCA A EMPRESA INFORMADA
        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Empresa não encontrada."
                ));

        // BUSCA O ENDERECO INFORMADO
        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        // VALIDA ENDERECO EM OUTRA LOJA
        if (storeDao.existsByAddressAndStoreIdNot(address, storeId)) {
            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        // ATUALIZA OS DADOS DA LOJA
        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        store.setEmail(dto.getEmail());
        store.setPhone(dto.getPhone());
        store.setCompany(company);
        store.setAddress(address);

        Store updated = storeDao.save(store);
        return toResponseDTO(updated);
    }

    // REMOVE LOJA PELO ID
    public void delete(int storeId) {
        if (!storeDao.existsById(storeId)) {
            throw new ResourceNotFoundException(
                    "Loja não encontrada."
            );
        }

        storeDao.deleteById(storeId);
    }
}
