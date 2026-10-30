package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.Address;
import com.mottainai.crudinicial.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// CONSULTAS JPA DE LOJAS E ENDEREÇOS OCUPADOS
// Fornece vínculos, projeções em lote e EXISTS usados pelas regras de disponibilidade do cadastro.

@Repository
public interface StoreDao extends JpaRepository<Store, Integer> {
    // NOMES DE LOJAS EM LOTE
    // Seleciona apenas endereço/nome para enriquecer a lista de Address sem uma busca de loja por endereço.
    @org.springframework.data.jpa.repository.Query("select s.address.addressId as addressId, s.name as storeName from Store s")
    List<AddressStoreName> findAddressStoreNames();

    // AGREGAÇÃO DAS LOJAS NO PAINEL
    // Exclui o ID técnico; os registros apagados permanecem no total e são tratados como inativos.
    @org.springframework.data.jpa.repository.Query("select count(s) as total, " +
            "count(case when s.active = true and s.deletedAt is null then 1 else null end) as active " +
            "from Store s where s.storeId <> :technicalId")
    DashboardCount summarizeDashboard(@org.springframework.data.repository.query.Param("technicalId") int technicalId);

    // PROJEÇÃO DOS RECENTES DE LOJA
    // Seleciona nome/data/situação e retira soft deleted; o limite vem do Pageable recebido.
    @org.springframework.data.jpa.repository.Query("select s.name as name, s.name as fallbackName, " +
            "s.createdAt as createdAt, s.active as active from Store s " +
            "where s.deletedAt is null and s.storeId <> :technicalId order by s.createdAt desc nulls last")
    List<DashboardRegistration> findDashboardRecent(@org.springframework.data.repository.query.Param("technicalId") int technicalId,
                                                   org.springframework.data.domain.Pageable pageable);

    // ENDEREÇOS OCUPADOS EM LOTE
    // Na criação, qualquer loja ocupa o endereço; na edição, a própria StoreId é desconsiderada.
    @org.springframework.data.jpa.repository.Query(
            "select s.address.addressId from Store s where (:storeId is null or s.storeId <> :storeId)")
    java.util.Set<Integer> findUnavailableAddressIds(@org.springframework.data.repository.query.Param("storeId") Integer storeId);

    // BUSCAS E VERIFICAÇÕES DE VÍNCULO
    // CNPJ identifica duplicidade e a loja técnica; CompanyId fornece a cascata e EXISTS controla ocupação de Address.
    boolean existsByCnpj(String cnpj);
    Optional<Store> findByCnpj(String cnpj);
    boolean existsByAddress(Address address);
    List<Store> findByCompany_CompanyId(Integer companyId);
    boolean existsByAddressAndStoreIdNot(Address address, int storeId);
    Optional<Store> findByAddress(Address address);
    // DISPONIBILIDADE POR IDENTIFICADOR
    // As versões com StoreIdNot permitem editar a loja sem rejeitar o endereço que já pertence a ela.
    boolean existsByAddress_AddressId(Integer addressId);
    boolean existsByAddress_AddressIdAndStoreIdNot(
            Integer addressId,
            int storeId
    );
}
