package com.mottainai.mottainaicrud;

import com.mottainai.mottainaicrud.dao.*;
import com.mottainai.mottainaicrud.dto.*;
import com.mottainai.mottainaicrud.security.*;
import com.mottainai.mottainaicrud.service.*;
import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import jakarta.persistence.EntityManagerFactory;
import java.math.BigDecimal;
import java.net.*;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.test.context.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
// Integração real de serviços, APIs e JSPs em PostgreSQL temporário; não usa a base da Aiven.
class CrudIntegrationTests {
    private static EmbeddedPostgres postgres;
    @DynamicPropertySource
    static void database(org.springframework.test.context.DynamicPropertyRegistry registry) throws Exception {
        postgres = EmbeddedPostgres.builder().setPort(0).start();
        new ResourceDatabasePopulator(new ClassPathResource("db/schema.sql"))
                .execute(postgres.getPostgresDatabase());
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl("postgres", "postgres"));
        registry.add("spring.datasource.username", () -> "postgres");
        registry.add("spring.datasource.password", () -> "postgres");
        registry.add("mottainai.crypto.key", () -> Base64.getEncoder().encodeToString(new byte[32]));
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.jpa.properties.hibernate.generate_statistics", () -> "true");
        registry.add("spring.config.import", () -> "");
    }
    @AfterAll static void stopDatabase() throws Exception { if (postgres != null) postgres.close(); }

    @Autowired JdbcTemplate jdbc;
    @Autowired EmployeeService employees;
    @Autowired UserAccountService accounts;
    @Autowired CompanyService companies;
    @Autowired StoreService stores;
    @Autowired AddressService addresses;
    @Autowired EmployeeRoleService roles;
    @Autowired SubscriptionPlanService plans;
    @Autowired AdministrationService admins;
    @Autowired DataEncryptionService crypto;
    @Autowired PasswordHashService passwords;
    @Autowired EntityManagerFactory entityManagerFactory;
    @Autowired jakarta.servlet.ServletContext servletContext;
    @LocalServerPort int port;
    int planId, addressId, companyId, storeId, roleId, adminId;
    HttpClient browser;

    @BeforeEach void resetAndSeed() {
        jdbc.execute("truncate UserAccount, Employee, Store, Address, EmployeeRole, Company, SubscriptionPlan, Administration restart identity cascade");
        var plan=plan("Plano teste"); planId=plans.create(plan).getPlanId();
        jdbc.update("insert into Address(AddressId,ZipCode,Street,Number,Neighborhood,City,State) values(101,'00000000','Registro anonimizado','0','Registro anonimizado','Registro anonimizado','Registro anonimizado')");
        jdbc.update("insert into EmployeeRole(RoleId,Name,PermissionLevel,Active) values(101,'Registro anonimizado',1,false)");
        jdbc.update("insert into Company(CompanyId,OfficialName,CNPJ,Email,Phone,Active,SubscriptionPlanId) values(101,'Registro anonimizado','00000000000000',?,null,false,?)",crypto.encrypt("anonimizado"),planId);
        jdbc.update("insert into Store(StoreId,Name,CNPJ,Email,Phone,Active,CompanyId,AddressId) values(101,'Registro anonimizado','00000000000001',?,?,false,101,101)",crypto.encrypt("anonimizado"),crypto.encrypt("anonimizado"));
        addressId=addresses.create(address()).getAddressId();
        companyId=companies.create(company()).getCompanyId();
        storeId=stores.create(store()).getStoreId();
        roleId=roles.create(role()).getRoleId();
        var admin=new AdministrationRequestDTO(); admin.setEmail("admin@teste.example");admin.setPassword("SenhaTeste@123");
        adminId=admins.create(admin).getAdministrationId();
        browser=HttpClient.newBuilder().cookieHandler(new CookieManager(null,CookiePolicy.ACCEPT_ALL))
                .followRedirects(HttpClient.Redirect.NEVER).build();
    }
    SubscriptionPlanRequestDTO plan(String name) {
        var d=new SubscriptionPlanRequestDTO();d.setName(name);d.setDescription("Plano de teste");
        d.setPrice(new BigDecimal("209.89"));d.setStoreLimit(1000);d.setUserLimit(1000);d.setActive(true);return d;
    }
    AddressRequestDTO address() {
        var d=new AddressRequestDTO();d.setZipCode("12345678");d.setStreet("Rua teste");d.setNumber("1");
        d.setNeighborhood("Centro");d.setCity("São Paulo");d.setState("SP");return d;
    }
    CompanyRequestDTO company() {
        var d=new CompanyRequestDTO();d.setOfficialName("Empresa teste");d.setTradeName("Teste");d.setCnpj("12345678000199");
        d.setEmail("empresa@teste.example");d.setPhone("11999999999");d.setActive(true);d.setSubscriptionPlanId(planId);return d;
    }
    StoreRequestDTO store() {
        var d=new StoreRequestDTO();d.setName("Loja teste");d.setCnpj("22345678000199");d.setEmail("loja@teste.example");
        d.setPhone("11999999999");d.setCompanyId(companyId);d.setAddressId(addressId);d.setActive(true);return d;
    }
    EmployeeRoleRequestDTO role() {
        var d=new EmployeeRoleRequestDTO();d.setName("Cargo teste");d.setDescription("Cargo");d.setPermissionLevel(1);d.setActive(true);return d;
    }
    EmployeeRequestDTO employee(String name) {
        var d=new EmployeeRequestDTO();d.setName(name);d.setCpf("10000000108");d.setEmail("pessoa@teste.example");
        d.setPhone("11999999999");d.setHireDate(LocalDate.of(2023,1,1));d.setStoreId(storeId);d.setRoleId(roleId);d.setActive(true);return d;
    }
    UserAccountRequestDTO account(int employeeId) {
        var d=new UserAccountRequestDTO();d.setEmployeeId(employeeId);d.setEmail("conta@teste.example");
        d.setPassword("SenhaTeste@123");d.setActive(true);return d;
    }
    HttpResponse<String> get(String path) throws Exception {
        return browser.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path)).GET().build(),HttpResponse.BodyHandlers.ofString());
    }
    HttpResponse<String> post(String path,String body) throws Exception {
        return browser.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
                .header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    void login() throws Exception { assertEquals(302,post("/login","email=admin%40teste.example&password=SenhaTeste%40123").statusCode()); }

    @Test void employeeAndAccountCreateReadUpdateDeleteAndAnonymization() {
        int id=employees.create(employee("Pessoa teste")).getEmployeeId();
        assertEquals("Pessoa teste",employees.findById(id).getName());
        var edit=employee("Nome editado");employees.update(id,edit);
        assertEquals("Nome editado",employees.findById(id).getName());
        int userId=accounts.create(account(id)).getUserId();
        assertEquals("Nome editado",accounts.findById(userId).getEmployeeName());
        var update=account(id);update.setEmail("editado@teste.example");accounts.update(userId,update);
        assertEquals("editado@teste.example",accounts.findById(userId).getEmail());
        employees.delete(id);
        assertEquals("Nome editado",employees.findById(id).getName());
        assertEquals("Anonimizado",employees.findById(id).getCpf());
        assertNotNull(accounts.findById(userId).getDeletedAt());
        assertEquals("Anonimizado",accounts.findById(userId).getEmail());
        int second=employees.create(employee("Outra pessoa")).getEmployeeId();
        int other=accounts.create(account(second)).getUserId();accounts.delete(other);
        assertEquals("Anonimizado",accounts.findById(other).getEmail());
    }
    @Test void companyStoreAddressPlanAndRoleCrudAndCascade() {
        var c=company();c.setOfficialName("Empresa editada");companies.update(companyId,c);
        assertEquals("Empresa editada",companies.findByCompanyId(companyId).getOfficialName());
        var s=store();s.setName("Loja editada");stores.update(storeId,s);
        assertEquals("Loja editada",stores.findById(storeId).getName());
        var a=address();a.setStreet("Rua editada");addresses.update(addressId,a);
        assertEquals("Rua editada",addresses.findById(addressId).getStreet());
        var r=role();r.setName("Cargo editado");roles.update(roleId,r);
        assertEquals("Cargo editado",roles.findById(roleId).getName());
        int emp=employees.create(employee("Pessoa cascata")).getEmployeeId();
        int user=accounts.create(account(emp)).getUserId();
        assertThrows(RuntimeException.class,()->roles.delete(roleId));
        assertThrows(RuntimeException.class,()->addresses.delete(addressId));
        assertThrows(RuntimeException.class,()->plans.delete(planId));
        companies.delete(companyId);
        assertNotNull(companies.findByCompanyId(companyId).getDeletedAt());
        assertNotNull(stores.findById(storeId).getDeletedAt());
        assertNotNull(addresses.findById(addressId).getDeletedAt());
        assertNotNull(employees.findById(emp).getDeletedAt());
        assertNotNull(accounts.findById(user).getDeletedAt());
        int spare=roles.create(roleWithName("Sem vinculo")).getRoleId();roles.delete(spare);
        assertNotNull(roles.findById(spare).getDeletedAt());
        int free=addresses.create(address()).getAddressId();addresses.delete(free);
        assertNotNull(addresses.findById(free).getDeletedAt());
        int p=plans.create(plan("Plano livre")).getPlanId();var updated=plan("Plano editado");plans.update(p,updated);
        assertEquals("Plano editado",plans.findById(p).getName());plans.delete(p);
        assertNotNull(plans.findById(p).getDeletedAt());
    }
    EmployeeRoleRequestDTO roleWithName(String name) { var d=role();d.setName(name);return d; }

    @Test void authenticationPagesAndCommaPriceThroughRealHttpAndJsp() throws Exception {
        assertEquals(302,get("/funcionarios").statusCode());
        assertEquals(302,get("/api/employees").statusCode());
        assertEquals(302,post("/login","email=admin%40teste.example&password=errada").statusCode());
        String error=get("/login").body();
        assertTrue(error.contains("login-erro"));assertTrue(error.contains("E-mail ou senha inválidos"));
        login();
        for(String page:List.of("/home","/sobre-nos","/planos","/faq","/inicio","/empresas","/lojas","/enderecos","/funcionarios","/cargos","/contas","/planos-administrativos","/administrations"))
            assertEquals(200,get(page).statusCode(),page);
        assertTrue(get("/assets/css/modais.css").body().contains("#b42318"));
        assertEquals(302,post("/planos-administrativos","acao=cadastrar&name=Plano+Virgula&description=Teste&price=209%2C89&storeLimit=2&userLimit=3&active=true").statusCode());
        assertEquals(new BigDecimal("209.89"),jdbc.queryForObject("select Price from SubscriptionPlan where Name='Plano Virgula'",BigDecimal.class));
        int id=jdbc.queryForObject("select PlanId from SubscriptionPlan where Name='Plano Virgula'",Integer.class);
        assertEquals(302,post("/planos-administrativos","acao=atualizar&id="+id+"&name=Plano+Virgula&description=Teste&price=1.209%2C89&storeLimit=2&userLimit=3&active=true").statusCode());
        assertEquals(new BigDecimal("1209.89"),jdbc.queryForObject("select Price from SubscriptionPlan where PlanId=?",BigDecimal.class,id));
        assertEquals(200,post("/planos-administrativos","acao=atualizar&id="+id+"&name=Plano+Virgula&description=Teste&price=abc&storeLimit=&userLimit=3&active=true").statusCode());
        assertEquals(302,post("/logout","").statusCode());assertEquals(302,get("/contas").statusCode());
    }

    @Test void fiveHundredRowsUseBoundedQueriesAndSearchStillFindsLastRecord() throws Exception {
        String encrypted=crypto.encrypt("Funcionário comum"),cpf=crypto.encrypt("10000000108");
        String email=crypto.encrypt("teste@teste.example"),phone=crypto.encrypt("11999999999");
        jdbc.update("insert into Employee(Name,CPF,Email,Phone,HireDate,StoreId,RoleId) select ?,?,?,?,DATE '2023-01-01',?,? from generate_series(1,499)",encrypted,cpf,email,phone,storeId,roleId);
        int last=employees.create(employee("Ultimo ZYX Busca")).getEmployeeId();
        jdbc.update("insert into UserAccount(Email,PasswordHash,EmployeeId) select ?,?,EmployeeId from Employee",email,passwords.hash("SenhaTeste@123"));
        var stats=entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();long start=System.nanoTime();
        var page=employees.findPage(1,6,null);
        long employeeQueries=stats.getPrepareStatementCount();
        assertEquals(500,page.getTotalElements());assertEquals(6,page.getContent().size());
        assertTrue(employeeQueries<=2,"Consultas de funcionários: "+employeeQueries);
        stats.clear();var userPage=accounts.findPage(1,6,null);long accountQueries=stats.getPrepareStatementCount();
        assertEquals(500,userPage.getTotalElements());assertEquals(6,userPage.getContent().size());
        assertTrue(accountQueries<=2,"Consultas de contas: "+accountQueries);
        assertEquals(84,employees.findPage(999,6,null).getNumber()+1);
        assertEquals(1,employees.findPage(-1,6,null).getNumber()+1);
        jdbc.update("update Employee set Active=false where EmployeeId=?",last);
        assertEquals(1,employees.findPage(1,6,false).getTotalElements());
        login();assertTrue(get("/funcionarios?busca=ZYX").body().contains("Ultimo ZYX Busca"));
        assertTrue(get("/contas?busca=ZYX").body().contains("Ultimo ZYX Busca"));
        assertTrue(get("/funcionarios?busca="+last).body().contains("Ultimo ZYX Busca"));
        stats.clear();assertEquals(200,get("/contas?acao=criar").statusCode());
        assertTrue(stats.getPrepareStatementCount()<=6,"Formulário executou consultas por funcionário: "+stats.getPrepareStatementCount());
        Path report=Path.of("build","performance-verificada.txt");
        Files.writeString(report,"500 funcionários e 500 contas: páginas de 6 registros.\nFuncionários: "+employeeQueries+" consultas.\nContas: "+accountQueries+" consultas.\nBusca encontrou o registro 500 fora da primeira página.\n",StandardCharsets.UTF_8);
    }

    @Test void invalidCipherStillFailsAndAdministrationCrudPreservesSelfProtection() {
        assertThrows(IllegalStateException.class,()->crypto.decrypt("ANON_invalido"));
        var d=new AdministrationRequestDTO();d.setEmail("outro@teste.example");d.setPassword("SenhaTeste@123");
        int id=admins.create(d).getAdministrationId();assertEquals(d.getEmail(),admins.findById(id).getEmail());
        d.setEmail("alterado@teste.example");admins.update(id,d);assertEquals(d.getEmail(),admins.findById(id).getEmail());
        assertThrows(RuntimeException.class,()->admins.delete(adminId,adminId));
        admins.delete(id,adminId);assertThrows(RuntimeException.class,()->admins.findById(id));
    }

    HttpResponse<String> json(String method, String path, String body) throws Exception {
        return browser.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+path))
                .header("Content-Type","application/json")
                .method(method,HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }
    int apiCreate(String path,String idField,String body) throws Exception {
        var created=json("POST",path,body);
        assertEquals(201,created.statusCode(),path+": "+created.body());
        var matcher=java.util.regex.Pattern.compile("\""+idField+"\"\\s*:\\s*(\\d+)").matcher(created.body());
        assertTrue(matcher.find(),created.body());return Integer.parseInt(matcher.group(1));
    }
    void apiCrud(String path,String idField,String body) throws Exception {
        int id=apiCreate(path,idField,body);
        assertEquals(200,get(path+"/"+id).statusCode(),path);
        var updated=json("PUT",path+"/"+id,body);
        assertEquals(200,updated.statusCode(),path+": "+updated.body());
        var deleted=json("DELETE",path+"/"+id,"");
        assertEquals(204,deleted.statusCode(),path+": "+deleted.body());
        assertEquals(path.endsWith("administrations")?404:200,get(path+"/"+id).statusCode(),path);
    }
    @Test void allEightCrudApisCreateViewEditAndDelete() throws Exception {
        login();
        apiCrud("/api/addresses","addressId","""
                {"zipCode":"12345678","street":"Rua API","number":"2","neighborhood":"Centro","city":"São Paulo","state":"SP"}
                """);
        apiCrud("/api/employee-roles","roleId","""
                {"name":"Cargo API","description":"Teste","permissionLevel":1,"active":true}
                """);
        apiCrud("/api/subscription-plans","planId","""
                {"name":"Plano API","description":"Teste","price":209.89,"storeLimit":2,"userLimit":3,"active":true}
                """);
        apiCrud("/api/companies","companyId","""
                {"officialName":"Empresa API","tradeName":"API","cnpj":"33345678000199","email":"api@teste.example","phone":"11999999999","active":true,"subscriptionPlanId":%d}
                """.formatted(planId));
        int freeAddress=addresses.create(address()).getAddressId();
        apiCrud("/api/stores","storeId","""
                {"name":"Loja API","cnpj":"44345678000199","email":"api@teste.example","phone":"11999999999","active":true,"companyId":%d,"addressId":%d}
                """.formatted(companyId,freeAddress));
        apiCrud("/api/employees","employeeId","""
                {"name":"Pessoa API","cpf":"10000000108","email":"api@teste.example","phone":"11999999999","hireDate":"2023-01-01","active":true,"storeId":%d,"roleId":%d}
                """.formatted(storeId,roleId));
        int emp=employees.create(employee("Pessoa conta API")).getEmployeeId();
        apiCrud("/api/user-accounts","userId","""
                {"email":"api@teste.example","password":"SenhaTeste@123","active":true,"employeeId":%d}
                """.formatted(emp));
        apiCrud("/api/administrations","administrationId","""
                {"email":"api.admin@teste.example","password":"SenhaTeste@123"}
                """);
    }

    @Test void generatedLoadImportsAndAllReadServicesHandleEveryRecord() {
        String sql=com.mottainai.mottainaicrud.util.DataLoadGenerator.generate(crypto);
        jdbc.execute(sql);
        var stats=entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
        stats.clear();
        try(var em=entityManagerFactory.createEntityManager()) {
            assertEquals(500,em.createQuery("select e from Employee e",com.mottainai.mottainaicrud.model.Employee.class).getResultList().size());
        }
        long originalStyleQueries=stats.getPrepareStatementCount();
        stats.clear();assertEquals(6,employees.findPage(1,6,null).getContent().size());
        long paginatedQueries=stats.getPrepareStatementCount();
        assertTrue(paginatedQueries<=2);
        assertTrue(originalStyleQueries>paginatedQueries);
        try {
            Files.writeString(Path.of("build","comparacao-consultas.txt"),
                    "Carga do gerador: 500 empresas, 500 lojas, 500 funcionários e 500 contas.\n"+
                    "SELECT de entidades sem JOIN FETCH (como findAll original): "+originalStyleQueries+" consultas.\n"+
                    "Primeira página otimizada de funcionários: "+paginatedQueries+" consultas.\n",
                    StandardCharsets.UTF_8);
        } catch(java.io.IOException ex) { throw new java.io.UncheckedIOException(ex); }
        assertEquals(500,employees.findAll().size());
        assertEquals(500,accounts.findAll().size());
        assertEquals(500,companies.findAll().size());
        assertEquals(500,stores.findAll().size());
        assertEquals(500,addresses.findAll().size());
        assertEquals(6,roles.findAll().size());
        assertEquals(5,plans.findAll().size());
        assertEquals(1,admins.findAll().size());
        assertTrue(crypto.decrypt(jdbc.queryForObject("select Email from Store where StoreId=101",String.class)).equals("anonimizado"));
        // Sequences manuais devem permitir o próximo cadastro sem colidir com o ID 101.
        assertTrue(addresses.create(address()).getAddressId()>501);
    }

    @Test void allCrudModalViewsCompileAndRender() throws Exception {
        int emp=employees.create(employee("Pessoa modal")).getEmployeeId();
        int account=accounts.create(account(emp)).getUserId();
        login();
        Map<String,Integer> paths=new LinkedHashMap<>();
        paths.put("/empresas",companyId);paths.put("/lojas",storeId);paths.put("/enderecos",addressId);
        paths.put("/funcionarios",emp);paths.put("/cargos",roleId);paths.put("/contas",account);
        paths.put("/planos-administrativos",planId);
        for(var entry:paths.entrySet()) {
            for(String action:List.of("criar","ver","editar","excluir")) {
                String path=entry.getKey()+"?acao="+action+"&id="+entry.getValue();
                assertEquals(200,get(path).statusCode(),path);
            }
        }
        assertEquals(200,get("/administrations/new").statusCode());
        assertEquals(200,get("/administrations/"+adminId).statusCode());
        assertEquals(200,get("/administrations/"+adminId+"/edit").statusCode());
    }

    void formCrud(String path,String table,String pk,String nameColumn,String createdName,String fields) throws Exception {
        assertEquals(302,post(path,"acao=cadastrar&"+fields).statusCode(),path);
        int id=jdbc.queryForObject("select "+pk+" from "+table+" where "+nameColumn+"=?",Integer.class,createdName);
        assertEquals(200,get(path+"?acao=ver&id="+id).statusCode(),path);
        assertEquals(200,get(path+"?acao=editar&id="+id).statusCode(),path);
        assertEquals(302,post(path,"acao=atualizar&id="+id+"&"+fields.replace("CRUD","EDIT")).statusCode(),path);
        assertEquals(createdName.replace("CRUD","EDIT"),jdbc.queryForObject("select "+nameColumn+" from "+table+" where "+pk+"=?",String.class,id));
        assertEquals(302,post(path,"acao=excluir&id="+id+"&"+fields).statusCode(),path);
        assertNotNull(jdbc.queryForObject("select DeletedAt from "+table+" where "+pk+"=?",java.time.LocalDateTime.class,id));
    }
    @Test void jspFormsCreateEditViewAndDeleteWithActualPosts() throws Exception {
        login();
        formCrud("/enderecos","Address","AddressId","Street","Rua CRUD",
                "zipCode=12345678&street=Rua+CRUD&number=2&neighborhood=Centro&city=Sao+Paulo&state=SP");
        formCrud("/cargos","EmployeeRole","RoleId","Name","Cargo CRUD",
                "name=Cargo+CRUD&description=Teste&permissionLevel=1&active=true");
        formCrud("/planos-administrativos","SubscriptionPlan","PlanId","Name","Plano CRUD",
                "name=Plano+CRUD&description=Teste&price=209%2C89&storeLimit=2&userLimit=3&active=true");
        formCrud("/empresas","Company","CompanyId","OfficialName","Empresa CRUD",
                "officialName=Empresa+CRUD&tradeName=Teste&cnpj=55345678000199&email=empresa%40teste.example&phone=11999999999&active=true&subscriptionPlanId="+planId);
        int addr=addresses.create(address()).getAddressId();
        formCrud("/lojas","Store","StoreId","Name","Loja CRUD",
                "name=Loja+CRUD&cnpj=66345678000199&email=loja%40teste.example&phone=11999999999&active=true&companyId="+companyId+"&addressId="+addr);
        String employeeFields="name=Pessoa+CRUD&cpf=10000000108&email=pessoa%40teste.example&phone=11999999999&hireDate=2023-01-01&active=true&storeId="+storeId+"&roleId="+roleId;
        assertEquals(302,post("/funcionarios","acao=cadastrar&"+employeeFields).statusCode());
        int emp=jdbc.queryForObject("select max(EmployeeId) from Employee",Integer.class);
        assertEquals(200,get("/funcionarios?acao=ver&id="+emp).statusCode());
        assertEquals(302,post("/funcionarios","acao=atualizar&id="+emp+"&"+employeeFields.replace("CRUD","EDIT")).statusCode());
        assertEquals("Pessoa EDIT",employees.findById(emp).getName());
        String accountFields="email=conta%40teste.example&password=SenhaTeste%40123&active=true&employeeId="+emp;
        assertEquals(302,post("/contas","acao=cadastrar&"+accountFields).statusCode());
        int account=jdbc.queryForObject("select max(UserId) from UserAccount",Integer.class);
        assertEquals(200,get("/contas?acao=ver&id="+account).statusCode());
        assertEquals(302,post("/contas","acao=atualizar&id="+account+"&"+accountFields.replace("conta%40","editada%40")).statusCode());
        assertEquals("editada@teste.example",accounts.findById(account).getEmail());
        assertEquals(302,post("/contas","acao=excluir&id="+account+"&"+accountFields).statusCode());
        assertNotNull(accounts.findById(account).getDeletedAt());
        assertEquals(302,post("/funcionarios","acao=excluir&id="+emp+"&"+employeeFields).statusCode());
        assertNotNull(employees.findById(emp).getDeletedAt());
        assertEquals(302,post("/administrations","email=form.admin%40teste.example&password=SenhaTeste%40123").statusCode());
        int admin=jdbc.queryForObject("select AdministrationId from Administration where Email='form.admin@teste.example'",Integer.class);
        assertEquals(200,get("/administrations/"+admin).statusCode());
        assertEquals(302,post("/administrations/"+admin+"/update","email=form.editado%40teste.example&password=SenhaTeste%40123").statusCode());
        assertEquals("form.editado@teste.example",admins.findById(admin).getEmail());
        assertEquals(302,post("/administrations/"+admin+"/delete","").statusCode());
        assertFalse(jdbc.queryForObject("select exists(select 1 from Administration where AdministrationId=?)",Boolean.class,admin));
    }

    @Test void invalidCreateAndEditFormsDoNotCrashOrSave() throws Exception {
        int emp=employees.create(employee("Pessoa validação")).getEmployeeId();
        int account=accounts.create(account(emp)).getUserId();
        login();
        Map<String,Integer> paths=Map.of("/enderecos",addressId,"/empresas",companyId,"/lojas",storeId,
                "/funcionarios",emp,"/contas",account,"/cargos",roleId,"/planos-administrativos",planId);
        for(var entry:paths.entrySet()) {
            assertEquals(200,post(entry.getKey(),"acao=cadastrar").statusCode(),"Criar inválido: "+entry.getKey());
            assertEquals(200,post(entry.getKey(),"acao=atualizar&id="+entry.getValue()).statusCode(),"Editar inválido: "+entry.getKey());
        }
        assertEquals("Pessoa validação",employees.findById(emp).getName());
        assertEquals("conta@teste.example",accounts.findById(account).getEmail());
        assertEquals(new BigDecimal("209.89"),plans.findById(planId).getPrice());
    }

    @Test void invalidAccountPasswordsGiveUserFeedbackInsteadOfServerError() throws Exception {
        int emp=employees.create(employee("Pessoa senha")).getEmployeeId();login();
        String fields="acao=cadastrar&email=conta%40teste.example&active=true&employeeId="+emp;
        assertTrue(post("/contas",fields+"&password=curta").statusCode()<400,"Senha curta derrubou a página");
        String longPassword=URLEncoder.encode("á".repeat(40),StandardCharsets.UTF_8);
        var response=post("/contas",fields+"&password="+longPassword);
        assertTrue(response.statusCode()<400,"Senha acima de 72 bytes derrubou a página");
        assertEquals(0,jdbc.queryForObject("select count(*) from UserAccount",Integer.class));
    }

    @Test void planPricesUseBrazilianLocaleEvenWhenBrowserIsEnglish() throws Exception {
        var dto=plan("Plano internacional");dto.setPrice(new BigDecimal("1209.89"));int id=plans.create(dto).getPlanId();login();
        String html=browser.send(HttpRequest.newBuilder(URI.create("http://localhost:"+port+"/planos-administrativos?acao=editar&id="+id))
                .header("Accept-Language","en-US").GET().build(),HttpResponse.BodyHandlers.ofString()).body();
        Files.writeString(Path.of("build/preco-browser-ingles.html"),html,StandardCharsets.UTF_8);
        assertTrue(html.contains("value=\"1.209,89\""),"Campo de edição não respeita formato brasileiro: " + servletContext.getResource("/WEB-INF/jsp/subscription-plans.jsp"));
    }

    @Test void planPriceApiRejectsValuesThatDatabaseWouldRoundOrOverflow() throws Exception {
        login();
        String body="""
                {"name":"Precisão inválida","description":"Teste","price":209.899,"storeLimit":1,"userLimit":1,"active":true}
                """;
        assertEquals(400,json("POST","/api/subscription-plans",body).statusCode(),"API arredondou valor sem validar");
        assertEquals(400,json("POST","/api/subscription-plans",body.replace("209.899","100000000")).statusCode());
        assertEquals(0,jdbc.queryForObject("select count(*) from SubscriptionPlan where Name='Precisão inválida'",Integer.class));
    }

    @Test void missingRecordsAndInvalidApiValuesHaveExpectedStatusCodes() throws Exception {
        login();
        for(String path:List.of("/funcionarios?acao=ver&id=999999","/contas?acao=editar&id=999999","/administrations/999999"))
            assertEquals(404,get(path).statusCode(),path);
        assertEquals(400,json("POST","/api/employees","{}").statusCode());
        assertEquals(400,json("POST","/api/user-accounts","{}").statusCode());
        assertEquals(400,json("PUT","/api/stores/"+storeId,"{}").statusCode());
    }

    // A chave comercial pode mudar na edição; a exclusão pela chave nova preserva a cascata original.
    @Test void storeCrudByCnpjReusesValidationAndDeletionCascade() throws Exception {
        int emp=employees.create(employee("Pessoa loja CNPJ")).getEmployeeId();
        int user=accounts.create(account(emp)).getUserId();login();
        String masked=URLEncoder.encode("22.345.678/0001-99",StandardCharsets.UTF_8);
        assertEquals(200,get("/api/stores/by-cnpj?cnpj="+masked).statusCode());
        String body="""
                {"name":"Loja editada por CNPJ","cnpj":"77345678000199","email":"loja@teste.example","phone":"11999999999","active":true,"companyId":%d,"addressId":%d}
                """.formatted(companyId,addressId);
        assertEquals(200,json("PUT","/api/stores/by-cnpj?cnpj="+masked,body).statusCode());
        assertEquals("Loja editada por CNPJ",stores.findById(storeId).getName());
        assertEquals(404,get("/api/stores/by-cnpj?cnpj=22345678000199").statusCode());
        assertEquals(204,json("DELETE","/api/stores/by-cnpj?cnpj=77345678000199","").statusCode());
        assertNotNull(stores.findById(storeId).getDeletedAt());
        assertNotNull(addresses.findById(addressId).getDeletedAt());
        assertNotNull(employees.findById(emp).getDeletedAt());
        assertNotNull(accounts.findById(user).getDeletedAt());
    }

    // A edição pelo email mantém a sessão sincronizada e a exclusão não permite remover a própria conta.
    @Test void administratorCrudByEmailPreservesSessionAndSelfDeletionProtection() throws Exception {
        login();
        var dto=new AdministrationRequestDTO();dto.setEmail("alvo@teste.example");dto.setPassword("SenhaTeste@123");
        int other=admins.create(dto).getAdministrationId();
        assertEquals(200,get("/api/administrations/by-email?email=alvo%40teste.example").statusCode());
        String body="""
                {"email":"admin.novo@teste.example","password":"SenhaTeste@123"}
                """;
        assertEquals(200,json("PUT","/api/administrations/by-email?email=admin%40teste.example",body).statusCode());
        assertTrue(get("/inicio").body().contains("admin.novo@teste.example"));
        assertEquals(409,json("DELETE","/api/administrations/by-email?email=admin.novo%40teste.example","").statusCode());
        assertEquals("admin.novo@teste.example",admins.findById(adminId).getEmail());
        assertEquals(204,json("DELETE","/api/administrations/by-email?email=alvo%40teste.example","").statusCode());
        assertThrows(RuntimeException.class,()->admins.findById(other));
        assertEquals(404,get("/api/administrations/by-email?email=alvo%40teste.example").statusCode());
        assertEquals(302,post("/logout","").statusCode());
        assertEquals(302,post("/login","email=admin.novo%40teste.example&password=SenhaTeste%40123").statusCode());
        assertEquals(200,get("/inicio").statusCode());
    }

    // Campos alternativos não devem contornar login, unicidade nem a reserva da loja de anonimização.
    @Test void alternativeKeysKeepAuthenticationAndRejectInvalidDuplicateOrTechnicalTargets() throws Exception {
        assertEquals(302,get("/api/stores/by-cnpj?cnpj=22345678000199").statusCode());
        assertEquals(302,json("DELETE","/api/administrations/by-email?email=admin%40teste.example","").statusCode());
        assertEquals(302,get("/lojas/por-cnpj?busca=22345678000199&acao=excluir").statusCode());
        login();
        assertEquals(400,get("/api/stores/by-cnpj?cnpj=abc").statusCode());
        assertEquals(404,get("/api/stores/by-cnpj?cnpj=99999999999999").statusCode());
        assertEquals(400,get("/api/administrations/by-email?email=").statusCode());
        assertEquals(404,get("/api/administrations/by-email?email=inexistente%40teste.example").statusCode());
        assertEquals(400,json("PUT","/api/stores/by-cnpj?cnpj=22345678000199","{}").statusCode());
        assertEquals(409,json("DELETE","/api/stores/by-cnpj?cnpj=00000000000001","").statusCode());
        int freeAddress=addresses.create(address()).getAddressId();var duplicate=store();
        duplicate.setCnpj("88345678000199");duplicate.setAddressId(freeAddress);stores.create(duplicate);
        String body="""
                {"name":"Duplicada","cnpj":"88345678000199","email":"loja@teste.example","phone":"11999999999","active":true,"companyId":%d,"addressId":%d}
                """.formatted(companyId,addressId);
        assertEquals(409,json("PUT","/api/stores/by-cnpj?cnpj=22345678000199",body).statusCode());
        assertEquals("22345678000199",stores.findById(storeId).getCnpj());
        var dto=new AdministrationRequestDTO();dto.setEmail("ocupado@teste.example");dto.setPassword("SenhaTeste@123");admins.create(dto);
        assertEquals(409,json("PUT","/api/administrations/by-email?email=admin%40teste.example",
                "{\"email\":\"ocupado@teste.example\",\"password\":\"SenhaTeste@123\"}").statusCode());
        assertEquals("admin@teste.example",admins.findById(adminId).getEmail());
    }

    // Segue apenas o redirecionamento GET para conferir o modal; nenhuma exclusão pode ocorrer nesse fluxo.
    HttpResponse<String> followLocation(HttpResponse<String> response) throws Exception {
        URI target=URI.create(response.headers().firstValue("Location").orElseThrow());
        return get(target.getRawPath()+(target.getRawQuery()==null?"":"?"+target.getRawQuery()));
    }

    @Test void interfaceLocatesByBusinessKeyAndRequiresExistingConfirmation() throws Exception {
        login();assertTrue(get("/lojas").body().contains("Excluir por CNPJ"));
        assertTrue(get("/administrations").body().contains("Editar por email"));
        var confirmation=get("/lojas/por-cnpj?busca=22.345.678%2F0001-99&acao=excluir");
        assertEquals(302,confirmation.statusCode());assertEquals(200,followLocation(confirmation).statusCode());
        assertNull(stores.findById(storeId).getDeletedAt());
        assertEquals(200,followLocation(get("/lojas/por-cnpj?busca=22345678000199&acao=editar")).statusCode());
        var dto=new AdministrationRequestDTO();dto.setEmail("confirmar@teste.example");dto.setPassword("SenhaTeste@123");
        int other=admins.create(dto).getAdministrationId();
        var adminConfirmation=get("/administrations/por-email?busca=confirmar%40teste.example&acao=excluir");
        assertEquals(302,adminConfirmation.statusCode());assertEquals(200,followLocation(adminConfirmation).statusCode());
        assertEquals("confirmar@teste.example",admins.findById(other).getEmail());
        assertEquals(200,followLocation(get("/administrations/por-email?busca=confirmar%40teste.example&acao=editar")).statusCode());
        assertEquals(302,get("/lojas/por-cnpj?busca=abc&acao=excluir").statusCode());
        assertEquals(302,get("/lojas/por-cnpj?busca=22345678000199&acao=outra").statusCode());
        assertEquals(302,get("/administrations/por-email?busca=&acao=excluir").statusCode());
        assertNull(stores.findById(storeId).getDeletedAt());
    }
}
