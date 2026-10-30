package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Address;
import com.mottainai.mottainaicrud.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreDao extends JpaRepository<Store, Integer> {

    // VERIFICA SE O CNPJ JA ESTA CADASTRADO
    boolean existsByCnpj(String cnpj);

    // VERIFICA SE O EMAIL JA ESTA CADASTRADO
    boolean existsByEmail(String email);

    // BUSCA LOJA PELO CNPJ
    Optional<Store> findByCnpj(String cnpj);

    boolean existsByAddress(Address address);
}