package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/subscription-plans")
public class SubscriptionPlanViewController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanViewController(SubscriptionPlanService subscriptionPlanService) {
        this.subscriptionPlanService = subscriptionPlanService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODOS OS PLANOS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("plans", subscriptionPlanService.findAll());
        return "subscription-plans";
    }

    // EXIBE A PAGINA DE DETALHES DE UM PLANO ESPECIFICO
    @GetMapping("/{planId}")
    public String detail(@PathVariable int planId, Model model) {
        model.addAttribute("plan", subscriptionPlanService.findById(planId));
        return "subscription-plan-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("planForm", new SubscriptionPlanRequestDTO());
        model.addAttribute("isEdit", false);
        return "subscription-plan-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{planId}/edit")
    public String editForm(@PathVariable int planId, Model model) {
        SubscriptionPlanResponseDTO existing = subscriptionPlanService.findById(planId);

        SubscriptionPlanRequestDTO form = new SubscriptionPlanRequestDTO();
        form.setName(existing.getName());
        form.setDescription(existing.getDescription());
        form.setPrice(existing.getPrice());
        form.setStoreLimit(existing.getStoreLimit());
        form.setUserLimit(existing.getUserLimit());

        model.addAttribute("planForm", form);
        model.addAttribute("planId", planId);
        model.addAttribute("isEdit", true);
        return "subscription-plan-form";
    }

    // PROCESSA O FORMULARIO PARA CRIAR UM NOVO PLANO
    @PostMapping
    public String create(@Valid @ModelAttribute("planForm") SubscriptionPlanRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "subscription-plan-form";
        }
        try {
            subscriptionPlanService.create(dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "subscription-plan-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Plano cadastrado com sucesso!");
        return "redirect:/subscription-plans";
    }

    // PROCESSA O FORMULARIO PARA ATUALIZAR UM PLANO EXISTENTE
    @PostMapping("/{planId}/update")
    public String update(@PathVariable int planId,
                         @Valid @ModelAttribute("planForm") SubscriptionPlanRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("planId", planId);
            model.addAttribute("isEdit", true);
            return "subscription-plan-form";
        }
        try {
            subscriptionPlanService.update(planId, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("planId", planId);
            model.addAttribute("isEdit", true);
            return "subscription-plan-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Plano atualizado com sucesso!");
        return "redirect:/subscription-plans";
    }

    // PROCESSA A EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{planId}/delete")
    public String delete(@PathVariable int planId, RedirectAttributes redirectAttributes) {
        try {
            subscriptionPlanService.delete(planId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Plano removido com sucesso!"
            );
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/subscription-plans";
    }
}