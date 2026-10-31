package com.mottainai.mottainaicrud.service;

import com.mottainai.mottainaicrud.dao.CompanyDao;
import com.mottainai.mottainaicrud.dao.EmployeeDao;
import com.mottainai.mottainaicrud.dao.StoreDao;
import com.mottainai.mottainaicrud.dao.UserAccountDao;
import com.mottainai.mottainaicrud.security.DataEncryptionService;
import static com.mottainai.mottainaicrud.util.AnonymizationConstants.TECHNICAL_RECORD_ID;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// CONSOLIDACAO DOS CARDS E CADASTROS RECENTES DO PAINEL
// AGREGACOES E PROJECOES — SEM findAll DE ENTIDADES COMPLETAS; AES SOMENTE NOS NOMES DE PESSOAS

@Service
public class DashboardService {

    private final CompanyDao companies;
    private final StoreDao stores;
    private final EmployeeDao employees;
    private final UserAccountDao accounts;
    // AES USADO SOMENTE PARA DESCRIPTOGRAFAR NOMES DE FUNCIONARIOS E CONTAS
    private final DataEncryptionService encryption;

    public DashboardService(CompanyDao companies,
                            StoreDao stores,
                            EmployeeDao employees,
                            UserAccountDao accounts,
                            DataEncryptionService encryption) {
        this.companies = companies;
        this.stores = stores;
        this.employees = employees;
        this.accounts = accounts;
        this.encryption = encryption;
    }

    // CARREGA O PAINEL SEM BUSCAR ENTIDADES COMPLETAS — SOMENTE AGREGACOES E PROJECOES
    @Transactional(readOnly = true)
    public DashboardData load() {
        var totals = new LinkedHashMap<String, Long>();
        addSummary(totals, "Empresas", companies.summarizeDashboard(TECHNICAL_RECORD_ID));
        addSummary(totals, "Lojas", stores.summarizeDashboard(TECHNICAL_RECORD_ID));
        addSummary(totals, "Funcionarios", employees.summarizeDashboard());
        addSummary(totals, "Contas", accounts.summarizeDashboard());

        // LIMITE NO BANCO: 10 RECENTES DE CADA CATEGORIA BASTAM PARA COMPOR OS 10 GLOBAIS
        var page = PageRequest.of(0, 10);
        var recent = new ArrayList<RecentData>();
        addRecent(recent, companies.findDashboardRecent(TECHNICAL_RECORD_ID, page), "Empresa", false);
        addRecent(recent, stores.findDashboardRecent(TECHNICAL_RECORD_ID, page), "Loja", false);
        addRecent(recent, employees.findDashboardRecent(page), "Funcionario", true);
        addRecent(recent, accounts.findDashboardRecent(page), "Conta de usuario", true);

        var top = recent.stream()
                .sorted(Comparator.comparing(RecentData::date,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .toList();

        return new DashboardData(totals, top);
    }

    // MONTA OS ATRIBUTOS DE TOTAIS, ATIVOS E INATIVOS POR CATEGORIA
    private void addSummary(Map<String, Long> model,
                            String category,
                            com.mottainai.mottainaicrud.dao.DashboardCount count) {
        model.put("total" + category, count.getTotal());
        var suffix = category.equals("Empresas")
                || category.equals("Lojas")
                || category.equals("Contas");
        model.put("total" + category + (suffix ? "Ativas" : "Ativos"), count.getActive());
        model.put("total" + category + (suffix ? "Inativas" : "Inativos"),
                count.getTotal() - count.getActive());
    }

    // DESCRIPTOGRAFA NOME SOMENTE SE O REGISTRO USAR AES (FUNCIONARIO E CONTA)
    private void addRecent(List<RecentData> target,
                           List<com.mottainai.mottainaicrud.dao.DashboardRegistration> rows,
                           String type,
                           boolean encrypted) {
        for (var row : rows) {
            var name = row.getName();
            // EMPRESA PODE USAR RAZAO SOCIAL COMO ALTERNATIVA SE NOME FANTASIA FOR NULL
            if (name == null || name.isBlank()) name = row.getFallbackName();
            if (encrypted) name = encryption.decrypt(name);
            target.add(new RecentData(name, type, row.getCreatedAt(), row.isActive()));
        }
    }

    // DADOS PREPARADOS PARA APRESENTACAO — SEM ENTIDADES JPA OU CREDENCIAIS
    public record RecentData(String name, String type, LocalDateTime date, boolean active) {}
    public record DashboardData(Map<String, Long> totals, List<RecentData> recent) {}
}
