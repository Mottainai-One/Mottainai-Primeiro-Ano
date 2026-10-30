package com.mottainai.crudinicial.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

// ANOTAÇÃO DE VALIDAÇÃO DO CPF
// Integra campos/parâmetros ao CpfValidator pelo contrato do Bean Validation, sem modificar o dado informado.

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class) // aponta para a classe que faz a validação de fato
public @interface ValidCPF {

    // Mensagem padrão, usada se nenhuma outra for especificada no campo
    String message() default "CPF inválido";

    // Obrigatórios pelo contrato de uma anotação de Bean Validation
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}