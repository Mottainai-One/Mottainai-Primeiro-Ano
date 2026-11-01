package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.EmployeeRoleRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeRoleResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.EmployeeRoleService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employee-roles")
public class EmployeeRoleViewController {

    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleViewController(EmployeeRoleService employeeRoleService) {
        this.employeeRoleService = employeeRoleService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODOS OS CARGOS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("roles", employeeRoleService.findAll());
        return "employee-roles";
    }

    // EXIBE A PAGINA DE DETALHES DE UM CARGO ESPECIFICO
    @GetMapping("/{roleId}")
    public String detail(@PathVariable int roleId, Model model) {
        model.addAttribute("role", employeeRoleService.findById(roleId));
        return "employee-role-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("roleForm", new EmployeeRoleRequestDTO());
        model.addAttribute("isEdit", false);
        return "employee-role-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{roleId}/edit")
    public String editForm(@PathVariable int roleId, Model model) {
        EmployeeRoleResponseDTO existing = employeeRoleService.findById(roleId);

        EmployeeRoleRequestDTO form = new EmployeeRoleRequestDTO();
        form.setName(existing.getName());
        form.setDescription(existing.getDescription());
        form.setPermissionLevel(existing.getPermissionLevel());

        model.addAttribute("roleForm", form);
        model.addAttribute("roleId", roleId);
        model.addAttribute("isEdit", true);
        return "employee-role-form";
    }

    // PROCESSA O FORMULARIO PARA CRIAR UM NOVO CARGO
    @PostMapping
    public String create(@Valid @ModelAttribute("roleForm") EmployeeRoleRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "employee-role-form";
        }
        try {
            employeeRoleService.create(dto);
        } catch (DuplicateResourceException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "employee-role-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Cargo cadastrado com sucesso!");
        return "redirect:/employee-roles";
    }

    // PROCESSA O FORMULARIO PARA ATUALIZAR UM CARGO EXISTENTE
    @PostMapping("/{roleId}/update")
    public String update(@PathVariable int roleId,
                         @Valid @ModelAttribute("roleForm") EmployeeRoleRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roleId", roleId);
            model.addAttribute("isEdit", true);
            return "employee-role-form";
        }
        try {
            employeeRoleService.update(roleId, dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("roleId", roleId);
            model.addAttribute("isEdit", true);
            return "employee-role-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Cargo atualizado com sucesso!");
        return "redirect:/employee-roles";
    }

    // PROCESSA A EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{roleId}/delete")
    public String delete(@PathVariable int roleId, RedirectAttributes redirectAttributes) {
        try {
            employeeRoleService.delete(roleId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Cargo removido com sucesso!"
            );
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/employee-roles";
    }
}