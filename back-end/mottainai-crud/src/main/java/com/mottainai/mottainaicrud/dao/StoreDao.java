package com.mottainai.mottainaicrud.dao;

import com.mottainai.crudinicial.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StoreDao extends JpaRepository<Store, Integer> {
    boolean existsByCnpj(String cnpj);
    boolean existsByEmail(String email);
    Optional<Store> findByCnpj(String cnpj);
}