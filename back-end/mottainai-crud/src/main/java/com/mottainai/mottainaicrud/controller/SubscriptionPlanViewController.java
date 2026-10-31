package com.mottainai.mottainaicrud.controller;

import com.mottainai.mottainaicrud.dto.SubscriptionPlanRequestDTO;
import com.mottainai.mottainaicrud.dto.SubscriptionPlanResponseDTO;
import com.mottainai.mottainaicrud.exception.DuplicateResourceException;
import com.mottainai.mottainaicrud.exception.ResourceNotFoundException;
import com.mottainai.mottainaicrud.exception.BusinessRuleException;
import com.mottainai.mottainaicrud.service.SubscriptionPlanService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.mottainaicrud.util.ViewPaginationUtils.addPage;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import static com.mottainai.mottainaicrud.util.ViewFilterUtils.matchesSearchById;
import static com.mottainai.mottainaicrud.util.ViewFilterUtils.matchesStatus;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA ADMINISTRATIVA DE PLANOS — /planos-administrativos
// Retorna subscription-plans.jsp; essa gestão é separada da rota pública /planos.

@Controller
@RequestMapping("/planos-administrativos")
public class SubscriptionPlanViewController {

    private final SubscriptionPlanService subscriptionPlanService;

    public SubscriptionPlanViewController(
            SubscriptionPlanService subscriptionPlanService) {

        this.subscriptionPlanService = subscriptionPlanService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza planos e os atributos planoCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<SubscriptionPlanResponseDTO> plans =
                subscriptionPlanService.findAll()
                        .stream()
                        .filter(plan ->
                                matchesSearchById(
                                        busca,
                                        plan.getPlanId(),
                                        plan.getName(),
                                        plan.getDescription()
                                )
                        )
                        .filter(plan ->
                                matchesStatus(
                                        plan.getActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "planos", plans, pagina, SubscriptionPlanResponseDTO::getPlanId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            model.addAttribute(
                    "planoCriar",
                    new SubscriptionPlanRequestDTO()
            );

        } else if ("ver".equals(acao) && id != null) {

            model.addAttribute(
                    "planoVer",
                    subscriptionPlanService.findById(id)
            );

        } else if ("editar".equals(acao) && id != null) {

            model.addAttribute(
                    "planoEditar",
                    subscriptionPlanService.findById(id)
            );

        } else if ("excluir".equals(acao) && id != null) {

            model.addAttribute(
                    "planoExcluir",
                    subscriptionPlanService.findById(id)
            );
        }

        return "subscription-plans";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute SubscriptionPlanRequestDTO dto,
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

                subscriptionPlanService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Plano cadastrado com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Plano não informado."
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

                subscriptionPlanService.update(id, dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Plano atualizado com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Plano não informado."
                    );
                }

                subscriptionPlanService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Plano excluído com sucesso!"
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

        return "redirect:/planos-administrativos";
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            SubscriptionPlanRequestDTO dto) {

        addPage(model, "planos",
                subscriptionPlanService.findAll(), 1, SubscriptionPlanResponseDTO::getPlanId);

        model.addAttribute(
                "planoCriar",
                dto
        );

        return "subscription-plans";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer planId) {

        addPage(model, "planos",
                subscriptionPlanService.findAll(), 1, SubscriptionPlanResponseDTO::getPlanId);

        model.addAttribute(
                "planoEditar",
                subscriptionPlanService.findById(planId)
        );

        return "subscription-plans";
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
