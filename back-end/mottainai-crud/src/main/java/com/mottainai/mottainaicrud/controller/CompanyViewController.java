package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.CompanyRequestDTO;
import com.mottainai.mottainaicrud.dto.CompanyResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.CompanyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/companies")
public class CompanyViewController {

    // INJECAO DE DEPENDENCIA DA CAMADA DE SERVICO
    private final CompanyService companyService;

    // CONSTRUTOR COM INJECAO DE DEPENDENCIA
    public CompanyViewController(CompanyService companyService) {
        this.companyService = companyService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODOS AS EMPRESAS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("companies", companyService.findAll());
        return "companies";
    }

    // EXIBE A PAGINA DE DETALHES DE UMA EMPRESA ESPECIFICA
    @GetMapping("/{companyId}")
    public String detail(@PathVariable int companyId, Model model) {
        model.addAttribute("company", companyService.findByCompanyId(companyId));
        return "company-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO DE UM NOVO REGISTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("companyForm", new CompanyRequestDTO());
        model.addAttribute("isEdit", false);
        return "company-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{companyId}/edit")
    public String editForm(@PathVariable int companyId, Model model) {
        CompanyResponseDTO existing = companyService.findByCompanyId(companyId);

        CompanyRequestDTO form = new CompanyRequestDTO();
        form.setCnpj(existing.getCnpj());
        form.setOfficialName(existing.getOfficialName());
        form.setTradeName(existing.getTradeName());
        form.setEmail(existing.getEmail());
        form.setPhone(existing.getPhone());
        form.setLatitude(existing.getLatitude());
        form.setLongitude(existing.getLongitude());
        form.setSubscriptionPlanId(existing.getSubscriptionPlanId());

        model.addAttribute("companyForm", form);
        model.addAttribute("companyId", companyId);
        model.addAttribute("isEdit", true);
        return "company-form";
    }

    // PROCESSA A SUBMISSAO DO FORMULARIO PARA CRIAR UM NOVO ADMINISTRADOR
    @PostMapping
    public String create(@Valid @ModelAttribute("companyForm") CompanyRequestDTO dto,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "company-form";
        }
        try {
            companyService.create(dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "company-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Empresa cadastrada com sucesso!");
        return "redirect:/companies";
    }

    // PROCESSA A SUBMISSAO DO FORMULARIO PARA ATUALIZAR UM REGISTRO EXISTENTE
    @PostMapping("/{companyId}/update")
    public String update(@PathVariable int companyId,
                          @Valid @ModelAttribute("companyForm") CompanyRequestDTO dto,
                          BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("companyId", companyId);
            model.addAttribute("isEdit", true);
            return "company-form";
        }
        try {
            companyService.update(companyId, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("companyId", companyId);
            model.addAttribute("isEdit", true);
            return "company-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Empresa atualizada com sucesso!");
        return "redirect:/companies";
    }

    // PROCESSA A REQUISICAO DE EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{companyId}/delete")
    public String delete(@PathVariable int companyId, RedirectAttributes redirectAttributes) {
        try {
            companyService.delete(companyId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Empresa removida com sucesso!"
            );
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/companies";
    }
}
