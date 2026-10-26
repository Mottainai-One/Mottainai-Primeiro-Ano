package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface CompanyDao extends JpaRepository<Company, Integer> {
    // A PK agora é companyId (String) — o parâmetro genérico continua "String",
    // só que o significado mudou: antes era CNPJ, agora é companyId.

    // CNPJ não é mais PK, mas continua sendo único no negócio.
    boolean existsByCnpj(String cnpj);
    Optional<Company> findByCompanyId(int companyId); // útil se você quiser um endpoint de busca por CNPJ
    List<Company> findByOfficialName(String officialName); // útil se você quiser um endpoint de busca por CNPJ
    List<Company> findByTradeName(String tradeName); // útil se você quiser um endpoint de busca por CNPJ
}