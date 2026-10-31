package com.mottainai.crudinicial.util;

import java.util.Locale;

// FILTROS DAS TELAS CRUD
// Pesquisa os DTOs já carregados, mantendo busca textual, igualdade de ID e seleção de ativos/inativos.

public final class ViewFilterUtils {

    private ViewFilterUtils() {
    }

    public static boolean matchesSearchById(String search, Integer id, String... values) {
        // BUSCA ADITIVA: ID exige igualdade; a busca textual anterior continua válida para os outros campos.
        if (id != null && search != null && !search.isBlank()) {
            try {
                if (id == Integer.parseInt(search.trim())) return true;
            } catch (NumberFormatException ignored) {
                // A entrada continua sendo uma busca textual, inclusive quando excede o intervalo de int.
            }
        }
        return matchesSearch(search, values);
    }

    // PESQUISA TEXTUAL SEM DIFERENÇA DE CAIXA
    // Entrada vazia aceita a linha; Locale.ROOT mantém a normalização independente do idioma do ambiente.
    public static boolean matchesSearch(
            String search,
            String... values) {

        if (search == null || search.isBlank()) {
            return true;
        }

        String normalizedSearch =
                search.trim().toLowerCase(Locale.ROOT);

        for (String value : values) {

            if (value != null
                    && value.toLowerCase(Locale.ROOT)
                    .contains(normalizedSearch)) {

                return true;
            }
        }

        return false;
    }

    // FILTRO DE SITUAÇÃO
    // ativo/inativo selecionam o booleano correspondente; situação vazia ou não reconhecida mantém a listagem.
    public static boolean matchesStatus(
            Boolean active,
            String status) {

        if (status == null || status.isBlank()) {
            return true;
        }

        if ("ativo".equalsIgnoreCase(status)) {
            return Boolean.TRUE.equals(active);
        }

        if ("inativo".equalsIgnoreCase(status)) {
            return Boolean.FALSE.equals(active);
        }

        return true;
    }
}
