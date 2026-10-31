package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AddressDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Address;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE ENDERECOS
// PROTEGE O ENDERECO TECNICO E IMPEDE EXCLUSAO DIRETA DE ENDERECO OCUPADO POR LOJA

@Service
public class AddressService {

    private final AddressDao addressDao;
    private final StoreDao storeDao;
    private final DataAnonymizationService anonymizationService;

    public AddressService(AddressDao addressDao,
                          StoreDao storeDao,
                          DataAnonymizationService anonymizationService) {
        this.addressDao = addressDao;
        this.storeDao = storeDao;
        // SERVICO USADO PARA SUBSTITUIR DADOS DE FORMA IRREVERSIVEL NO SOFT DELETE
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO SIMPLES — VALIDACOES FICAM NO DTO
    public AddressResponseDTO create(AddressRequestDTO dto) {
        Address address = toEntity(dto);
        Address saved = addressDao.save(address);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM O REGISTRO TECNICO; NOMES DE LOJA CARREGADOS EM LOTE
    public List<AddressResponseDTO> findAll() {
        var storeNames = storeDao.findAddressStoreNames()
                .stream()
                .collect(java.util.stream.Collectors.toMap(
                        com.mottainai.mottainaicrud.dao.AddressStoreName::getAddressId,
                        com.mottainai.mottainaicrud.dao.AddressStoreName::getStoreName));
        return addressDao.findAll()
                .stream()
                .filter(address -> address.getAddressId() != TECHNICAL_RECORD_ID)
                .map(address -> toResponseDTO(address, storeNames.get(address.getAddressId())))
                .toList();
    }

    // CONSULTA — 404 SE AUSENTE
    public AddressResponseDTO findById(Integer addressId) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));
        return toResponseDTO(address);
    }

    // EDICAO — BLOQUEIA REGISTRO TECNICO E JA EXCLUIDOS
    public AddressResponseDTO update(Integer addressId, AddressRequestDTO dto) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));

        // ENDERECO TECNICO NAO PODE SER EDITADO
        if (address.getAddressId() == TECHNICAL_RECORD_ID) {
            throw new BusinessRuleException("O endereco tecnico de anonimizacao nao pode ser editado.");
        }

        // ENDERECO COM SOFT DELETE NAO PODE SER EDITADO NOVAMENTE
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar um endereco excluido.");
        }

        address.setZipCode(dto.getZipCode());
        address.setStreet(dto.getStreet());
        address.setNumber(dto.getNumber());
        address.setComplement(dto.getComplement());
        address.setNeighborhood(dto.getNeighborhood());
        address.setCity(dto.getCity());
        address.setState(dto.getState());

        Address updated = addressDao.save(address);
        return toResponseDTO(updated);
    }

    // SUBSTITUICAO IRREVERSIVEL DOS DADOS DO ENDERECO — CEP MANTÉM FORMATO NUMERICO
    private void anonymize(Address address) {
        // CEP ANONIMO COM 8 DIGITOS RESPEITANDO A CONSTRAINT DO BANCO
        address.setZipCode(
                anonymizationService.anonymizeNumericId(address.getAddressId(), "97", 8)
        );
        address.setStreet(anonymizationService.anonymize());
        address.setNumber(anonymizationService.anonymize());
        address.setComplement(null);
        address.setNeighborhood(anonymizationService.anonymize());
        address.setCity(anonymizationService.anonymize());
        address.setState(anonymizationService.anonymize());
        // MARCA SOFT DELETE
        address.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    // EXCLUSAO DIRETA — BLOQUEIA REGISTRO TECNICO E ENDERECO OCUPADO POR LOJA
    public void delete(Integer addressId) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));

        // REGISTRO TECNICO NUNCA PODE SER EXCLUIDO
        if (address.getAddressId() == TECHNICAL_RECORD_ID) {
            throw new BusinessRuleException("O endereco tecnico de anonimizacao nao pode ser excluido.");
        }

        // IMPEDE DUPLA EXCLUSAO
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException("Este endereco ja foi excluido.");
        }

        // ENDERECO VINCULADO A UMA LOJA NAO PODE SER EXCLUIDO DIRETAMENTE
        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException("Nao e possivel excluir um endereco associado a uma loja.");
        }

        anonymize(address);
        addressDao.save(address);
    }

    @Transactional
    // EXCLUSAO NA CASCATA DA LOJA — IGNORA A PROTECAO DE VINCULO PORQUE A LOJA JA ESTA SENDO EXCLUIDA
    public void anonymizeForStoreDeletion(Integer addressId) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));
        anonymize(address);
        addressDao.save(address);
    }

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

    // DETALHE: BUSCA A LOJA VINCULADA INDIVIDUALMENTE
    private AddressResponseDTO toResponseDTO(Address address) {
        return toResponseDTO(address,
                storeDao.findByAddress(address)
                        .map(com.mottainai.mottainaicrud.model.Store::getName)
                        .orElse(null));
    }

    // LISTAGEM: NOME DA LOJA JA RESOLVIDO PELO CHAMADOR — SEM CONSULTA ADICIONAL
    private AddressResponseDTO toResponseDTO(Address address, String storeName) {
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
        dto.setUpdatedAt(address.getUpdatedAt());
        dto.setDeletedAt(address.getDeletedAt());
        // NOME DA LOJA VINCULADA — NULL SE ENDERECO SEM LOJA
        dto.setStoreName(storeName);
        return dto;
    }
}
