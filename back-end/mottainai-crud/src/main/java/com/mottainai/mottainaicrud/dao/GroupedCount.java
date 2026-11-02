package com.mottainai.mottainaicrud.dao;

// PROJEÇÃO DE CONTAGEM AGRUPADA
// O alias id identifica o cargo/plano e total representa os vínculos contados na consulta agregada.

public interface GroupedCount {
    Integer getId();
    long getTotal();
}
