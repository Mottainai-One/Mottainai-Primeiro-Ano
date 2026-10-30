package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.UserAccountRequestDTO;
import com.mottainai.mottainaicrud.dto.UserAccountResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user-accounts")
public class UserAccountViewController {

    private final UserAccountService userAccountService;

    public UserAccountViewController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODAS AS CONTAS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("accounts", userAccountService.findAll());
        return "user-accounts";
    }

    // EXIBE A PAGINA DE DETALHES DE UMA CONTA ESPECIFICA
    @GetMapping("/{userId}")
    public String detail(@PathVariable int userId, Model model) {
        model.addAttribute("account", userAccountService.findById(userId));
        return "user-account-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("accountForm", new UserAccountRequestDTO());
        model.addAttribute("isEdit", false);
        return "user-account-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{userId}/edit")
    public String editForm(@PathVariable int userId, Model model) {
        UserAccountResponseDTO existing = userAccountService.findById(userId);

        UserAccountRequestDTO form = new UserAccountRequestDTO();
        form.setEmail(existing.getEmail());
        form.setEmployeeId(existing.getEmployeeId());
        // SENHA EM BRANCO NO FORM DE EDICAO

        model.addAttribute("accountForm", form);
        model.addAttribute("userId", userId);
        model.addAttribute("isEdit", true);
        return "user-account-form";
    }

    // PROCESSA O FORMULARIO PARA CRIAR UMA NOVA CONTA
    @PostMapping
    public String create(@Valid @ModelAttribute("accountForm") UserAccountRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "user-account-form";
        }
        try {
            userAccountService.create(dto);
        } catch (DuplicateResourceException | ResourceNotFoundException | IllegalArgumentException e){
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "user-account-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Conta criada com sucesso!");
        return "redirect:/user-accounts";
    }

    // PROCESSA O FORMULARIO PARA ATUALIZAR UMA CONTA EXISTENTE
    @PostMapping("/{userId}/update")
    public String update(@PathVariable int userId,
                         @Valid @ModelAttribute("accountForm") UserAccountRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("userId", userId);
            model.addAttribute("isEdit", true);
            return "user-account-form";
        }
        try {
            userAccountService.update(userId, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException | IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("isEdit", true);
            return "user-account-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Conta atualizada com sucesso!");
        return "redirect:/user-accounts";
    }

    // PROCESSA A EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{userId}/delete")
    public String delete(@PathVariable int userId, RedirectAttributes redirectAttributes) {
        try {
            userAccountService.delete(userId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Conta removida com sucesso!"
            );
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/user-accounts";
    }
}