package com.mottainai.mottainaicrud.controller;

import jakarta.servlet.ServletContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

// NAVEGAÇÃO PÚBLICA — /sobre-nos, /planos, /faq E /login
// Mantém Sobre Nós/Planos existentes e abre o Login na Home, inclusive após redirects de sessão/autenticação.

@Controller
public class PublicPageViewController {
    private final ServletContext servletContext;

    public PublicPageViewController(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    @GetMapping("/sobre-nos")
    public String about() { return existingPage("sobre-nos"); }

    @GetMapping("/planos")
    public String plans() { return existingPage("planos"); }

    @GetMapping("/faq")
    public String faq() { return existingPage("faq"); }

    @GetMapping("/login")
    public String login(Model model) {
        // O modal reutiliza POST /login; a flag seleciona somente sua apresentação, sem autenticar pela navegação.
        model.addAttribute("abrirLogin", true);
        return existingPage("home");
    }

    // VERIFICAÇÃO DA VIEW PÚBLICA
    // JSP ausente resulta em 404; falha de leitura é técnica e não é confundida com regra de negócio.
    private String existingPage(String view) {
        // Não inventa uma página nem encaminha para um JSP inexistente (erro 500).
        try (var page = servletContext.getResourceAsStream("/WEB-INF/jsp/" + view + ".jsp")) {
            if (page == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Página pública ainda não disponível.");
            }
        } catch (java.io.IOException ex) {
            throw new IllegalStateException("Não foi possível verificar a página pública.", ex);
        }
        return view;
    }
}
