package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Administration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrationDao extends JpaRepository<Administration, Integer> {
    // VERIFICACAO DE EXISTENCIA POR EMAIL
    boolean existsByEmail(String email);
}
