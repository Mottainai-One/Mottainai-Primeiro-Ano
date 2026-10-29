package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;

@Repository
public interface CompanyDao extends JpaRepository<Company, Integer> {
    // A PK AGORA EH COMPANYID (STRING) — O PARAMETRO GENÉRICO CONTINUA "STRING"
    // SOH QUE O SIGNIFICADO MUDOU: ANTES ERA CNPJ, AGORA É COMPANYID

    // CNPJ NAO EH MAIS PK, MAS CONTINUA SENDO UNICO NO NEGOCIO
    boolean existsByCnpj(String cnpj);
    // UTIL PARA UM ENDPOINT DE BUSCA POR CNPJ
    Optional<Company> findByCompanyId(int companyId);
    List<Company> findByOfficialName(String officialName);
    List<Company> findByTradeName(String tradeName);
}