package com.mottainai.mottainaicrud;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

// IMPLANTAÇÃO DO WAR: conecta a aplicação Spring a um contêiner Servlet externo.
// Não é um Servlet de CRUD: as requisições são encaminhadas aos controllers pelo Spring MVC.
public class ServletInitializer extends SpringBootServletInitializer {

    // Indica a classe principal ao contêiner, reutilizando a mesma configuração do main.
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MottainaiCrudApplication.class);
    }

}
