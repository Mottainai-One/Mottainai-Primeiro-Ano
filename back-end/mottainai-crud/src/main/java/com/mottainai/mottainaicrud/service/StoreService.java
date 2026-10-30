package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.AddressDao;
import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.StoreRequestDTO;
import com.mottainai.mottainaicrud.dto.StoreResponseDTO;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.model.Address;
import com.mottainai.mottainaicrud.model.Company;
import com.mottainai.mottainaicrud.model.Store;
import com.mottainai.mottainaicrud.security.DataAnonymizationService;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

// REGRAS DO CADASTRO DE LOJAS
// CONTROLA EMPRESA E ENDERECO; PROTEGE A LOJA TECNICA; CASCATA LOGICA PARA FUNCIONARIOS E ENDERECO

@Service
public class StoreService {

    private final StoreDao storeDao;
    private final CompanyDao companyDao;
    private final AddressDao addressDao;
    private final EmployeeDao employeeDao;
    private final EmployeeService employeeService;
    // CRIPTOGRAFIA REVERSIVEL DOS CONTATOS DA LOJA
    private final DataEncryptionService encryptionService;
    private final AddressService addressService;
    // ANONIMIZACAO IRREVERSIVEL USADA NO SOFT DELETE DA LOJA
    private final DataAnonymizationService anonymizationService;

    public StoreService(StoreDao storeDao,
                        CompanyDao companyDao,
                        AddressDao addressDao,
                        EmployeeDao employeeDao,
                        EmployeeService employeeService,
                        DataEncryptionService encryptionService,
                        AddressService addressService,
                        DataAnonymizationService anonymizationService) {
        this.storeDao = storeDao;
        this.companyDao = companyDao;
        this.addressDao = addressDao;
        this.employeeDao = employeeDao;
        this.employeeService = employeeService;
        this.encryptionService = encryptionService;
        this.addressService = addressService;
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO — CNPJ UNICO, EMPRESA E ENDERECO VALIDOS, ENDERECO LIVRE; EMAIL E TELEFONE CRIPTOGRAFADOS
    public StoreResponseDTO create(StoreRequestDTO dto) {
        if (storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Ja existe uma loja cadastrada com esse CNPJ.");
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada."));

        // EMPRESA EXCLUIDA NAO PODE RECEBER NOVAS LOJAS
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel cadastrar uma loja em uma empresa excluida.");
        }

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));

        // ENDERECO EXCLUIDO NAO PODE SER REUTILIZADO
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um endereco excluido.");
        }

        // ENDERECO JA OCUPADO POR OUTRA LOJA
        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException("Este endereco ja esta associado a outra loja.");
        }

        Store store = new Store();
        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        // EMAIL E TELEFONE CRIPTOGRAFADOS COM AES ANTES DE SALVAR
        store.setEmail(encryptionService.encrypt(dto.getEmail()));
        store.setPhone(encryptionService.encrypt(dto.getPhone()));
        store.setCompany(company);
        store.setAddress(address);
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        store.setActive(dto.getActive());

        Store saved = storeDao.save(store);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM A LOJA TECNICA
    public List<StoreResponseDTO> findAll() {
        return storeDao.findAll()
                .stream()
                .filter(store -> store.getStoreId() != TECHNICAL_RECORD_ID)
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE — ID TECNICO TRATADO COMO NAO ENCONTRADO
    public StoreResponseDTO findById(int storeId) {
        if (storeId == TECHNICAL_RECORD_ID) {
            throw new ResourceNotFoundException("Loja nao encontrada.");
        }
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada."));
        return toResponseDTO(store);
    }

    // EDICAO — PROTEGE LOJA TECNICA E EXCLUIDAS; VERIFICA CNPJ E ENDERECO NA ATUALIZACAO
    public StoreResponseDTO update(int storeId, StoreRequestDTO dto) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada."));

        // LOJA TECNICA NAO PODE SER EDITADA
        if (TECHNICAL_STORE_CNPJ.equals(store.getCnpj())) {
            throw new BusinessRuleException("A loja tecnica de anonimizacao nao pode ser editada.");
        }

        // LOJA COM SOFT DELETE NAO PODE SER EDITADA
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel editar uma loja excluida.");
        }

        if (!store.getCnpj().equals(dto.getCnpj()) && storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Ja existe uma loja cadastrada com esse CNPJ.");
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa nao encontrada."));

        // EMPRESA EXCLUIDA NAO PODE SER REUTILIZADA
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel cadastrar uma loja em uma empresa excluida.");
        }

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereco nao encontrado."));

        // ENDERECO EXCLUIDO NAO PODE SER REUTILIZADO
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException("Nao e possivel utilizar um endereco excluido.");
        }

        // ENDERECO JA OCUPADO POR OUTRA LOJA — A PROPRIA LOJA E EXCLUIDA DA VERIFICACAO
        if (storeDao.existsByAddressAndStoreIdNot(address, storeId)) {
            throw new DuplicateResourceException("Este endereco ja esta associado a outra loja.");
        }

        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        store.setEmail(encryptionService.encrypt(dto.getEmail()));
        store.setPhone(encryptionService.encrypt(dto.getPhone()));
        store.setCompany(company);
        store.setAddress(address);
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        store.setActive(dto.getActive());

        Store updated = storeDao.save(store);
        return toResponseDTO(updated);
    }

    @Transactional
    // SOFT DELETE COM CASCATA — FUNCIONARIOS EXCLUIDOS PELO EmployeeService; ENDERECO ANONIMIZADO; NOME PRESERVADO
    public void delete(Integer storeId) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loja nao encontrada."));

        // LOJA TECNICA PRECISAS PERMANECER PARA RECEBER FUNCIONARIOS ANONIMIZADOS
        if (TECHNICAL_STORE_CNPJ.equals(store.getCnpj())) {
            throw new BusinessRuleException("A loja tecnica de anonimizacao nao pode ser excluida.");
        }

        // IMPEDE DUPLA EXCLUSAO
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException("Esta loja ja foi excluida.");
        }

        // CASCATA: FUNCIONARIOS AINDA ATIVOS SAO EXCLUIDOS LOGICAMENTE ANTES DA LOJA
        // EmployeeService.delete() TAMBEM CUIDA DA CONTA VINCULADA A CADA FUNCIONARIO
        employeeDao.findByStore_StoreId(storeId)
                .stream()
                .filter(employee -> employee.getDeletedAt() == null)
                .forEach(employee -> employeeService.delete(employee.getEmployeeId()));

        // CNPJ SUBSTITUIDO POR IDENTIFICADOR ANONIMO DE 14 DIGITOS
        store.setCnpj(anonymizationService.anonymizeNumericId(store.getStoreId(), "98", 14));
        store.setEmail(anonymizationService.anonymize());
        store.setPhone(anonymizationService.anonymize());
        store.setLatitude(null);
        store.setLongitude(null);
        store.setActive(false);
        store.setDeletedAt(LocalDateTime.now());

        // ENDERECO ANONIMIZADO PELA CASCATA — USA METODO PROPRIO PARA IGNORAR A PROTECAO DE VINCULO
        addressService.anonymizeForStoreDeletion(store.getAddress().getAddressId());
        storeDao.save(store);
    }

    // ENDERECO DISPONIVEL PARA O SELECT — VERIFICA SE ESTA OCUPADO POR OUTRA LOJA
    public boolean isAddressAvailable(Integer addressId, Integer currentStoreId) {
        if (currentStoreId == null) {
            // CADASTRO: ENDERECO DEVE ESTAR COMPLETAMENTE LIVRE
            return !storeDao.existsByAddress_AddressId(addressId);
        }
        // EDICAO: O PROPRIO ENDERECO DA LOJA CONTINUA SENDO OPCAO VALIDA
        return !storeDao.existsByAddress_AddressIdAndStoreIdNot(addressId, currentStoreId);
    }

    // IDS OCUPADOS EM LOTE — EVITA EXISTS INDIVIDUAL PARA CADA ENDERECO DO SELECT
    public java.util.Set<Integer> findUnavailableAddressIds(Integer currentStoreId) {
        return storeDao.findUnavailableAddressIds(currentStoreId);
    }

    // MONTA O DTO DA LOJA — CONTATOS DESCRIPTOGRAFADOS SOMENTE SE NAO ANONIMIZADOS
    private StoreResponseDTO toResponseDTO(Store store) {
        StoreResponseDTO dto = new StoreResponseDTO();
        dto.setStoreId(store.getStoreId());
        dto.setName(store.getName());
        dto.setCnpj(store.getCnpj());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        dto.setActive(store.isActive());
        dto.setCreatedAt(store.getCreatedAt());
        dto.setUpdatedAt(store.getUpdatedAt());
        dto.setDeletedAt(store.getDeletedAt());
        dto.setCompanyId(store.getCompany().getCompanyId());
        dto.setCompanyName(store.getCompany().getTradeName());
        dto.setAddressId(store.getAddress().getAddressId());
        dto.setAddressZipCode(store.getAddress().getZipCode());
        dto.setAddressStreet(store.getAddress().getStreet());
        dto.setAddressNumber(store.getAddress().getNumber());
        dto.setAddressComplement(store.getAddress().getComplement());
        dto.setAddressNeighborhood(store.getAddress().getNeighborhood());
        dto.setAddressCity(store.getAddress().getCity());
        dto.setAddressState(store.getAddress().getState());

        boolean deleted = store.getDeletedAt() != null;
        if (deleted) {
            // APOS SOFT DELETE, EMAIL E TELEFONE NAO SAO MAIS AES — NAO TENTAR DESCRIPTOGRAFAR
            dto.setEmail("Anonimizado");
            dto.setPhone("Anonimizado");
        } else {
            dto.setEmail(encryptionService.decrypt(store.getEmail()));
            dto.setPhone(encryptionService.decrypt(store.getPhone()));
        }
        return dto;
    }
}
