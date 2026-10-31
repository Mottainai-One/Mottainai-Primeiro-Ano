package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// PAINEL ADMINISTRATIVO — /inicio
// Transforma o resultado leve do DashboardService nos atributos e registros esperados pelo JSP existente.

@Controller
public class HomeCrudViewController {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private final DashboardService dashboardService;

    public HomeCrudViewController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    // MODELO DO PAINEL
    // Mantém os nomes dos totais e cadastrosRecentes usados pelo JSP, sem refazer consultas dos CRUDs completos.
    @GetMapping("/inicio")
    public String homeCrud(@RequestParam(defaultValue = "1") int pagina, Model model) {
        var data = dashboardService.load();
        model.addAllAttributes(data.totals());
        addPage(model, "cadastrosRecentes", data.recent().stream().map(row ->
                new RecentRegistration(row.name(), row.type(), row.date(), row.active())).toList(), pagina);
        return "home-crud-page";
    }

    // REGISTRO DE APRESENTAÇÃO
    // Os getters expõem nome/tipo/situação e data formatada para a expressão EL do JSP, sem entidade JPA.
    public static class RecentRegistration {

        private final String nome;
        private final String tipo;
        private final LocalDateTime data;
        private final boolean ativo;

        public RecentRegistration(
                String nome,
                String tipo,
                LocalDateTime data,
                boolean ativo) {

            this.nome = nome;
            this.tipo = tipo;
            this.data = data;
            this.ativo = ativo;
        }

        public String getNome() {
            return nome;
        }

        public String getTipo() {
            return tipo;
        }

        public LocalDateTime getData() {
            return data;
        }

        public boolean isAtivo() {
            return ativo;
        }

        public String getDataFormatada() {

            if (data == null) {
                return "";
            }

            return data.format(DATE_FORMATTER);
        }
    }
}