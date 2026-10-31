package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.AdministrationRequestDTO;
import com.mottainai.crudinicial.dto.AdministrationResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.service.AdministrationService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;

// GESTÃO PRIVADA: /administrations/** já está protegido pelo interceptor existente.
@Controller
@RequestMapping("/administrations")
public class AdministrationViewController {
    private final AdministrationService administrationService;

    public AdministrationViewController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String busca,
                       @RequestParam(defaultValue = "1") int pagina, Model model) {
        populate(model, busca, pagina);
        return "administrations";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("administration", administrationService.findById(id));
        populate(model, "", 1);
        model.addAttribute("administrationView", true);
        return "administrations";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        return form(model, false, 0);
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        var existing = administrationService.findById(id);
        var dto = new AdministrationRequestDTO();
        dto.setEmail(existing.getEmail());
        model.addAttribute("administrationForm", dto);
        return form(model, true, id);
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("administrationForm") AdministrationRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirect) {
        if (!result.hasErrors()) {
            try {
                administrationService.create(dto);
                redirect.addFlashAttribute("mensagem", "Administração cadastrada com sucesso!");
                return "redirect:/administrations";
            } catch (DuplicateResourceException | IllegalArgumentException ex) {
                model.addAttribute("erro", ex.getMessage());
            }
        } else {
            model.addAttribute("erro", "Verifique o email e a senha informados.");
        }
        dto.setPassword(null);
        return form(model, false, 0);
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id,
                         @Valid @ModelAttribute("administrationForm") AdministrationRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirect, HttpSession session) {
        if (!result.hasErrors()) {
            try {
                AdministrationResponseDTO updated = administrationService.update(id, dto);
                // O perfil reutiliza os dois atributos da sessão, sem buscar ou expor hash.
                if (Integer.valueOf(id).equals(session.getAttribute("administrationId"))) {
                    session.setAttribute("administrationEmail", updated.getEmail());
                }
                redirect.addFlashAttribute("mensagem", "Administração atualizada com sucesso!");
                return "redirect:/administrations";
            } catch (DuplicateResourceException | IllegalArgumentException ex) {
                model.addAttribute("erro", ex.getMessage());
            }
        } else {
            model.addAttribute("erro", "Verifique o email e a senha informados.");
        }
        dto.setPassword(null);
        return form(model, true, id);
    }

    // A confirmação mantém a busca e a página da listagem; a regra é reaplicada no POST.
    @GetMapping("/{id}/delete")
    public String deleteConfirmation(@PathVariable int id, @RequestParam(defaultValue = "") String busca,
                                     @RequestParam(defaultValue = "1") int pagina, Model model,
                                     HttpSession session, RedirectAttributes redirect) {
        try {
            var target = administrationService.findForDeletion(id, (Integer) session.getAttribute("administrationId"));
            populate(model, busca, pagina);
            model.addAttribute("administrationDelete", target);
            return "administrations";
        } catch (com.mottainai.crudinicial.exception.BusinessRuleException
                 | com.mottainai.crudinicial.exception.ResourceNotFoundException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
            return deletionRedirect(busca, pagina, redirect);
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, @RequestParam(defaultValue = "") String busca,
                         @RequestParam(defaultValue = "1") int pagina, HttpSession session,
                         RedirectAttributes redirect) {
        try {
            administrationService.delete(id, (Integer) session.getAttribute("administrationId"));
            redirect.addFlashAttribute("mensagem", "Administração excluída com sucesso!");
        } catch (com.mottainai.crudinicial.exception.BusinessRuleException
                 | com.mottainai.crudinicial.exception.ResourceNotFoundException ex) {
            redirect.addFlashAttribute("erro", ex.getMessage());
        }
        return deletionRedirect(busca, pagina, redirect);
    }

    private String deletionRedirect(String busca, int pagina, RedirectAttributes redirect) {
        if (!busca.isBlank()) redirect.addAttribute("busca", busca);
        if (pagina > 1) redirect.addAttribute("pagina", pagina);
        return "redirect:/administrations";
    }

    // Apenas DTOs sem credenciais chegam à listagem; o GET continua sendo a busca real.
    private void populate(Model model, String busca, int pagina) {
        String term = busca.strip().toLowerCase(Locale.ROOT);
        addPage(model, "administrations", administrationService.findAll().stream()
                .filter(admin -> term.isEmpty() || admin.getEmail().toLowerCase(Locale.ROOT).contains(term)
                        || String.valueOf(admin.getAdministrationId()).contains(term))
                .toList(), pagina, AdministrationResponseDTO::getAdministrationId);
        if (!model.containsAttribute("administrationForm")) {
            model.addAttribute("administrationForm", new AdministrationRequestDTO());
        }
    }

    // Uma única view mantém os modais e nunca preenche o campo de senha na edição.
    private String form(Model model, boolean edit, int id) {
        populate(model, "", 1);
        model.addAttribute("administrationFormOpen", true);
        model.addAttribute("isEdit", edit);
        model.addAttribute("administrationId", id);
        return "administrations";
    }
}
