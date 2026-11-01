package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.AddressRequestDTO;
import com.mottainai.mottainaicrud.dto.AddressResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.service.AddressService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/addresses")
public class AddressViewController {

    private final AddressService addressService;

    public AddressViewController(AddressService addressService) {
        this.addressService = addressService;
    }

    // EXIBE A PAGINA DE LISTAGEM COM TODOS OS ENDERECOS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("addresses", addressService.findAll());
        return "addresses";
    }

    // EXIBE A PAGINA DE DETALHES DE UM ENDERECO ESPECIFICO
    @GetMapping("/{addressId}")
    public String detail(@PathVariable Integer addressId, Model model) {
        model.addAttribute("address", addressService.findById(addressId));
        return "address-detail";
    }

    // EXIBE O FORMULARIO EM BRANCO PARA CADASTRO
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("addressForm", new AddressRequestDTO());
        model.addAttribute("isEdit", false);
        return "address-form";
    }

    // CARREGA OS DADOS EXISTENTES E EXIBE O FORMULARIO EM MODO DE EDICAO
    @GetMapping("/{addressId}/edit")
    public String editForm(@PathVariable Integer addressId, Model model) {
        AddressResponseDTO existing = addressService.findById(addressId);

        AddressRequestDTO form = new AddressRequestDTO();
        form.setZipCode(existing.getZipCode());
        form.setStreet(existing.getStreet());
        form.setNumber(existing.getNumber());
        form.setComplement(existing.getComplement());
        form.setNeighborhood(existing.getNeighborhood());
        form.setCity(existing.getCity());
        form.setState(existing.getState());

        model.addAttribute("addressForm", form);
        model.addAttribute("addressId", addressId);
        model.addAttribute("isEdit", true);
        return "address-form";
    }

    // PROCESSA O FORMULARIO PARA CRIAR UM NOVO ENDERECO
    @PostMapping
    public String create(@Valid @ModelAttribute("addressForm") AddressRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "address-form";
        }
        addressService.create(dto);
        redirectAttributes.addFlashAttribute("mensagem", "Endereço cadastrado com sucesso!");
        return "redirect:/addresses";
    }

    // PROCESSA O FORMULARIO PARA ATUALIZAR UM ENDERECO EXISTENTE
    @PostMapping("/{addressId}/update")
    public String update(@PathVariable Integer addressId,
                         @Valid @ModelAttribute("addressForm") AddressRequestDTO dto,
                         BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("addressId", addressId);
            model.addAttribute("isEdit", true);
            return "address-form";
        }
        try {
            addressService.update(addressId, dto);
        } catch (ResourceNotFoundException e) {
            model.addAttribute("erro", e.getMessage());
            model.addAttribute("addressId", addressId);
            model.addAttribute("isEdit", true);
            return "address-form";
        }
        redirectAttributes.addFlashAttribute("mensagem", "Endereço atualizado com sucesso!");
        return "redirect:/addresses";
    }

    // PROCESSA A EXCLUSAO E REDIRECIONA PARA A LISTAGEM
    @PostMapping("/{addressId}/delete")
    public String delete(@PathVariable Integer addressId, RedirectAttributes redirectAttributes) {
        try {
            addressService.delete(addressId);
            redirectAttributes.addFlashAttribute(
                    "mensagem",
                    "Endereço removido com sucesso!"
            );
        } catch (DuplicateResourceException | ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }
        return "redirect:/addresses";
    }
}