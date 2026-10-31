package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.EmployeeRoleRequestDTO;
import com.mottainai.crudinicial.dto.EmployeeRoleResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesSearchById;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesStatus;
import com.mottainai.crudinicial.service.EmployeeRoleService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA DE CARGOS — /cargos
// Retorna employee-roles.jsp com busca e situação; o cargo reservado à anonimização fica oculto.

@Controller
@RequestMapping("/cargos")
public class EmployeeRoleViewController {

    private final EmployeeRoleService employeeRoleService;

    public EmployeeRoleViewController(
            EmployeeRoleService employeeRoleService) {

        this.employeeRoleService = employeeRoleService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza cargos e os atributos cargoCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<EmployeeRoleResponseDTO> roles =
                employeeRoleService.findAll()
                        .stream()
                        .filter(role ->
                                !TECHNICAL_ROLE_NAME.equals(
                                        role.getName()
                                )
                        )
                        .filter(role ->
                                matchesSearchById(
                                        busca,
                                        role.getRoleId(),
                                        role.getName(),
                                        role.getDescription()
                                )
                        )
                        .filter(role ->
                                matchesStatus(
                                        role.isActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "cargos",
                roles, pagina, EmployeeRoleResponseDTO::getRoleId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            model.addAttribute(
                    "cargoCriar",
                    new EmployeeRoleRequestDTO()
            );

        } else if ("ver".equals(acao) && id != null) {

            model.addAttribute(
                    "cargoVer",
                    employeeRoleService.findById(id)
            );

        } else if ("editar".equals(acao) && id != null) {

            model.addAttribute(
                    "cargoEditar",
                    employeeRoleService.findById(id)
            );

        } else if ("excluir".equals(acao) && id != null) {

            model.addAttribute(
                    "cargoExcluir",
                    employeeRoleService.findById(id)
            );
        }

        return "employee-roles";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute EmployeeRoleRequestDTO dto,
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

                employeeRoleService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Cargo cadastrado com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Cargo não informado."
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

                employeeRoleService.update(id, dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Cargo atualizado com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Cargo não informado."
                    );
                }

                employeeRoleService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Cargo excluído com sucesso!"
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

        return "redirect:/cargos";
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            EmployeeRoleRequestDTO dto) {

        addPage(model, "cargos",
                employeeRoleService.findAll()
                        .stream()
                        .filter(role ->
                                !TECHNICAL_ROLE_NAME.equals(
                                        role.getName()
                                )
                        )
                        .toList(), 1, EmployeeRoleResponseDTO::getRoleId);

        model.addAttribute(
                "cargoCriar",
                dto
        );

        return "employee-roles";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer roleId) {

        addPage(model, "cargos",
                employeeRoleService.findAll()
                        .stream()
                        .filter(role ->
                                !TECHNICAL_ROLE_NAME.equals(
                                        role.getName()
                                )
                        )
                        .toList(), 1, EmployeeRoleResponseDTO::getRoleId);

        model.addAttribute(
                "cargoEditar",
                employeeRoleService.findById(roleId)
        );

        return "employee-roles";
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
