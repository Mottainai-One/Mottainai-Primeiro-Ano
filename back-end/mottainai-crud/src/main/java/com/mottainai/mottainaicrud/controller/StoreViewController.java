package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.StoreRequestDTO;
import com.mottainai.mottainaicrud.dto.StoreResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.StoreService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/stores")
public class StoreViewController {

    private final StoreService storeService;

    public StoreViewController(StoreService storeService) {
        this.storeService = storeService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODAS AS LOJAS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("stores", storeService.findAll());
        return "stores";
    }

    // EXIBE A PAGINA DE DETALHES DE UMA LOJA ESPECIFICA
    @GetMapping("/{storeId}")
    public String detail(@PathVariable int storeId, Model model) {
        model.addAttribute("store", storeService.findById(storeId));
        return "store-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("storeForm", new StoreRequestDTO());
        model.addAttribute("isEdit", false);
        return "store-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{storeId}/edit")
    public String editForm(@PathVariable int storeId, Model model) {
        StoreResponseDTO existing = storeService.findById(storeId);

        StoreRequestDTO form = new StoreRequestDTO();
        form.setName(existing.getName());
        form.setCnpj(existing.getCnpj());
        form.setEmail(existing.getEmail());
        form.setPhone(existing.getPhone());
        form.setCompanyId(existing.getCompanyId());
        form.setAddressId(existing.getAddressId());
        model.addAttribute("storeForm", form);
        model.addAttribute("storeId", storeId);
        model.addAttribute("isEdit", true);
        return "store-form";
    }

    // PROCESSA O FORMULARIO PARA CRIAR UMA NOVA LOJA
    @PostMapping
    public String create(@Valid @ModelAttribute("storeForm") StoreRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "store-form";
        }
        try {
            storeService.create(dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "store-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Loja cadastrada com sucesso!");
        return "redirect:/stores";
    }

    // PROCESSA O FORMULARIO PARA ATUALIZAR UMA LOJA EXISTENTE
    @PostMapping("/{storeId}/update")
    public String update(@PathVariable int storeId,
                         @Valid @ModelAttribute("storeForm") StoreRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("storeId", storeId);
            model.addAttribute("isEdit", true);
            return "store-form";
        }
        try {
            storeService.update(storeId, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("storeId", storeId);
            model.addAttribute("isEdit", true);
            return "store-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Loja atualizada com sucesso!");
        return "redirect:/stores";
    }

    // PROCESSA A EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{storeId}/delete")
    public String delete(@PathVariable int storeId, RedirectAttributes redirectAttributes) {
        try {
            storeService.delete(storeId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Loja removida com sucesso!"
            );
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/stores";
    }
}