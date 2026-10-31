package com.mottainai.mottainaicrud.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

// INTERCEPTAÇÃO DAS ROTAS ADMINISTRATIVAS
// WebConfig aplica esta verificação aos CRUDs e APIs; páginas e arquivos públicos ficam fora desses padrões.

// PROTEÇÃO DE ROTAS: exige a sessão de Administration e não considera uma sessão de UserAccount suficiente.
public class AdministrationSessionInterceptor implements HandlerInterceptor {
    private final com.mottainai.mottainaicrud.service.AdministrationService administrations;

    public AdministrationSessionInterceptor(com.mottainai.mottainaicrud.service.AdministrationService administrations) {
        this.administrations = administrations;
    }

    // VERIFICAÇÃO SEM CRIAR SESSÃO
    // getSession(false) não autentica nem cria estado para visitantes; ID/email e a existência da conta liberam a rota.
    // Sem esses atributos há redirect para /login, respeitando o context path; no-store evita reutilizar a resposta administrativa.
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws java.io.IOException {
        response.setHeader("Cache-Control", "no-store");
        var session = request.getSession(false);
        if (session != null && session.getAttribute("administrationId") instanceof Integer id
                && session.getAttribute("administrationEmail") instanceof String) {
            if (administrations.exists(id)) return true;
            // Não encerra a sessão de quem excluiu; revoga apenas a de uma conta já removida.
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
        return false;
    }
}
