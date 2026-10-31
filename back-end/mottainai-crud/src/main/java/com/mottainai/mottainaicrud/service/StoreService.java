package com.mottainai.crudinicial.service;

import com.mottainai.crudinicial.dao.AddressDao;
import com.mottainai.crudinicial.dao.CompanyDao;
import com.mottainai.crudinicial.dao.EmployeeDao;
import com.mottainai.crudinicial.dao.StoreDao;
import com.mottainai.crudinicial.dto.StoreRequestDTO;
import com.mottainai.crudinicial.dto.StoreResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.model.Address;
import com.mottainai.crudinicial.model.Company;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;
import com.mottainai.crudinicial.model.Store;
import com.mottainai.crudinicial.security.DataAnonymizationService;
import com.mottainai.crudinicial.security.DataEncryptionService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import com.mottainai.crudinicial.exception.BusinessRuleException;

// REGRAS DO CADASTRO DE LOJAS
// Controla empresa/endereço, protege a loja técnica e coordena a exclusão lógica de funcionários e endereço.

@Service
public class StoreService {

    private final StoreDao storeDao;
    private final CompanyDao companyDao;
    private final AddressDao addressDao;
    private final EmployeeDao employeeDao;
    private final EmployeeService employeeService;
    private final DataEncryptionService encryptionService;
    private final AddressService addressService;
    private final DataAnonymizationService anonymizationService;

    public StoreService(StoreDao storeDao, CompanyDao companyDao, AddressDao addressDao, EmployeeDao employeeDao, EmployeeService employeeService, DataEncryptionService encryptionService, AddressService addressService, DataAnonymizationService anonymizationService) {
        this.storeDao = storeDao;
        this.companyDao = companyDao;
        this.addressDao = addressDao;
        this.employeeDao = employeeDao;
        this.employeeService = employeeService;
        this.addressService = addressService;
        // Serviço responsável pela criptografia reversível
        // dos dados que ainda pertencem a registros válidos.
        this.encryptionService = encryptionService;
        // Serviço usado somente quando os dados precisam
        // perder definitivamente a relação com o valor original.
        this.anonymizationService = anonymizationService;
    }

    // CADASTRO E EXCLUSIVIDADE DO ENDEREÇO
    // Exige CNPJ não duplicado, empresa/endereço não excluídos e Address livre; contatos são cifrados por AES.
    public StoreResponseDTO create(StoreRequestDTO dto) {
        if (storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Já existe uma loja cadastrada com esse CNPJ.");
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        // Não permite criar uma loja para uma empresa
        // que já foi excluída logicamente.
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível cadastrar uma loja em uma empresa excluída."
            );
        }

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));

        // Endereços excluídos não podem ser reutilizados
        // em novas lojas.
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um endereço excluído."
            );
        }

        Store store = new Store();
        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        // Criptografa o e-mail antes de persistir no banco.
        store.setEmail(encryptionService.encrypt(dto.getEmail()));
        // O telefone também é armazenado de forma criptografada.
        store.setPhone(encryptionService.encrypt(dto.getPhone()));
        store.setCompany(company);
        store.setAddress(address);
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        store.setActive(dto.getActive());

        if (storeDao.existsByAddress(address)) {
            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        Store saved = storeDao.save(store);
        return toResponseDTO(saved);
    }

    // LISTAGEM SEM A LOJA TÉCNICA
    // A linha reservada é retirada por ID; as demais usam DTOs com empresa, endereço e contatos preparados.
    public List<StoreResponseDTO> findAll() {
        return storeDao.findAll()
                .stream()
                .filter(store ->
                        store.getStoreId() != TECHNICAL_RECORD_ID
                )
                .map(this::toResponseDTO)
                .toList();
    }

    // DETALHE DA LOJA
    // O ID técnico é tratado como não encontrado; os cadastros normais são correlacionados no DTO.
    public StoreResponseDTO findById(int storeId) {

        if (storeId == TECHNICAL_RECORD_ID) {
            throw new ResourceNotFoundException(
                    "Loja não encontrada."
            );
        }

        Store store = storeDao.findById(storeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Loja não encontrada."
                        )
                );

        return toResponseDTO(store);
    }

    // EDIÇÃO E VÍNCULOS DA LOJA
    // Protege o histórico, rejeita CNPJ duplicado e permite manter o endereço da própria loja, sem ocupá-lo em outra.
    public StoreResponseDTO update(int storeId, StoreRequestDTO dto) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() -> new ResourceNotFoundException("Loja não encontrada."));

        // A loja técnica recebe funcionários
        // depois que eles são anonimizados.
        if (TECHNICAL_STORE_CNPJ.equals(
                store.getCnpj()
        )) {
            throw new BusinessRuleException(
                    "A loja técnica de anonimização não pode ser editada."
            );
        }

        // Uma loja excluída logicamente não pode mais ser editada.
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível editar uma loja excluída."
            );
        }

        if (!store.getCnpj().equals(dto.getCnpj()) && storeDao.existsByCnpj(dto.getCnpj())) {
            throw new DuplicateResourceException("Já existe uma loja cadastrada com esse CNPJ.");
        }

        Company company = companyDao.findById(dto.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Empresa não encontrada."));

        // Não permite atualizar uma loja para uma empresa
        // que já foi excluída logicamente.
        if (company.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível cadastrar uma loja em uma empresa excluída."
            );
        }

        Address address = addressDao.findById(dto.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado."));

        // Endereços excluídos não podem ser reutilizados
        // em novas lojas.
        if (address.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Não é possível utilizar um endereço excluído."
            );
        }

        store.setName(dto.getName());
        store.setCnpj(dto.getCnpj());
        // Criptografa o e-mail antes de persistir no banco.
        store.setEmail(encryptionService.encrypt(dto.getEmail()));
        // O telefone também é armazenado de forma criptografada.
        store.setPhone(encryptionService.encrypt(dto.getPhone()));
        store.setCompany(company);
        store.setAddress(address);
        store.setLatitude(dto.getLatitude());
        store.setLongitude(dto.getLongitude());
        store.setActive(dto.getActive());

        if (storeDao.existsByAddressAndStoreIdNot(
                address,
                storeId)) {

            throw new DuplicateResourceException(
                    "Este endereço já está associado a outra loja."
            );
        }

        Store updated = storeDao.save(store);
        return toResponseDTO(updated);
    }

    @Transactional
    // CASCATA DA LOJA
    // Funcionários são excluídos pelo EmployeeService e o endereço é anonimizado; nome e linha da loja permanecem no histórico.
    public void delete(Integer storeId) {
        Store store = storeDao.findById(storeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Loja não encontrada.")
                );

        // Sem esta loja, futuras exclusões de funcionários
        // não conseguiriam manter o vínculo obrigatório de Store.
        if (TECHNICAL_STORE_CNPJ.equals(
                store.getCnpj()
        )) {
            throw new BusinessRuleException(
                    "A loja técnica de anonimização não pode ser excluída."
            );
        }

        // Impede que uma loja já excluída
        // seja anonimizada novamente.
        if (store.getDeletedAt() != null) {
            throw new BusinessRuleException(
                    "Esta loja já foi excluída."
            );
        }

        // Antes de anonimizar a loja, exclui logicamente todos
        // os funcionários que ainda estão vinculados a ela.
        //
        // O EmployeeService já cuida da anonimização do funcionário
        // e também da UserAccount associada, caso exista.
        employeeDao.findByStore_StoreId(storeId)
                .stream()
                .filter(employee -> employee.getDeletedAt() == null)
                .forEach(employee ->
                        employeeService.delete(employee.getEmployeeId())
                );
        // Mantém o nome da loja como referência histórica.
        // Substitui o CNPJ original por um identificador anônimo
        // de 14 dígitos, mantendo as constraints do banco.
        store.setCnpj(
                anonymizationService.anonymizeNumericId(
                        store.getStoreId(),
                        "98",
                        14
                )
        );
        // Email e telefone deixam de possuir qualquer ligação
        // com os valores originais.
        store.setEmail(anonymizationService.anonymize());
        store.setPhone(anonymizationService.anonymize());
        // Remove a localização geográfica original da loja.
        store.setLatitude(null);
        store.setLongitude(null);
        // Realiza a exclusão lógica sem apagar a linha do banco.
        store.setActive(false);
        store.setDeletedAt(LocalDateTime.now());
        // Como a própria loja está sendo excluída,
        // o endereço relacionado também precisa ser anonimizado.
        // Usamos o método específico para não quebrar a proteção
        // do delete() público do AddressService.
        addressService.anonymizeForStoreDeletion(store.getAddress().getAddressId());
        storeDao.save(store);
    }

    // CORRELAÇÃO DA LOJA PARA A RESPOSTA
    // Reúne empresa/endereço sem enviar entidades ao JSP; contatos ANON_ não são enviados ao decrypt.
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
        // Dados da empresa usados tanto na tabela
        // quanto no modal de detalhes da loja.
        dto.setCompanyId(
                store.getCompany().getCompanyId()
        );
        dto.setCompanyName(
                store.getCompany().getTradeName()
        );

        // Dados completos do endereço são enviados no DTO
        // para evitar que o JSP precise acessar entidades JPA.
        dto.setAddressId(
                store.getAddress().getAddressId()
        );
        dto.setAddressZipCode(
                store.getAddress().getZipCode()
        );
        dto.setAddressStreet(
                store.getAddress().getStreet()
        );
        dto.setAddressNumber(
                store.getAddress().getNumber()
        );
        dto.setAddressComplement(
                store.getAddress().getComplement()
        );
        dto.setAddressNeighborhood(
                store.getAddress().getNeighborhood()
        );
        dto.setAddressCity(
                store.getAddress().getCity()
        );
        dto.setAddressState(
                store.getAddress().getState()
        );

        boolean deleted = store.getDeletedAt() != null;

        // Depois da exclusão lógica, Email e Phone deixam de ser AES.
        // Por isso não tentamos descriptografar esses campos.
        if (deleted) {
            dto.setEmail("Anonimizado");
            dto.setPhone("Anonimizado");
        } else {
            // Registros normais continuam sendo descriptografados
            // apenas antes de serem enviados para a tela.
            dto.setEmail(encryptionService.decrypt(store.getEmail()));
            dto.setPhone(encryptionService.decrypt(store.getPhone()));
        }
        return dto;
    }

    // VALIDAÇÃO INDIVIDUAL DO ENDEREÇO
    // EXISTS consulta a ocupação; na edição, a própria loja é excluída da verificação.
    public boolean isAddressAvailable(
            Integer addressId,
            Integer currentStoreId) {

        // No cadastro, o endereço precisa estar
        // completamente livre.
        if (currentStoreId == null) {
            return !storeDao.existsByAddress_AddressId(
                    addressId
            );
        }

        // Na edição, o endereço da própria loja
        // continua sendo uma opção válida.
        return !storeDao
                .existsByAddress_AddressIdAndStoreIdNot(
                        addressId,
                        currentStoreId
                );
    }

    // DISPONIBILIDADE PARA O SELECT EM LOTE
    // Fornece IDs ocupados para filtrar todas as opções sem repetir EXISTS a cada endereço.
    public java.util.Set<Integer> findUnavailableAddressIds(Integer currentStoreId) {
        return storeDao.findUnavailableAddressIds(currentStoreId);
    }
}
