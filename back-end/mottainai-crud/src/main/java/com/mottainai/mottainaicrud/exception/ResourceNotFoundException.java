package com.mottainai.mottainaicrud.exception;

// LANCADA QUANDO UM RECURSO BUSCADO POR ID/CPF/CNPJ NAO EXISTE NO BANCO
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}