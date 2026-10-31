package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.CompanyRequestDTO;
import com.mottainai.crudinicial.dto.CompanyResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import com.mottainai.crudinicial.service.CompanyService;
import com.mottainai.crudinicial.service.SubscriptionPlanService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_COMPANY_CNPJ;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesSearchById;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesStatus;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA DE EMPRESAS — /empresas
// Retorna companies.jsp e os planos disponíveis; as regras de vínculo e anonimização ficam no Service.

@Controller
@RequestMapping("/empresas")
public class CompanyViewController {

    private final CompanyService companyService;
    private final SubscriptionPlanService subscriptionPlanService;

    public CompanyViewController(
            CompanyService companyService,
            SubscriptionPlanService subscriptionPlanService) {

        this.companyService = companyService;
        this.subscriptionPlanService = subscriptionPlanService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza empresas e os atributos empresaCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<CompanyResponseDTO> companies =
                companyService.findAll()
                        .stream()
                        .filter(company ->
                                !TECHNICAL_COMPANY_CNPJ.equals(
                                        company.getCnpj()
                                )
                        )
                        .filter(company ->
                                matchesSearchById(
                                        busca,
                                        company.getCompanyId(),
                                        company.getTradeName(),
                                        company.getOfficialName(),
                                        company.getCnpj(),
                                        company.getEmail(),
                                        company.getSubscriptionPlanName()
                                )
                        )
                        .filter(company ->
                                matchesStatus(
                                        company.isActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "empresas",
                companies, pagina, CompanyResponseDTO::getCompanyId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            addFormOptions(model);

        } else if ("ver".equals(acao) && id != null) {

            model.addAttribute(
                    "empresaVer",
                    companyService.findByCompanyId(id)
            );

        } else if ("editar".equals(acao) && id != null) {

            model.addAttribute(
                    "empresaEditar",
                    companyService.findByCompanyId(id)
            );

            addFormOptions(model);

        } else if ("excluir".equals(acao) && id != null) {

            model.addAttribute(
                    "empresaExcluir",
                    companyService.findByCompanyId(id)
            );
        }

        return "companies";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute CompanyRequestDTO dto,
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

                companyService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Empresa cadastrada com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Empresa não informada."
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

                companyService.update(id, dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Empresa atualizada com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Empresa não informada."
                    );
                }

                companyService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Empresa excluída com sucesso!"
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

        return "redirect:/empresas";
    }

    // OPÇÕES DE PLANO PARA O FORMULÁRIO
    // Inclui somente planos não excluídos; a validação do vínculo é repetida no Service ao persistir.
    private void addFormOptions(Model model) {

        // Exibe no select apenas planos que não foram excluídos.
        model.addAttribute(
                "planosDisponiveis",
                subscriptionPlanService.findAll()
                        .stream()
                        .filter(plan -> plan.getDeletedAt() == null)
                        .toList()
        );
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            CompanyRequestDTO dto) {

        addPage(model, "empresas",
                companyService.findAll()
                        .stream()
                        .filter(company ->
                                !TECHNICAL_COMPANY_CNPJ.equals(
                                        company.getCnpj()
                                )
                        )
                        .toList(), 1, CompanyResponseDTO::getCompanyId);

        model.addAttribute(
                "empresaCriar",
                dto
        );

        addFormOptions(model);

        return "companies";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer companyId) {

        addPage(model, "empresas",
                companyService.findAll()
                        .stream()
                        .filter(company ->
                                !TECHNICAL_COMPANY_CNPJ.equals(
                                        company.getCnpj()
                                )
                        )
                        .toList(), 1, CompanyResponseDTO::getCompanyId);

        model.addAttribute(
                "empresaEditar",
                companyService.findByCompanyId(companyId)
        );

        addFormOptions(model);

        return "companies";
    }

    // MENSAGENS DE VALIDAÇÃO PARA O JSP
    // fieldErrors associa campo à primeira mensagem encontrada, como esperado pelos componentes já existentes.
    private void addValidationErrors(
            BindingResult result,
            Model model) {

        // Converte os erros do @Valid em um mapa:
        // nomeDoCampo -> mensagemDoErro.
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
