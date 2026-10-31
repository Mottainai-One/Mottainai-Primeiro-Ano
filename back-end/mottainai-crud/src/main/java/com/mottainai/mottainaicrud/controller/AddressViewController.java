package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.AddressRequestDTO;
import com.mottainai.crudinicial.dto.AddressResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import com.mottainai.crudinicial.service.AddressService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesSearchById;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA DE ENDEREÇOS — /enderecos
// Retorna addresses.jsp com filtros e modais; o cadastro técnico é retirado da listagem normal.

@Controller
@RequestMapping("/enderecos")
public class AddressViewController {

    private final AddressService addressService;

    public AddressViewController(
            AddressService addressService) {

        this.addressService = addressService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza enderecos e os atributos enderecoCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<AddressResponseDTO> addresses =
                addressService.findAll()
                        .stream()
                        .filter(address ->
                                address.getAddressId()
                                        != TECHNICAL_RECORD_ID
                        )
                        .filter(address ->
                                matchesSearchById(
                                        busca,
                                        address.getAddressId(),
                                        address.getZipCode(),
                                        address.getStreet(),
                                        address.getNumber(),
                                        address.getNeighborhood(),
                                        address.getCity(),
                                        address.getState(),
                                        address.getStoreName()
                                )
                        )
                        .toList();

        addPage(model, "enderecos",
                addresses, pagina, AddressResponseDTO::getAddressId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            model.addAttribute(
                    "enderecoCriar",
                    new AddressRequestDTO()
            );

        } else if ("ver".equals(acao)
                && id != null) {

            model.addAttribute(
                    "enderecoVer",
                    addressService.findById(id)
            );

        } else if ("editar".equals(acao)
                && id != null) {

            model.addAttribute(
                    "enderecoEditar",
                    addressService.findById(id)
            );

        } else if ("excluir".equals(acao)
                && id != null) {

            model.addAttribute(
                    "enderecoExcluir",
                    addressService.findById(id)
            );
        }

        return "addresses";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute AddressRequestDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {

            if ("cadastrar".equals(acao)) {

                if (result.hasErrors()) {

                    addValidationErrors(
                            result,
                            model
                    );

                    return showCreateWithErrors(
                            model,
                            dto
                    );
                }

                addressService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Endereço cadastrado com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Endereço não informado."
                    );
                }

                if (result.hasErrors()) {

                    addValidationErrors(
                            result,
                            model
                    );

                    return showEditWithErrors(
                            model,
                            id
                    );
                }

                addressService.update(
                        id,
                        dto
                );

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Endereço atualizado com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Endereço não informado."
                    );
                }

                addressService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Endereço excluído com sucesso!"
                );
            }

        } catch (
                DuplicateResourceException
                | ResourceNotFoundException
                | BusinessRuleException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/enderecos";
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            AddressRequestDTO dto) {

        addPage(model, "enderecos",
                addressService.findAll()
                        .stream()
                        .filter(address ->
                                address.getAddressId()
                                        != TECHNICAL_RECORD_ID
                        )
                        .toList(), 1, AddressResponseDTO::getAddressId);

        model.addAttribute(
                "enderecoCriar",
                dto
        );

        return "addresses";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer addressId) {

        addPage(model, "enderecos",
                addressService.findAll()
                        .stream()
                        .filter(address ->
                                address.getAddressId()
                                        != TECHNICAL_RECORD_ID
                        )
                        .toList(), 1, AddressResponseDTO::getAddressId);

        model.addAttribute(
                "enderecoEditar",
                addressService.findById(
                        addressId
                )
        );

        return "addresses";
    }

    // MENSAGENS DE VALIDAÇÃO PARA O JSP
    // fieldErrors associa campo à primeira mensagem encontrada, como esperado pelos componentes já existentes.
    private void addValidationErrors(
            BindingResult result,
            Model model) {

        Map<String, String> fieldErrors =
                result.getFieldErrors()
                        .stream()
                        .collect(
                                Collectors.toMap(
                                        FieldError::getField,
                                        FieldError::getDefaultMessage,
                                        (first, second) -> first
                                )
                        );

        model.addAttribute(
                "fieldErrors",
                fieldErrors
        );
    }
}
