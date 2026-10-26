// NOVO — assumindo Address com PK int addressId (bate com "AddressId serial" do script.sql)
package com.mottainai.mottainaicrud.dao;

import com.mottainai.mottainaicrud.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressDao extends JpaRepository<Address, Integer> {
}