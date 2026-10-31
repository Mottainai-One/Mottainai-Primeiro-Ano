package com.mottainai.mottainaicrud.exception;

// Lançada quando uma operação viola uma regra de duplicidade do sistema.
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}