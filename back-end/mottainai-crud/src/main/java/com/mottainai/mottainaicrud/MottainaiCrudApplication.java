package com.mottainai.mottainaicrud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// PONTO DE ENTRADA: inicia o Spring, a configuração web e o acesso ao PostgreSQL.
public class MottainaiCrudApplication {

    // Executado pelo IntelliJ ou bootRun; os argumentos permitem configurar porta/perfil.
    public static void main(String[] args) {
        SpringApplication.run(MottainaiCrudApplication.class, args);
    }

}
