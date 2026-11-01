package com.mottainai.mottainaicrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// HOME PÚBLICA — / E /home
// Entrega a view home sem consultar os CRUDs ou criar autenticação administrativa.

@Controller
public class HomeViewController {

    // NAVEGAÇÃO PÚBLICA
    // As duas URLs entregam home.jsp sem carregar o dashboard /inicio.
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }
}