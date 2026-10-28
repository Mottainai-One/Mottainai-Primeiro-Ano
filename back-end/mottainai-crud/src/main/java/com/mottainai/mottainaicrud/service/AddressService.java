package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AddressDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Address;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    // ACESSO AOS DADOS DE ENDERECOS
    private final AddressDao addressDao;

    // ACESSO AOS DADOS DE LOJAS
    private final StoreDao storeDao;

    public AddressService(AddressDao addressDao, StoreDao storeDao) {
        this.addressDao = addressDao;
        this.storeDao = storeDao;
    }

    // LISTA TODOS OS ENDERECOS
    public List<AddressResponseDTO> findAll() {
        return addressDao.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // BUSCA ENDERECO PELO ID
    public AddressResponseDTO findById(Integer addressId) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        return toResponseDTO(address);
    }

    // CONVERTE DTO PARA ENTIDADE
    private Address toEntity(AddressRequestDTO dto) {
        Address address = new Address();
        address.setZipCode(dto.getZipCode());
        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setNeighborhood(dto.getNeighborhood());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        return address;
    }

    // CONVERTE ENTIDADE PARA DTO DE RESPOSTA
    private AddressResponseDTO toResponseDTO(Address address) {
        AddressResponseDTO dto = new AddressResponseDTO();
        dto.setAddressId(address.getAddressId());
        dto.setZipCode(address.getZipCode());
        dto.setStreet(address.getStreet());
        dto.setNumber(address.getNumber());
        dto.setComplement(address.getComplement());
        dto.setNeighborhood(address.getNeighborhood());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setCreatedAt(address.getCreatedAt());
        return dto;
    }

    public AddressResponseDTO create(AddressRequestDTO dto) {

        // CONVERTE O DTO PARA ENTIDADE
        Address address = toEntity(dto);

        // SALVA E RETORNA A RESPOSTA
        Address saved = addressDao.save(address);
        return toResponseDTO(saved);
    }

    public AddressResponseDTO update(
            Integer addressId,
            AddressRequestDTO dto
    ) {
        // BUSCA O ENDERECO EXISTENTE
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Endereço não encontrado."
                ));

        // ATUALIZA OS DADOS DO ENDERECO
        address.setZipCode(dto.getZipCode());
        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setNeighborhood(dto.getNeighborhood());
        address.setCity(dto.getCity());
        address.setState(dto.getState());

        // SALVA AS ALTERACOES
        Address updated = addressDao.save(address);
        return toResponseDTO(updated);
    }
}