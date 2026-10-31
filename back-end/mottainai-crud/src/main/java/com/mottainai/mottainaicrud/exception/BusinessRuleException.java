package com.mottainai.mottainaicrud.exception;

// REGRAS DE NEGÓCIO: uma operação proibida é conflito do usuário, não falha de configuração.
public class BusinessRuleException extends RuntimeException {

    public BusinessRuleException(String message) {
        super(message);
    }
}
