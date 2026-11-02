package com.mottainai.mottainaicrud.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

// VALIDAÇÃO DE CPF NA ENTRADA
// A anotação ValidCPF usa tamanho, repetição de dígitos e cálculo dos verificadores; obrigatoriedade fica no NotBlank.

public class CpfValidator implements ConstraintValidator<ValidCPF, String> {

    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Campo nulo/vazio não é responsabilidade deste validador
        // (use @NotBlank junto para isso — separação de responsabilidades)
        if (cpf == null || cpf.isBlank()) {
            return true;
        }

        // Valida se o CPF possui 11 dígitos ou a formatação padrão, rejeitando letras e símbolos indevidos.
        if (!cpf.matches("\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}")) {
            return false;
        }
        // Remove qualquer formatação (pontos, traço), caso o front envie "123.456.789-00"
        String cleanCpf = cpf.replaceAll("[^0-9]", "");

        // CPF tem exatamente 11 dígitos
        if (cleanCpf.length() != 11) {
            return false;
        }

        // Rejeita sequências repetidas (ex: "11111111111"), que passam na fórmula mas são inválidas
        if (cleanCpf.chars().distinct().count() == 1) {
            return false;
        }

        return validateCheckDigits(cleanCpf);
    }

    // Calcula os dois dígitos verificadores e compara com os que vieram no CPF
    private boolean validateCheckDigits(String cpf) {
        int[] digits = cpf.chars().map(Character::getNumericValue).toArray();

        int firstCheckDigit = calculateCheckDigit(digits, 9, 10);
        if (firstCheckDigit != digits[9]) {
            return false;
        }

        int secondCheckDigit = calculateCheckDigit(digits, 10, 11);
        return secondCheckDigit == digits[10];
    }

    // Fórmula oficial do CPF: multiplica cada dígito por um peso decrescente,
    // soma tudo, tira o resto da divisão por 11, e ajusta conforme a regra
    private int calculateCheckDigit(int[] digits, int length, int weightStart) {
        int sum = 0;
        for (int i = 0; i < length; i++) {
            sum += digits[i] * (weightStart - i);
        }
        int remainder = sum % 11;
        return (remainder < 2) ? 0 : (11 - remainder);
    }
}