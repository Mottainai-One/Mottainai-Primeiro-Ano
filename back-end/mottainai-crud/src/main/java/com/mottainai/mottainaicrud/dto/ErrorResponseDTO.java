package com.mottainai.mottainaicrud.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

// FORMATO DE ERRO DAS APIS
// Concentra status, mensagem, instante e caminho da requisição; o handler decide o conteúdo seguro de cada resposta.

@Getter
@Setter
public class ErrorResponseDTO {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ErrorResponseDTO(int status, String error, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}