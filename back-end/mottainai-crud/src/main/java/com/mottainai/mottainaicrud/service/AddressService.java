package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AddressDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.model.Address;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE ENDEREÇOS
// Protege o endereço técnico, impede exclusão direta de endereço ocupado e anonimiza o endereço na cascata da loja.

@Service
public class AddressService {

    private final AddressDao addressDao;
    private final StoreDao storeDao;
    private final DataAnonymizationService anonymizationService;

    public AddressService(AddressDao addressDao, StoreDao storeDao, DataAnonymizationService anonymizationService) {
        this.addressDao = addressDao;
        this.storeDao = storeDao;
        // Serviço usado para remover de forma irreversível
        // os dados originais durante a exclusão lógica.
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO DO ENDEREÇO
    // Converte a entrada validada e devolve o DTO; a associação com uma loja é apresentada quando existir.
    public AddressResponseDTO create(AddressRequestDTO dto) {
        Address address = toEntity(dto);
        Address saved = addressDao.save(address);
        return toResponseDTO(saved);
    }

    // LISTAGEM COM CORRELAÇÃO EM LOTE
    // Carrega nomes das lojas uma vez e retira o ID técnico; registros apagados mantêm seu histórico na lista.
    public List<AddressResponseDTO> findAll() {
        var storeNames = storeDao.findAddressStoreNames().stream().collect(java.util.stream.Collectors.toMap(
                com.mottainai.mottainaicrud.dao.AddressStoreName::getAddressId,
                com.mottainai.mottainaicrud.dao.AddressStoreName::getStoreName));
        return addressDao.findAll()
                .stream()
                .filter(address ->
                        address.getAddressId() != TECHNICAL_RECORD_ID
                )
                .map(address -> toResponseDTO(address, storeNames.get(address.getAddressId())))
                .toList();
    }

    // CONSULTA DO CADASTRO
    // Ausência do ID produz 404; os campos de saída e a loja vinculada são montados no DTO.
    public AddressResponseDTO findById(Integer addressId) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));
        return toResponseDTO(address);
    }

    // EDIÇÃO PROTEGIDA
    // O endereço técnico e registros já apagados não podem ser editados; os demais campos mantêm o mesmo ID.
    public AddressResponseDTO update(Integer addressId, AddressRequestDTO dto) {
        Address address = addressDao.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));

        if (address.getAddressId() == TECHNICAL_RECORD_ID) {
            throw new BusinessRuleException(
                    "O endereço técnico de anonimização não pode ser editado."
            );
        }

        // Um endereço excluído logicamente não pode ser editado novamente.
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar um endereço excluído."
            );
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

    // SUBSTITUIÇÃO DO ENDEREÇO ORIGINAL
    // O CEP recebe identificador numérico do formato exigido; os demais dados são substituídos, sem operação inversa.
    private void anonymize(Address address) {

        // Mantém o CEP com exatamente 8 números,
        // respeitando a constraint do banco.
        address.setZipCode(
                anonymizationService.anonymizeNumericId(
                        address.getAddressId(),
                        "97",
                        8
                )
        );

        // Substitui definitivamente os dados do endereço.
        address.setStreet(anonymizationService.anonymize());
        address.setNumber(anonymizationService.anonymize());
        address.setComplement(null);
        address.setNeighborhood(anonymizationService.anonymize());
        address.setCity(anonymizationService.anonymize());
        address.setState(anonymizationService.anonymize());

        // Registra a exclusão lógica.
        address.setDeletedAt(LocalDateTime.now());
    }

    @Transactional
    // EXCLUSÃO DIRETA DO ENDEREÇO
    // Bloqueia o registro técnico e endereços ocupados; anonimiza os campos e preenche DeletedAt sem apagar a linha.
    public void delete(Integer addressId) {

        Address address = addressDao.findById(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Endereço não encontrado."
                        )
                );

        if (address.getAddressId() == TECHNICAL_RECORD_ID) {
            throw new BusinessRuleException(
                    "O endereço técnico de anonimização não pode ser excluído."
            );
        }

        // Um endereço já anonimizado não deve
        // passar novamente pelo processo de exclusão.
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Este endereço já foi excluído."
            );
        }

        // Mantém a regra existente:
        // um endereço utilizado por uma loja não pode
        // ser excluído diretamente.
        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException(
                    "Não é possível excluir um endereço associado a uma loja."
            );
        }

        // Como o endereço não está associado a nenhuma loja,
        // sua exclusão lógica pode ser realizada normalmente.
        anonymize(address);

        addressDao.save(address);
    }

    @Transactional
    // ENDEREÇO NA CASCATA DA LOJA
    // Chamado quando a própria loja é excluída, evitando o bloqueio de ocupação da exclusão direta; não faz DELETE físico.
    public void anonymizeForStoreDeletion(Integer addressId) {

        Address address = addressDao.findById(addressId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Endereço não encontrado."
                        )
                );

        // Este método ignora a proteção de vínculo porque é chamado
        // justamente durante a exclusão da própria loja.
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

    // CONVERSÃO DO DETALHE
    // A consulta de uma única loja associada é usada no detalhe; a listagem utiliza a sobrecarga com nome já carregado.
    private AddressResponseDTO toResponseDTO(Address address) {
        return toResponseDTO(address, storeDao.findByAddress(address).map(com.mottainai.mottainaicrud.model.Store::getName).orElse(null));
    }

    // CONVERSÃO COM CORRELAÇÃO JÁ RESOLVIDA
    // storeName pode ser null para endereço sem loja; esta sobrecarga não dispara consulta adicional.
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

        // NOME DA LOJA VINCULADA
        // O chamador já resolveu a associação, individualmente ou em lote, antes de montar este DTO.
        dto.setStoreName(storeName);

        return dto;
    }
}
