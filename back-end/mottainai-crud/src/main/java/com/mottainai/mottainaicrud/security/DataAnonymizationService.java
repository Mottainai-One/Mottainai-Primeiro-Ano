package com.mottainai.crudinicial.security;

import org.springframework.stereotype.Service;

import java.time.LocalDate;

import java.util.UUID;

// SUBSTITUIÇÃO DOS DADOS NO SOFT DELETE
// Gera marcadores ANON_, data substituta e identificadores numéricos compatíveis com os campos existentes.

@Service
public class DataAnonymizationService {
    // MARCADOR IRREVERSÍVEL
    // ANON_ identifica uma substituição aleatória; não há chave ou mapeamento para restaurar o dado pessoal anterior.
    public String anonymize() {
        // Gera um valor aleatório novo, sem guardar nenhuma relação
        // com o dado original. Por isso não existe operação de "desanonimizar".
        return "ANON_" + UUID.randomUUID();
    }

    // DATA SUBSTITUTA
    // Mantém um LocalDate válido para satisfazer a coluna obrigatória sem conservar a data real de contratação.
    public LocalDate anonymizeDate() {
        // Substitui a data original por uma data fixa usada
        // apenas como valor anônimo no banco.
        //
        // Não existe nenhuma relação entre essa data e
        // a data original do funcionário.
        return LocalDate.of(1970, 1, 1);
    }

    // IDENTIFICADOR NUMÉRICO SUBSTITUTO
    // Combina prefixo e ID interno para manter tamanho/unicidade; não é cifra nem valor aleatório.
    // O identificador comercial anterior não é usado no resultado, mas o ID interno permanece reconhecível.
    public String anonymizeNumericId(int id, String prefix, int totalLength) {
        String idText = String.valueOf(id);
        int zerosNeeded =
                totalLength
                        - prefix.length()
                        - idText.length();
        if (zerosNeeded < 0) {
            throw new IllegalArgumentException(
                    "O identificador não cabe no tamanho informado."
            );
        }
        return prefix
                + "0".repeat(zerosNeeded)
                + idText;
    }
}
