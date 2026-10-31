package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.UserAccountRequestDTO;
import com.mottainai.mottainaicrud.dto.UserAccountResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.service.EmployeeService;
import com.mottainai.mottainaicrud.service.UserAccountService;
import static com.mottainai.mottainaicrud.util.ViewFilterUtils.matchesSearch;
import static com.mottainai.mottainaicrud.util.ViewFilterUtils.matchesStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.mottainaicrud.util.ViewPaginationUtils.addPage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA DE CONTAS DE FUNCIONÁRIOS — /contas
// Retorna user-accounts.jsp e as opções de Employee; trata apenas as responsabilidades atuais desse CRUD.

@Controller
@RequestMapping("/contas")
public class UserAccountViewController {

    private final UserAccountService userAccountService;
    private final EmployeeService employeeService;

    public UserAccountViewController(
            UserAccountService userAccountService,
            EmployeeService employeeService) {

        this.userAccountService = userAccountService;
        this.employeeService = employeeService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza texto de email e dados do funcionário; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza contas e os atributos contaCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<UserAccountResponseDTO> accounts =
                userAccountService.findAll()
                        .stream()
                        .filter(account ->
                                matchesSearch(
                                        busca,
                                        account.getEmail(),
                                        account.getEmployeeName(),
                                        account.getStoreName(),
                                        account.getRoleName()
                                )
                        )
                        .filter(account ->
                                matchesStatus(
                                        account.isActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "contas",
                accounts, pagina, UserAccountResponseDTO::getUserId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            model.addAttribute(
                    "contaCriar",
                    new UserAccountRequestDTO()
            );

            addFormOptions(
                    model,
                    null
            );

        } else if ("ver".equals(acao)
                && id != null) {

            model.addAttribute(
                    "contaVer",
                    userAccountService.findById(id)
            );

        } else if ("editar".equals(acao)
                && id != null) {

            model.addAttribute(
                    "contaEditar",
                    userAccountService.findById(id)
            );

            addFormOptions(
                    model,
                    id
            );

        } else if ("excluir".equals(acao)
                && id != null) {

            model.addAttribute(
                    "contaExcluir",
                    userAccountService.findById(id)
            );
        }

        return "user-accounts";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute UserAccountRequestDTO dto,
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

                userAccountService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Conta cadastrada com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Conta não informada."
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

                userAccountService.update(
                        id,
                        dto
                );

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Conta atualizada com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Conta não informada."
                    );
                }

                userAccountService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Conta excluída com sucesso!"
                );
            }

        } catch (
                DuplicateResourceException
                | ResourceNotFoundException
                | BusinessRuleException
                | IllegalArgumentException e) {

            redirectAttributes.addFlashAttribute(
                    "erro",
                    e.getMessage()
            );
        }

        return "redirect:/contas";
    }

    // FUNCIONÁRIOS DISPONÍVEIS PARA A CONTA
    // Exclui funcionários apagados e os vinculados a outra conta; a conta atual pode manter seu próprio vínculo.
    private void addFormOptions(
            Model model,
            Integer currentUserId) {

        // Só mostra funcionários:
        // 1. que não foram excluídos;
        // 2. que ainda não possuem outra conta.
        model.addAttribute(
                "funcionariosDisponiveis",
                employeeService.findAll()
                        .stream()
                        .filter(employee ->
                                employee.getDeletedAt() == null
                        )
                        .filter(employee ->
                                userAccountService
                                        .isEmployeeAvailable(
                                                employee.getEmployeeId(),
                                                currentUserId
                                        )
                        )
                        .toList()
        );
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            UserAccountRequestDTO dto) {

        addPage(model, "contas",
                userAccountService.findAll(), 1, UserAccountResponseDTO::getUserId);

        model.addAttribute(
                "contaCriar",
                dto
        );

        addFormOptions(
                model,
                null
        );

        return "user-accounts";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer userId) {

        addPage(model, "contas",
                userAccountService.findAll(), 1, UserAccountResponseDTO::getUserId);

        model.addAttribute(
                "contaEditar",
                userAccountService.findById(userId)
        );

        addFormOptions(
                model,
                userId
        );

        return "user-accounts";
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