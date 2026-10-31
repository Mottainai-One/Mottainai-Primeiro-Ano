// NOVO — assumindo Address com PK int addressId (bate com "AddressId serial" do script.sql)
package com.mottainai.crudinicial.dao;

import com.mottainai.crudinicial.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// REPOSITÓRIO JPA DE ENDEREÇOS
// As operações herdadas acessam PostgreSQL pelo DataSource do Spring; o Service define o soft delete.

@Repository
public interface AddressDao extends JpaRepository<Address, Integer> {
}