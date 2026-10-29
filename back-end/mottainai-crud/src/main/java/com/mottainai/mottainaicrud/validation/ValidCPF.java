package com.mottainai.mottainaicrud.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

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