package com.mottainai.mottainaicrud.util;

import java.math.BigDecimal;

/** Formulários aceitam 209,89, 209.89 e 1.209,89, sem arredondamento silencioso. */
public final class PriceParser {
    private PriceParser() {}

    // Padroniza apenas formatos conhecidos e rejeita ambiguidades antes de converter para BigDecimal.
    public static BigDecimal parse(String text) {
        if (text == null || text.isBlank()) return null;
        String value = text.trim();
        if (value.matches("[+-]?\\d{1,3}(?:\\.\\d{3})+,\\d{1,2}")) {
            value = value.replace(".", "").replace(',', '.');
        } else if (value.matches("[+-]?\\d+(?:[.,]\\d{1,2})?")) {
            value = value.replace(',', '.');
        } else {
            throw new IllegalArgumentException("Informe um preço como 209,89, com até duas casas decimais.");
        }
        return new BigDecimal(value);
    }
}
