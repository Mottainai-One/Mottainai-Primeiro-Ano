package com.mottainai.mottainaicrud.dao;

// PROJEÇÃO DOS CARDS DO PAINEL
// total e active vêm de agregações; o Service obtém os inativos pela diferença entre esses valores.

public interface DashboardCount {
    long getTotal();
    long getActive();
}
