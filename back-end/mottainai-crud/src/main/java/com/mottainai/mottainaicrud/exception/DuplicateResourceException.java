package com.mottainai.mottainaicrud.exception;

// LANCADA QUANDO UMA OPERACAO VIOLA UMA REGRA DE DUPLICIDADE DO SISTEMA.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}