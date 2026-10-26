package com.mottainai.mottainaicrud.dao;

import com.mottainai.crudinicial.model.Administration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministrationDao extends JpaRepository<Administration, Integer> {
    boolean existsByEmail(String email);
}
