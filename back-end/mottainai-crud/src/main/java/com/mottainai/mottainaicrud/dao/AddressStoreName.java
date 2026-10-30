package com.mottainai.crudinicial.dao;

// PROJEÇÃO DO NOME DA LOJA POR ENDEREÇO
// Os aliases addressId/storeName permitem montar DTOs em lote sem findByAddress para cada linha.

public interface AddressStoreName {
    Integer getAddressId();
    String getStoreName();
}
