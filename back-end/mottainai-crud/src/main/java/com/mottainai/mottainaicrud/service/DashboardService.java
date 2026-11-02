package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.*;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;

// CONSOLIDAÇÃO DOS CARDS E CADASTROS RECENTES
// Usa agregações e projeções para evitar findAll de entidades completas; somente nomes de pessoas exigem AES na resposta.


@Service
public class DashboardService {
    private final CompanyDao companies;
    private final StoreDao stores;
    private final EmployeeDao employees;
    private final UserAccountDao accounts;
    private final DataEncryptionService encryption;

    public DashboardService(CompanyDao companies, StoreDao stores, EmployeeDao employees,
                            UserAccountDao accounts, DataEncryptionService encryption) {
        this.companies = companies;
        this.stores = stores;
        this.employees = employees;
        this.accounts = accounts;
        this.encryption = encryption;
    }

    // LEITURA DO PAINEL SEM CARREGAR CRUDS COMPLETOS
    // Os repositories retiram os apagados dos recentes e agregam os cards; cada categoria fornece no máximo dez candidatos.
    @Transactional(readOnly = true)
    public DashboardData load() {
        var totals = new LinkedHashMap<String, Long>();
        addSummary(totals, "Empresas", companies.summarizeDashboard(TECHNICAL_RECORD_ID));
        addSummary(totals, "Lojas", stores.summarizeDashboard(TECHNICAL_RECORD_ID));
        addSummary(totals, "Funcionarios", employees.summarizeDashboard());
        addSummary(totals, "Contas", accounts.summarizeDashboard());

        // LIMITE NO BANCO: os 10 recentes de cada categoria bastam para compor os 10 globais.
        var page = PageRequest.of(0, 10);
        var recent = new ArrayList<RecentData>();
        addRecent(recent, companies.findDashboardRecent(TECHNICAL_RECORD_ID, page), "Empresa", false);
        addRecent(recent, stores.findDashboardRecent(TECHNICAL_RECORD_ID, page), "Loja", false);
        addRecent(recent, employees.findDashboardRecent(page), "Funcionário", true);
        addRecent(recent, accounts.findDashboardRecent(page), "Conta de usuário", true);
        var top = recent.stream().sorted(Comparator.comparing(RecentData::date,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10).toList();
        return new DashboardData(totals, top);
    }

    // ATRIBUTOS COMPATÍVEIS COM O JSP
    // Preserva os nomes dos cards; inativos são o total menos ativos, incluindo os soft deleted nas agregações.
    private void addSummary(Map<String, Long> model, String category, DashboardCount count) {
        model.put("total" + category, count.getTotal());
        var suffix = category.equals("Empresas") || category.equals("Lojas") || category.equals("Contas");
        model.put("total" + category + (suffix ? "Ativas" : "Ativos"), count.getActive());
        model.put("total" + category + (suffix ? "Inativas" : "Inativos"), count.getTotal() - count.getActive());
    }

    // NOMES DOS RECENTES
    // Empresa pode usar razão social como alternativa; nomes de Employee/conta são descriptografados com o mesmo serviço AES.
    private void addRecent(List<RecentData> target, List<DashboardRegistration> rows, String type, boolean encrypted) {
        for (var row : rows) {
            var name = row.getName();
            if (name == null || name.isBlank()) name = row.getFallbackName();
            if (encrypted) name = encryption.decrypt(name);
            target.add(new RecentData(name, type, row.getCreatedAt(), row.isActive()));
        }
    }

    // DADOS PREPARADOS PARA APRESENTAÇÃO
    // Transportam os campos dos recentes e os totais ao Controller, sem entidades JPA ou credenciais.
    public record RecentData(String name, String type, LocalDateTime date, boolean active) {}
    public record DashboardData(Map<String, Long> totals, List<RecentData> recent) {}
}
