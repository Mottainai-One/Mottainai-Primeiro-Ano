package com.mottainai.mottainaicrud.dao;

// PROJEÇÃO LEVE DOS CADASTROS RECENTES
// Agrupa nome, alternativa de nome, data e situação; nomes de pessoas ainda exigem AES no Service.

public interface DashboardRegistration {
    String getName();
    String getFallbackName();
    java.time.LocalDateTime getCreatedAt();
    boolean isActive();
}
