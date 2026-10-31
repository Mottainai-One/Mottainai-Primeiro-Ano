package com.mottainai.mottainaicrud.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

// CONFIGURAÇÃO DAS VIEWS E ROTAS ADMINISTRATIVAS
// Centraliza o interceptor de sessão, a resolução de JSPs e o caminho adicional de recursos públicos.

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final com.mottainai.mottainaicrud.service.AdministrationService administrations;

    public WebConfig(com.mottainai.mottainaicrud.service.AdministrationService administrations) {
        this.administrations = administrations;
    }


    // PROTEÇÃO DOS CAMINHOS ADMINISTRATIVOS
    // Os padrões abrangem telas e APIs; as rotas públicas e os assets não são incluídos nesta lista.
    @Override
    public void addInterceptors(org.springframework.web.servlet.config.annotation.InterceptorRegistry registry) {
        registry.addInterceptor(new com.mottainai.mottainaicrud.security.AdministrationSessionInterceptor(administrations))
                .addPathPatterns("/inicio", "/inicio/**", "/empresas/**", "/lojas/**", "/enderecos/**",
                        "/funcionarios/**", "/cargos/**", "/contas/**", "/planos-administrativos/**",
                        "/api/**", "/administrations/**");
    }

    // RESOLUÇÃO DAS VIEWS
    // O nome retornado pelo ViewController é combinado com /WEB-INF/jsp/ e a extensão .jsp.
    @Bean
    public ViewResolver viewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

    // Serve arquivos de /webapp/resources/** como /resources/** nas URLs
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**")
                .addResourceLocations("/resources/");
    }
}
