// JSP/MVC

package com.mottainai.mottainaicrud.controller;

// IMPORTACOES: DTOS, EXCECOES, SERVICE, VALIDACAO E JPA
import com.mottainai.mottainaicrud.dao.EmployeeRoleDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dto.EmployeeRequestDTO;
import com.mottainai.mottainaicrud.dto.EmployeeResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/employees")
public class EmployeeViewController {
    private final EmployeeService employeeService;
    private final StoreDao storeDao;
    private final EmployeeRoleDao employeeRoleDao;


    public EmployeeViewController(EmployeeService employeeService, StoreDao storeDao, EmployeeRoleDao employeeRoleDao) {
        this.employeeService = employeeService;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;
    }

    // LISTAGEM
    @GetMapping
    public String list(Model model) {
        model.addAttribute("employees", employeeService.findAll());
        return "employees";
    }

    // DETALHES
    @GetMapping("/{employeeId}")
    public String detail(@PathVariable Integer employeeId, Model model) {
        model.addAttribute("employee", employeeService.findById(employeeId));
        return "employee-details";
    }

    // FORMULARIO DE CRIACAO (VAZIO)
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("employeeForm", new EmployeeRequestDTO());
        model.addAttribute("isEdit", false);
        return "employee-form";
    }

    // FORMULARIO DE EDICAO (PRE-PREENCHIDO)
    @GetMapping("/{employeeId}/edit")
    public String editForm(@PathVariable Integer employeeId, Model model) {
        EmployeeResponseDTO existing = employeeService.findById(employeeId);

        // RESPONSE DTO E REQUEST DTO SAO OBJETOS DIFERENTES, ENTAO CONVERTE MANUALMENTE
        EmployeeRequestDTO form = new EmployeeRequestDTO();
        form.setCpf(existing.getCpf());
        form.setName(existing.getName());
        form.setEmail(existing.getEmail());
        form.setPhone(existing.getPhone());
        form.setHireDate(existing.getHireDate());
        form.setStoreId(existing.getStoreId());
        form.setRoleId(existing.getRoleId());

        model.addAttribute("employeeForm", form);
        model.addAttribute("employeeId", employeeId);
        model.addAttribute("isEdit", true);
        return "employee-form";
    }

    // CRIAR (SUBMIT DO FORM DE CRIACAO)
    @PostMapping
    public String create(@Valid @ModelAttribute("employeeForm") EmployeeRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "employee-form";
        }
        try {
            employeeService.create(dto);
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("isEdit", false);
            return "employee-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Funcionário cadastrado com sucesso!");
        return "redirect:/employees";
    }

    // ATUALIZAR (SUBMIT DO FORM DE EDICAO)
    @PostMapping("/{employeeId}/update")
    public String update(@PathVariable Integer employeeId,
                         @Valid @ModelAttribute("employeeForm") EmployeeRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("isEdit", true);
            return "employee-form";
        }
        try {
            employeeService.update(employeeId, dto);
        } catch(DuplicateResourceException | ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("employeeId", employeeId);
            model.addAttribute("isEdit", true);
            return "employee-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Funcionário atualizado com sucesso!");
        return "redirect:/employees";
    }

    // EXCLUIR
    @PostMapping("/{employeeId}/delete")
    public String delete(@PathVariable Integer employeeId, RedirectAttributes redirectAttributes) {
        try {
            employeeService.delete(employeeId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Funcionário removido com sucesso!"
            );
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/employees";
    }
}