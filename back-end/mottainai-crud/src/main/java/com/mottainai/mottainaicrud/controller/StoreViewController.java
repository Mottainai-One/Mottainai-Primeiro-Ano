package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dto.StoreRequestDTO;
import com.mottainai.crudinicial.dto.StoreResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import com.mottainai.crudinicial.service.AddressService;
import com.mottainai.crudinicial.service.CompanyService;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_COMPANY_CNPJ;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;
import com.mottainai.crudinicial.service.StoreService;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesSearchById;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesStatus;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import java.util.List;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// TELA DE LOJAS — /lojas
// Retorna stores.jsp com empresa e endereço relacionados; os selects respeitam exclusão e ocupação do endereço.

@Controller
@RequestMapping("/lojas")
public class StoreViewController {

    private final StoreService storeService;
    private final CompanyService companyService;
    private final AddressService addressService;

    public StoreViewController(StoreService storeService, CompanyService companyService, AddressService addressService) {
        this.storeService = storeService;
        this.companyService = companyService;
        this.addressService = addressService;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza lojas e os atributos lojaCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        // Por enquanto a paginação continua simples.
        // Depois podemos mover busca e paginação para o banco.
        List<StoreResponseDTO> stores =
                storeService.findAll()
                        .stream()
                        .filter(store ->
                                !TECHNICAL_STORE_CNPJ.equals(
                                        store.getCnpj()
                                )
                        )
                        .filter(store ->
                                matchesSearchById(
                                        busca,
                                        store.getStoreId(),
                                        store.getName(),
                                        store.getCompanyName(),
                                        store.getCnpj(),
                                        store.getEmail()
                                )
                        )
                        .filter(store ->
                                matchesStatus(
                                        store.isActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "lojas",
                stores, pagina, StoreResponseDTO::getStoreId);

        if ("criar".equals(acao)) {

            addFormOptions(
                    model,
                    null
            );

        } else if ("ver".equals(acao) && id != null) {

            model.addAttribute(
                    "lojaVer",
                    storeService.findById(id)
            );

        } else if ("editar".equals(acao) && id != null) {

            StoreResponseDTO store = storeService.findById(id);

            // O JSP usa o próprio DTO de resposta
            // para preencher os valores do modal.
            model.addAttribute(
                    "lojaEditar",
                    store
            );

            addFormOptions(
                    model,
                    id
            );

        } else if ("excluir".equals(acao) && id != null) {

            model.addAttribute(
                    "lojaExcluir",
                    storeService.findById(id)
            );
        }

        return "stores";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute StoreRequestDTO dto,
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

                storeService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Loja cadastrada com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Loja não informada."
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

                storeService.update(id, dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Loja atualizada com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Loja não informada."
                    );
                }

                storeService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Loja excluída com sucesso!"
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

        return "redirect:/lojas";
    }

    // EMPRESAS E ENDEREÇOS DISPONÍVEIS
    // A consulta em lote informa os endereços ocupados e preserva o endereço da própria loja na edição.
    private void addFormOptions(
            Model model,
            Integer currentStoreId) {

        // Empresas excluídas não podem receber novas lojas.
        model.addAttribute(
                "empresasDisponiveis",
                companyService.findAll()
                        .stream()
                        .filter(company ->
                                company.getDeletedAt() == null
                        )
                        .filter(company ->
                                !TECHNICAL_COMPANY_CNPJ.equals(
                                        company.getCnpj()
                                )
                        )
                        .toList()
        );

        // Só mostra endereços:
        // 1. que não foram excluídos;
        // 2. que não pertencem a outra loja.
        var unavailableAddressIds = storeService.findUnavailableAddressIds(currentStoreId);
        model.addAttribute(
                "enderecosDisponiveis",
                addressService.findAll()
                        .stream()
                        .filter(address ->
                                address.getDeletedAt() == null
                        )
                        .filter(address ->
                                !unavailableAddressIds.contains(address.getAddressId())
                        )
                        .toList()
        );
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            StoreRequestDTO dto) {

        addPage(model, "lojas",
                storeService.findAll()
                        .stream()
                        .filter(store ->
                                !TECHNICAL_STORE_CNPJ.equals(
                                        store.getCnpj()
                                )
                        )
                        .toList(), 1, StoreResponseDTO::getStoreId);

        model.addAttribute(
                "lojaCriar",
                dto
        );

        addFormOptions(
                model,
                null
        );

        return "stores";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer storeId) {

        addPage(model, "lojas",
                storeService.findAll()
                        .stream()
                        .filter(store ->
                                !TECHNICAL_STORE_CNPJ.equals(
                                        store.getCnpj()
                                )
                        )
                        .toList(), 1, StoreResponseDTO::getStoreId);

        model.addAttribute(
                "lojaEditar",
                storeService.findById(storeId)
        );

        addFormOptions(
                model,
                storeId
        );

        return "stores";
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
