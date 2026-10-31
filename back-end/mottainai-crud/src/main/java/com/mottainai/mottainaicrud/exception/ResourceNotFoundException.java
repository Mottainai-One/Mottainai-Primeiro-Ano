package com.mottainai.crudinicial.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// RECURSO INEXISTENTE — HTTP 404
// Representa a ausência do cadastro procurado; a anotação e o handler preservam o status de não encontrado.

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}