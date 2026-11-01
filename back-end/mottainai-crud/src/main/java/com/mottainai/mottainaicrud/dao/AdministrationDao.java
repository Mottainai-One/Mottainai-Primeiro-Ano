package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Administration;
import org.springframework.data.repository.Repository;
import java.util.List;
import java.util.Optional;

// GESTÃO ADMINISTRATIVA: o Service impede autoexclusão; o login mantém seu DAO JDBC.
public interface AdministrationDao extends Repository<Administration, Integer> {
    List<Administration> findAll();
    Optional<Administration> findById(Integer id);
    boolean existsByEmail(String email);
    Administration save(Administration administration);
    boolean existsById(Integer id);
    void delete(Administration administration);
}
