package com.mottainai.crudinicial.controller;

import com.mottainai.crudinicial.dao.EmployeeRoleDao;
import com.mottainai.crudinicial.dao.StoreDao;
import com.mottainai.crudinicial.dto.EmployeeRequestDTO;
import com.mottainai.crudinicial.dto.EmployeeResponseDTO;
import com.mottainai.crudinicial.exception.DuplicateResourceException;
import com.mottainai.crudinicial.exception.ResourceNotFoundException;
import com.mottainai.crudinicial.exception.BusinessRuleException;
import com.mottainai.crudinicial.service.EmployeeService;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesSearchById;
import static com.mottainai.crudinicial.util.ViewFilterUtils.matchesStatus;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import static com.mottainai.crudinicial.util.ViewPaginationUtils.addPage;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

// TELA DE FUNCIONÁRIOS — /funcionarios
// Retorna employees.jsp com filtros e opções de loja/cargo; dados pessoais chegam à tela por DTOs.

@Controller
@RequestMapping("/funcionarios")
public class EmployeeViewController {

    private final EmployeeService employeeService;
    private final StoreDao storeDao;
    private final EmployeeRoleDao employeeRoleDao;

    public EmployeeViewController(
            EmployeeService employeeService,
            StoreDao storeDao,
            EmployeeRoleDao employeeRoleDao) {

        this.employeeService = employeeService;
        this.storeDao = storeDao;
        this.employeeRoleDao = employeeRoleDao;
    }

    // LISTAGEM, FILTROS E MODAIS
    // busca utiliza ID exato e texto; acao/id selecionam consulta, edição ou confirmação de exclusão.
    // Conforme a ação e a validação, o fluxo utiliza funcionarios e os atributos funcionarioCriar/Ver/Editar/Excluir utilizados pelo JSP.
    @GetMapping
    public String list(
            @RequestParam(required = false) String acao,
            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String situacao,
            @RequestParam(defaultValue = "1") int pagina,
            Model model) {

        List<EmployeeResponseDTO> employees =
                employeeService.findAll()
                        .stream()
                        .filter(employee ->
                                matchesSearchById(
                                        busca,
                                        employee.getEmployeeId(),
                                        employee.getName(),
                                        employee.getEmail(),
                                        employee.getStoreName(),
                                        employee.getRoleName()
                                )
                        )
                        .filter(employee ->
                                matchesStatus(
                                        employee.isActive(),
                                        situacao
                                )
                        )
                        .toList();

        addPage(model, "funcionarios",
                employees, pagina, EmployeeResponseDTO::getEmployeeId);

        // A tabela é paginada após os filtros; as opções dos formulários continuam completas.

        if ("criar".equals(acao)) {

            model.addAttribute(
                    "funcionarioCriar",
                    new EmployeeRequestDTO()
            );

            addFormOptions(model);

        } else if ("ver".equals(acao)
                && id != null) {

            model.addAttribute(
                    "funcionarioVer",
                    employeeService.findById(id)
            );

        } else if ("editar".equals(acao)
                && id != null) {

            model.addAttribute(
                    "funcionarioEditar",
                    employeeService.findById(id)
            );

            addFormOptions(model);

        } else if ("excluir".equals(acao)
                && id != null) {

            model.addAttribute(
                    "funcionarioExcluir",
                    employeeService.findById(id)
            );
        }

        return "employees";
    }

    // TRATAMENTO DAS AÇÕES DO FORMULÁRIO
    // acao distingue cadastrar, atualizar e excluir; BindingResult contém os erros do RequestDTO validado.
    // Erros de campo reabrem o modal; conflitos conhecidos geram flash erro e o redirect retorna à listagem.
    @PostMapping
    public String handlePost(
            @RequestParam String acao,
            @RequestParam(required = false) Integer id,
            @Valid @ModelAttribute EmployeeRequestDTO dto,
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

                employeeService.create(dto);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Funcionário cadastrado com sucesso!"
                );

            } else if ("atualizar".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Funcionário não informado."
                    );
                }

                if (result.hasErrors()) {

                    addValidationErrors(
                            result,
                            model
                    );

                    return showEditWithErrors(
                            model,
                            id,
                            dto
                    );
                }

                employeeService.update(
                        id,
                        dto
                );

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Funcionário atualizado com sucesso!"
                );

            } else if ("excluir".equals(acao)) {

                if (id == null) {
                    throw new ResourceNotFoundException(
                            "Funcionário não informado."
                    );
                }

                employeeService.delete(id);

                redirectAttributes.addFlashAttribute(
                        "mensagem",
                        "Funcionário excluído com sucesso!"
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

        return "redirect:/funcionarios";
    }

    // VÍNCULOS DO FUNCIONÁRIO
    // As opções excluem lojas/cargos apagados e registros reservados; o Service também valida o vínculo recebido.
    private void addFormOptions(Model model) {

        // Mostra apenas lojas válidas para receber funcionários.
        // A loja técnica de anonimização não aparece no formulário.
        model.addAttribute(
                "lojasDisponiveis",
                storeDao.findAll()
                        .stream()
                        .filter(store ->
                                store.getDeletedAt() == null
                        )
                        .filter(store ->
                                !TECHNICAL_STORE_CNPJ.equals(
                                        store.getCnpj()
                                )
                        )
                        .toList()
        );

        // Mostra apenas cargos válidos.
        // O cargo técnico é reservado para anonimização.
        model.addAttribute(
                "cargosDisponiveis",
                employeeRoleDao.findAll()
                        .stream()
                        .filter(role ->
                                role.getDeletedAt() == null
                        )
                        .filter(role ->
                                !TECHNICAL_ROLE_NAME.equals(
                                        role.getName()
                                )
                        )
                        .toList()
        );
    }

    // REABERTURA DO CADASTRO INVÁLIDO
    // Recompõe a tabela e mantém o RequestDTO no Model para apresentar os erros sem executar o cadastro.
    private String showCreateWithErrors(
            Model model,
            EmployeeRequestDTO dto) {

        addPage(model, "funcionarios",
                employeeService.findAll(), 1, EmployeeResponseDTO::getEmployeeId);

        // Mantém os valores digitados caso haja erro.
        model.addAttribute(
                "funcionarioCriar",
                dto
        );

        addFormOptions(model);

        return "employees";
    }

    // REABERTURA DA EDIÇÃO INVÁLIDA
    // Recarrega o cadastro pelo ID para montar o modal e mantém fieldErrors na mesma renderização.
    private String showEditWithErrors(
            Model model,
            Integer employeeId,
            EmployeeRequestDTO dto) {

        addPage(model, "funcionarios",
                employeeService.findAll(), 1, EmployeeResponseDTO::getEmployeeId);

        // Precisamos manter o ID para o formulário de edição.
        EmployeeResponseDTO employee =
                employeeService.findById(employeeId);

        // PRESERVAÇÃO DO FORMULÁRIO DE EDIÇÃO
        // Após recuperar o ID, reaplica os valores enviados ao DTO para mostrar a entrada inválida sem gravá-la.
        employee.setName(dto.getName());
        employee.setCpf(dto.getCpf());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setHireDate(dto.getHireDate());

        if (dto.getStoreId() != null) {
            employee.setStoreId(
                    dto.getStoreId()
            );
        }

        if (dto.getRoleId() != null) {
            employee.setRoleId(
                    dto.getRoleId()
            );
        }

        if (dto.getActive() != null) {
            employee.setActive(
                    dto.getActive()
            );
        }

        model.addAttribute(
                "funcionarioEditar",
                employee
        );

        addFormOptions(model);

        return "employees";
    }

    // MENSAGENS DE VALIDAÇÃO PARA O JSP
    // fieldErrors associa campo à primeira mensagem encontrada, como esperado pelos componentes já existentes.
    private void addValidationErrors(
            BindingResult result,
            Model model) {

        // Permite que os erros de validação do Spring
        // sejam mostrados ao lado de cada campo no JSP.
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
