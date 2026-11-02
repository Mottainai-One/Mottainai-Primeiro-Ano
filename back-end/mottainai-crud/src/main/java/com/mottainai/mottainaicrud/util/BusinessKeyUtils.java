package com.mottainai.mottainaicrud.util;

/** Normaliza as chaves de consulta sem modificar os dados persistidos nem escolher registros por nome. */
public final class BusinessKeyUtils {
    private BusinessKeyUtils() {}

    // O mesmo CNPJ pode ser informado com máscara ou com os 14 dígitos usados no banco.
    public static String cnpj(String value) {
        if (value == null || !value.trim().matches("\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}")) {
            throw new IllegalArgumentException("Informe o CNPJ completo, com 14 dígitos.");
        }
        return value.trim().replaceAll("[^0-9]", "");
    }

    // Remove espaços das extremidades; mantém a comparação exata usada no login e no UNIQUE do banco.
    public static String email(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Informe o email completo do administrador.");
        }
        return value.trim();
    }
}
