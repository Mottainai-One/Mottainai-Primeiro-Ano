package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AdministrationRequestDTO;
import com.mottainai.mottainaicrud.dto.AdministrationResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.AdministrationService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/administrations")
public class AdministrationViewController {

    private final AdministrationService administrationService;

    public AdministrationViewController(AdministrationService administrationService) {
        this.administrationService = administrationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("administrations", administrationService.findAll());
        return "administrations";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable int id, Model model) {
        model.addAttribute("administration", administrationService.findById(id));
        return "administration-detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("administrationForm", new AdministrationRequestDTO());
        model.addAttribute("isEdit", false);
        return "administration-form";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable int id, Model model) {
        AdministrationResponseDTO existing = administrationService.findById(id);

        AdministrationRequestDTO form = new AdministrationRequestDTO();
        form.setEmail(existing.getEmail());
        // A SENHA NAO EH RETORNADA NO RESPONSE
        // SE FICAR VAZIA DURANTE A EDICAO, O SERVICE MANTEM A SENHA ATUAL

        model.addAttribute("administrationForm", form);
        model.addAttribute("administrationId", id);
        model.addAttribute("isEdit", true);
        return "administration-form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("administrationForm") AdministrationRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        // PERMITE MANTER A SENHA ATUAL SE O CAMPO VIER VAZIO NA EDICAO
        boolean passwordEmpty = dto.getPasswordHash() == null || dto.getPasswordHash().isBlank();
        boolean hasErrors = result.getFieldErrors().stream()
                .anyMatch(err -> !passwordEmpty || !err.getField().equals("passwordHash"));

        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "administration-form";
        }
        try {
            administrationService.create(dto);
        } catch (DuplicateResourceException | IllegalArgumentException e){
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "administration-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Administração cadastrada com sucesso!");
        return "redirect:/administrations";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable int id,
                         @Valid @ModelAttribute("administrationForm") AdministrationRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("administrationId", id);
            model.addAttribute("isEdit", true);
            return "administration-form";
        }
        try {
            administrationService.update(id, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("administrationId", id);
            model.addAttribute("isEdit", true);
            return "administration-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Administração atualizada com sucesso!");
        return "redirect:/administrations";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            administrationService.delete(id);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Administração removida com sucesso!"
            );
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/administrations";
    }
}