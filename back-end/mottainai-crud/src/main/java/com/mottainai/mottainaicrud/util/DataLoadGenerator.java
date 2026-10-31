package com.mottainai.crudinicial.util;

import com.mottainai.crudinicial.security.DataEncryptionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_COMPANY_CNPJ;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_RECORD_ID;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_ROLE_NAME;
import static com.mottainai.crudinicial.util.AnonymizationConstants.TECHNICAL_STORE_CNPJ;

// GERAÇÃO MANUAL DE SQL PARA DADOS SINTÉTICOS
// Produz data_load_crypto.sql com registros e referências técnicas; não executa os comandos no PostgreSQL.

public class DataLoadGenerator {

    private static final int TOTAL_RECORDS = 500;

    // Seed fixa para manter os dados fictícios reproduzíveis.
    private static final Random RANDOM =
            new Random(20261017L);

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final String TECHNICAL_ADDRESS_ZIP =
            "00000000";

    // Hash BCrypt usado somente nos registros fictícios.
    private static final String TEST_PASSWORD_HASH =
            "$2b$12$IXmU5oLQCmpUXCfYVoJ32GDKlllRwIyGjc9xgNWjLvGbc91EJqWA6";

    private static final String[] PLAN_NAMES = {
            "Free",
            "Basic",
            "Professional",
            "Enterprise",
            "Professional Plan"
    };

    private static final String[] ROLE_NAMES = {
            "Gerente de loja",
            "Supervisor",
            "Estoquista",
            "Caixa",
            "Repositor",
            "Analista"
    };

    private static final String[] COMPANY_PREFIXES = {
            "Estrela",
            "Central",
            "Horizonte",
            "Prisma",
            "Vale",
            "Cedro",
            "Boreal",
            "Planalto"
    };

    private static final String[] COMPANY_AREAS = {
            "Alimentos",
            "Comércio",
            "Construção",
            "Logística",
            "Serviços",
            "Tecnologia"
    };

    private static final String[] COMPANY_TYPES = {
            "Indústria e Comércio LTDA",
            "Comércio e Serviços LTDA",
            "Distribuidora LTDA",
            "Soluções Corporativas LTDA"
    };

    private static final String[] FIRST_NAMES = {
            "Carlos",
            "Mariana",
            "Lucas",
            "Beatriz",
            "Rafael",
            "Camila",
            "Gabriel",
            "Larissa",
            "Matheus",
            "Juliana",
            "Felipe",
            "Ana",
            "Gustavo",
            "Isabela",
            "Pedro",
            "Sofia",
            "Leonardo",
            "Amanda",
            "Bruno",
            "Leticia"
    };

    private static final String[] LAST_NAMES = {
            "Silva",
            "Santos",
            "Oliveira",
            "Souza",
            "Costa",
            "Rodrigues",
            "Almeida",
            "Pereira",
            "Ferreira",
            "Gomes",
            "Martins",
            "Rocha",
            "Ribeiro",
            "Carvalho",
            "Lima"
    };

    // GERAÇÃO DO ARQUIVO DE CARGA
    // Exige a chave AES da aplicação e organiza inserts na ordem das FKs; apenas grava SQL, sem abrir conexão ao banco.
    public static void main(String[] args)
            throws IOException {

        String aesKey =
                System.getenv("MOTTAINAI_AES_KEY");

        if (aesKey == null || aesKey.isBlank()) {
            throw new IllegalStateException(
                    "MOTTAINAI_AES_KEY não encontrada."
            );
        }

        DataEncryptionService encryptionService =
                new DataEncryptionService(aesKey);

        StringBuilder sql =
                new StringBuilder();

        generateReset(sql);
        generateSubscriptionPlans(sql);
        generateAddresses(sql);
        generateEmployeeRoles(sql);
        generateCompanies(sql, encryptionService);
        generateStores(sql, encryptionService);
        generateEmployees(sql, encryptionService);
        generateUserAccounts(sql, encryptionService);
        generateSequenceAdjustments(sql);

        Path output =
                Path.of("data_load_crypto.sql");

        Files.writeString(
                output,
                sql.toString()
        );

        System.out.println(
                "Data load criado em:"
        );

        System.out.println(
                output.toAbsolutePath()
        );
    }

    // REINICIALIZAÇÃO DA BASE DE TESTE NO SQL GERADO
    // Inclui TRUNCATE/RESTART IDENTITY CASCADE das sete tabelas; Administration não é incluída. Não é a exclusão dos CRUDs.
    private static void generateReset(
            StringBuilder sql) {

        // Data load completo para desenvolvimento/testes.
        // Administration não é alterada.
        sql.append(
                "truncate table UserAccount, Employee, Store, Address, "
        );
        sql.append(
                "EmployeeRole, Company, SubscriptionPlan "
        );
        sql.append(
                "restart identity cascade;\n\n"
        );
    }

    // PLANOS DE REFERÊNCIA DA CARGA
    // Os nomes inseridos são utilizados pelas subconsultas que vinculam as empresas fictícias aos planos.
    private static void generateSubscriptionPlans(
            StringBuilder sql) {

        sql.append(
                "insert into SubscriptionPlan "
        );

        sql.append(
                "(Name, Description, Price, StoreLimit, UserLimit, Active, CreatedAt) values\n"
        );

        sql.append(
                "('Free', 'Plano básico para pequenas empresas', 20.00, 1, 3, true, current_timestamp),\n"
        );

        sql.append(
                "('Basic', 'Recursos essenciais para empresas em crescimento', 99.90, 5, 10, true, current_timestamp),\n"
        );

        sql.append(
                "('Professional', 'Recursos avançados para empresas de médio porte', 299.90, 20, 50, true, current_timestamp),\n"
        );

        sql.append(
                "('Enterprise', 'Recursos completos para grandes organizações', 999.90, 100, 999, true, current_timestamp),\n"
        );

        sql.append(
                "('Professional Plan', 'Plano de assinatura com suporte administrativo', 349.90, 8, 24, true, current_timestamp);\n\n"
        );
    }

    // ENDEREÇOS NORMAIS E TÉCNICO
    // Gera endereços para as lojas e a referência técnica do ID já definido nas constantes, preservando o formato do CEP.
    private static void generateAddresses(
            StringBuilder sql) {

        // Primeiro cria os 500 endereços normais.
        sql.append(
                "insert into Address "
        );

        sql.append(
                "(AddressId, ZipCode, Street, Number, Complement, Neighborhood, City, State, CreatedAt) values\n"
        );

        for (int i = 1; i <= TOTAL_RECORDS; i++) {

            String zipCode =
                    String.format(
                            "1%07d",
                            i
                    );

            sql.append("(")
                    .append(regularRecordId(i))
                    .append(", '")
                    .append(zipCode)
                    .append("', 'Rua Exemplo ")
                    .append(i)
                    .append("', '")
                    .append(100 + i)
                    .append("', null, 'Bairro ")
                    .append(((i - 1) % 10) + 1)
                    .append("', 'São Paulo', 'São Paulo', current_timestamp)");

            if (i < TOTAL_RECORDS) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }

        // O endereço técnico sempre possui ID 101.
        sql.append(
                "insert into Address "
        );

        sql.append(
                "(AddressId, ZipCode, Street, Number, Complement, Neighborhood, City, State, CreatedAt) values\n"
        );

        sql.append("(")
                .append(TECHNICAL_RECORD_ID)
                .append(", '")
                .append(TECHNICAL_ADDRESS_ZIP)
                .append(
                        "', 'Registro anonimizado', '0', null, "
                )
                .append(
                        "'Registro anonimizado', 'Registro anonimizado', "
                )
                .append(
                        "'Registro anonimizado', current_timestamp);\n\n"
                );
    }

    // CARGOS E REFERÊNCIA DE ANONIMIZAÇÃO
    // O nome técnico coincide com a busca feita pelo EmployeeService durante a exclusão lógica.
    private static void generateEmployeeRoles(
            StringBuilder sql) {

        // Cria os cargos normais com IDs 1..6.
        sql.append(
                "insert into EmployeeRole "
        );

        sql.append(
                "(Name, Description, PermissionLevel, Active, CreatedAt) values\n"
        );

        for (int i = 0; i < ROLE_NAMES.length; i++) {

            sql.append("('")
                    .append(
                            sqlEscape(
                                    ROLE_NAMES[i]
                            )
                    )
                    .append(
                            "', 'Cargo fictício para testes', "
                    )
                    .append(i + 1)
                    .append(", true, current_timestamp)");

            if (i < ROLE_NAMES.length - 1) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }

        // O cargo técnico sempre possui ID 101.
        sql.append(
                "insert into EmployeeRole "
        );

        sql.append(
                "(RoleId, Name, Description, PermissionLevel, Active, CreatedAt) values\n"
        );

        sql.append("(")
                .append(TECHNICAL_RECORD_ID)
                .append(", '")
                .append(
                        sqlEscape(
                                TECHNICAL_ROLE_NAME
                        )
                )
                .append(
                        "', 'Cargo reservado para registros anonimizados', "
                )
                .append(
                        "1, false, current_timestamp);\n\n"
                );
    }

    // EMPRESAS FICTÍCIAS COM CONTATOS AES
    // Cada empresa aponta para plano por nome; a linha técnica é uma referência distinta usada pela loja técnica.
    private static void generateCompanies(
            StringBuilder sql,
            DataEncryptionService encryptionService) {

        // Cria as 500 empresas normais.
        sql.append(
                "insert into Company "
        );

        sql.append(
                "(CompanyId, OfficialName, TradeName, CNPJ, Email, Phone, Active, CreatedAt, SubscriptionPlanId) values\n"
        );

        for (int i = 1; i <= TOTAL_RECORDS; i++) {

            String prefix =
                    randomItem(COMPANY_PREFIXES);

            String area =
                    randomItem(COMPANY_AREAS);

            String type =
                    randomItem(COMPANY_TYPES);

            String number =
                    String.format("%03d", i);

            String tradeName =
                    prefix
                            + " "
                            + area
                            + " "
                            + number;

            String officialName =
                    tradeName
                            + " "
                            + type;

            String cnpj =
                    generateCnpj(
                            90000000 + i
                    );

            String email =
                    "contato"
                            + number
                            + "@empresa"
                            + number
                            + ".example";

            String phone =
                    generatePhone(i);

            String encryptedEmail =
                    encryptionService.encrypt(email);

            String encryptedPhone =
                    encryptionService.encrypt(phone);

            LocalDateTime createdAt =
                    randomDateTime();

            String planName =
                    PLAN_NAMES[
                            ((i - 1) * PLAN_NAMES.length / TOTAL_RECORDS)
                    ];

            sql.append("(")
                    .append(regularRecordId(i))
                    .append(", '")
                    .append(sqlEscape(officialName))
                    .append("', '")
                    .append(sqlEscape(tradeName))
                    .append("', '")
                    .append(cnpj)
                    .append("', '")
                    .append(encryptedEmail)
                    .append("', '")
                    .append(encryptedPhone)
                    .append("', true, '")
                    .append(
                            createdAt.format(
                                    DATE_TIME_FORMAT
                            )
                    )
                    .append("', ")
                    .append(
                            "(select PlanId from SubscriptionPlan where Name = '"
                    )
                    .append(
                            sqlEscape(
                                    planName
                            )
                    )
                    .append("'))");

            if (i < TOTAL_RECORDS) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }

        // A empresa técnica sempre possui ID 101.
        sql.append(
                "insert into Company "
        );

        sql.append(
                "(CompanyId, OfficialName, TradeName, CNPJ, Email, Phone, Active, CreatedAt, SubscriptionPlanId) values\n"
        );

        sql.append("(")
                .append(TECHNICAL_RECORD_ID)
                .append(
                        ", 'Registro anonimizado', "
                )
                .append(
                        "'Registro anonimizado', '"
                )
                .append(
                        TECHNICAL_COMPANY_CNPJ
                )
                .append(
                        "', 'anonimizado', null, false, current_timestamp, "
                )
                .append(
                        "(select PlanId from SubscriptionPlan where Name = 'Free'));"
                );

        sql.append("\n\n");
    }

    // LOJAS FICTÍCIAS COM FKS VÁLIDAS
    // Resolve empresa/endereço por identificadores gerados e acrescenta a loja técnica que sustentará funcionários anonimizados.
    private static void generateStores(
            StringBuilder sql,
            DataEncryptionService encryptionService) {

        // Cria as 500 lojas normais.
        sql.append(
                "insert into Store "
        );

        sql.append(
                "(StoreId, Name, CNPJ, Email, Phone, Active, CreatedAt, CompanyId, AddressId) values\n"
        );

        for (int i = 1; i <= TOTAL_RECORDS; i++) {

            String number =
                    String.format("%03d", i);

            String name =
                    "Loja " + number;

            String cnpj =
                    generateCnpj(
                            91000000 + i
                    );

            String email =
                    "loja"
                            + number
                            + "@mottainai.example";

            String phone =
                    generatePhone(
                            i + 100
                    );

            String encryptedEmail =
                    encryptionService.encrypt(email);

            String encryptedPhone =
                    encryptionService.encrypt(phone);

            LocalDateTime createdAt =
                    randomDateTime();

            String companyCnpj =
                    generateCnpj(
                            90000000 + i
                    );

            String addressZip =
                    String.format(
                            "1%07d",
                            i
                    );

            sql.append("(")
                    .append(regularRecordId(i))
                    .append(", '")
                    .append(name)
                    .append("', '")
                    .append(cnpj)
                    .append("', '")
                    .append(encryptedEmail)
                    .append("', '")
                    .append(encryptedPhone)
                    .append("', true, '")
                    .append(
                            createdAt.format(
                                    DATE_TIME_FORMAT
                            )
                    )
                    .append("', ")
                    .append(
                            "(select CompanyId from Company where CNPJ = '"
                    )
                    .append(companyCnpj)
                    .append("'), ")
                    .append(
                            "(select AddressId from Address where ZipCode = '"
                    )
                    .append(addressZip)
                    .append("'))");

            if (i < TOTAL_RECORDS) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }

        // A loja técnica sempre possui ID 101.
        // Ela referencia Company 101 e Address 101.
        sql.append(
                "insert into Store "
        );

        sql.append(
                "(StoreId, Name, CNPJ, Email, Phone, Active, CreatedAt, CompanyId, AddressId) values\n"
        );

        sql.append("(")
                .append(TECHNICAL_RECORD_ID)
                .append(
                        ", 'Registro anonimizado', '"
                )
                .append(
                        TECHNICAL_STORE_CNPJ
                )
                .append(
                        "', 'anonimizado', 'anonimizado', false, current_timestamp, "
                )
                .append(
                        TECHNICAL_RECORD_ID
                )
                .append(", ")
                .append(
                        TECHNICAL_RECORD_ID
                )
                .append(");\n\n");
    }

    // FUNCIONÁRIOS COM DADOS PESSOAIS CIFRADOS
    // Gera CPF e datas de teste, cifra os campos exigidos e resolve loja/cargo a partir das referências já inseridas.
    private static void generateEmployees(
            StringBuilder sql,
            DataEncryptionService encryptionService) {

        sql.append(
                "insert into Employee "
        );

        sql.append(
                "(Name, CPF, Email, Phone, Active, HireDate, CreatedAt, StoreId, RoleId) values\n"
        );

        for (int i = 1; i <= TOTAL_RECORDS; i++) {

            String firstName =
                    randomItem(FIRST_NAMES);

            String lastName =
                    randomItem(LAST_NAMES);

            String name =
                    firstName
                            + " "
                            + lastName;

            String cpf =
                    generateCpf(i);

            String email =
                    firstName.toLowerCase()
                            + "."
                            + lastName.toLowerCase()
                            + String.format("%03d", i)
                            + "@mottainai.example";

            String phone =
                    generatePhone(
                            i + 200
                    );

            String encryptedName =
                    encryptionService.encrypt(name);

            String encryptedCpf =
                    encryptionService.encrypt(cpf);

            String encryptedEmail =
                    encryptionService.encrypt(email);

            String encryptedPhone =
                    encryptionService.encrypt(phone);

            LocalDate hireDate =
                    randomDate();

            LocalDateTime createdAt =
                    randomDateTime();

            String storeCnpj =
                    generateCnpj(
                            91000000 + i
                    );

            String roleName =
                    ROLE_NAMES[
                            (i - 1) % ROLE_NAMES.length
                    ];

            sql.append("('")
                    .append(encryptedName)
                    .append("', '")
                    .append(encryptedCpf)
                    .append("', '")
                    .append(encryptedEmail)
                    .append("', '")
                    .append(encryptedPhone)
                    .append("', true, '")
                    .append(
                            hireDate.format(
                                    DATE_FORMAT
                            )
                    )
                    .append("', '")
                    .append(
                            createdAt.format(
                                    DATE_TIME_FORMAT
                            )
                    )
                    .append("', ")
                    .append(
                            "(select StoreId from Store where CNPJ = '"
                    )
                    .append(storeCnpj)
                    .append("'), ")
                    .append(
                            "(select RoleId from EmployeeRole where Name = '"
                    )
                    .append(
                            sqlEscape(
                                    roleName
                            )
                    )
                    .append("'))");

            if (i < TOTAL_RECORDS) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }
    }

    // CONTAS SINTÉTICAS DOS FUNCIONÁRIOS
    // Insere email AES e um hash BCrypt de teste já definido, mantendo a associação por EmployeeId.
    private static void generateUserAccounts(
            StringBuilder sql,
            DataEncryptionService encryptionService) {

        sql.append(
                "insert into UserAccount "
        );

        sql.append(
                "(Email, PasswordHash, Active, CreatedAt, EmployeeId) values\n"
        );

        for (int i = 1; i <= TOTAL_RECORDS; i++) {

            String email =
                    "usuario"
                            + String.format("%03d", i)
                            + "@mottainai.example";

            String encryptedEmail =
                    encryptionService.encrypt(email);

            LocalDateTime createdAt =
                    randomDateTime();

            sql.append("('")
                    .append(encryptedEmail)
                    .append("', '")
                    .append(TEST_PASSWORD_HASH)
                    .append("', true, '")
                    .append(
                            createdAt.format(
                                    DATE_TIME_FORMAT
                            )
                    )
                    .append("', ")
                    .append(i)
                    .append(")");

            if (i < TOTAL_RECORDS) {
                sql.append(",\n");
            } else {
                sql.append(";\n\n");
            }
        }
    }

    // Reserva o ID técnico mesmo quando a carga ultrapassa 100 registros comuns.
    private static int regularRecordId(int index) {
        return index >= TECHNICAL_RECORD_ID ? index + 1 : index;
    }

    // IDs manuais não avançam a sequence; o próximo cadastro deve vir após o maior ID inserido.
    private static void generateSequenceAdjustments(StringBuilder sql) {
        for (String[] column : new String[][] {
                {"company", "companyid"}, {"address", "addressid"},
                {"employeerole", "roleid"}, {"store", "storeid"}}) {
            sql.append("select setval(pg_get_serial_sequence('")
                    .append(column[0]).append("', '").append(column[1])
                    .append("'), (select max(").append(column[1]).append(") from ")
                    .append(column[0]).append("), true);\n");
        }
    }

    // CPF SINTÉTICO COM VERIFICADORES
    // A base numérica recebe dois dígitos em sequência para atender à validação usada nos testes do cadastro.
    private static String generateCpf(
            int index) {

        String base =
                String.format(
                        "%09d",
                        100000000 + index
                );

        int firstDigit =
                calculateCpfDigit(
                        base,
                        10
                );

        String firstPart =
                base + firstDigit;

        int secondDigit =
                calculateCpfDigit(
                        firstPart,
                        11
                );

        return firstPart
                + secondDigit;
    }

    // CÁLCULO DO VERIFICADOR DE CPF
    // A soma com pesos decrescentes e o módulo 11 geram o dígito da etapa, ajustando resultados maiores para zero.
    private static int calculateCpfDigit(
            String value,
            int weight) {

        int sum = 0;

        for (int i = 0; i < value.length(); i++) {

            int digit =
                    Character.getNumericValue(
                            value.charAt(i)
                    );

            sum += digit
                    * (weight - i);
        }

        int result =
                11 - (sum % 11);

        if (result >= 10) {
            return 0;
        }

        return result;
    }

    // CNPJ SINTÉTICO
    // Combina raiz e filial de teste e acrescenta os dois verificadores; não consulta dados de empresas reais.
    private static String generateCnpj(
            int rootNumber) {

        String root =
                String.format(
                        "%08d",
                        rootNumber
                );

        String base =
                root + "0001";

        int firstDigit =
                calculateCnpjDigit(base);

        int secondDigit =
                calculateCnpjDigit(
                        base + firstDigit
                );

        return base
                + firstDigit
                + secondDigit;
    }

    // CÁLCULO DO VERIFICADOR DE CNPJ
    // Seleciona pesos conforme a etapa de 12/13 dígitos e aplica o módulo 11.
    private static int calculateCnpjDigit(
            String value) {

        int[] weights;

        if (value.length() == 12) {

            weights = new int[]{
                    5, 4, 3, 2,
                    9, 8, 7, 6,
                    5, 4, 3, 2
            };

        } else {

            weights = new int[]{
                    6, 5, 4, 3, 2,
                    9, 8, 7, 6,
                    5, 4, 3, 2
            };
        }

        int sum = 0;

        for (int i = 0; i < value.length(); i++) {

            int digit =
                    Character.getNumericValue(
                            value.charAt(i)
                    );

            sum += digit
                    * weights[i];
        }

        int remainder =
                sum % 11;

        if (remainder < 2) {
            return 0;
        }

        return 11 - remainder;
    }

    // TELEFONE SINTÉTICO
    // Usa prefixo e índice para montar os contatos da carga; não recupera informações externas.
    private static String generatePhone(
            int index) {

        return String.format(
                "119%08d",
                index
        );
    }

    // DATAS DO CENÁRIO DE TESTE
    // Restringe o sorteio ao intervalo definido no código e usa a semente fixa do gerador.
    private static LocalDate randomDate() {

        LocalDate start =
                LocalDate.of(
                        2023,
                        1,
                        1
                );

        LocalDate end =
                LocalDate.of(
                        2026,
                        10,
                        17
                );

        long days =
                end.toEpochDay()
                        - start.toEpochDay();

        long randomDays =
                Math.floorMod(
                        RANDOM.nextLong(),
                        days + 1
                );

        return start.plusDays(
                randomDays
        );
    }

    // HORÁRIO DOS REGISTROS SINTÉTICOS
    // Combina a data sorteada com horas/minutos/segundos para exercitar ordenação e apresentação dos recentes.
    private static LocalDateTime randomDateTime() {

        LocalDate date =
                randomDate();

        int hour =
                8 + RANDOM.nextInt(11);

        int minute =
                RANDOM.nextInt(60);

        int second =
                RANDOM.nextInt(60);

        return date.atTime(
                hour,
                minute,
                second
        );
    }

    // VARIAÇÃO DOS DADOS DE TESTE
    // Escolhe itens dos catálogos definidos na própria classe, usando o Random compartilhado da carga.
    private static String randomItem(
            String[] values) {

        return values[
                RANDOM.nextInt(
                        values.length
                )
                ];
    }

    // TEXTO LITERAL DO ARQUIVO SQL
    // Duplica aspas nos valores gerados; não substitui a parametrização usada pelo JdbcTemplate no fluxo HTTP.
    private static String sqlEscape(
            String value) {

        return value.replace(
                "'",
                "''"
        );
    }
}
