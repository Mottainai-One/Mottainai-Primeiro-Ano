package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.CompanyDAO;
import org.mot.mottainaijdbc.dto.CompanyStoreCountDTO;
import org.mot.mottainaijdbc.dto.CompanyUserAccountCountDTO;
import org.mot.mottainaijdbc.model.Company;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CompanyDAOTest extends DatabaseTestCase {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        CompanyDAO companyDAO = new CompanyDAO();

        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyInsert = new Company(900, "Empresa de teste inserida", "Teste inserção", "90002001999999", "insert-company@example.com", "1100009999", -3.98767, -5.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.insert(companyInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        CompanyDAO companyDAO = new CompanyDAO();
        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyUpdate = new Company(101, "Grupo Casas Bahia", "Casa Bahia Comercial Ltda", "90002001001127", "m***@empresa.com", "(11) 9****-1709", -5.98767, -2.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.updateViaCnpj(companyUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO VIA EMAIL
    @Test
    public void checkUpdateViaEmail() {
        CompanyDAO companyDAO = new CompanyDAO();
        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyUpdate = new Company(101, "Grupo Casas Bahia", "Casa Bahia Comercial Ltda", "90002001001127", "m***@empresa.com", "(11) 9****-1709", -5.98767, -2.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.updateViaEmail(companyUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO VIA CNPJ
    @Test
    public void checkUpdateViaCnpj() {
        CompanyDAO companyDAO = new CompanyDAO();
        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyUpdate = new Company(101, "Grupo Casas Bahia", "Casa Bahia Comercial Ltda", "90002001001127", "m***@empresa.com", "(11) 9****-1709", -5.98767, -2.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.updateViaCnpj(companyUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        CompanyDAO companyDAO = new CompanyDAO();

        assertEquals(1, companyDAO.delete(101));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO VIA CNPJ
    @Test
    public void checkDeleteViaCnpj() {
        CompanyDAO companyDAO = new CompanyDAO();

        assertEquals(1, companyDAO.deleteViaCnpj("90002001001127"));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO VIA LATITUDE E LONGITUDE
    @Test
    public void checkDeleteViaLatitudeAndLongitude() {
        CompanyDAO companyDAO = new CompanyDAO();

        assertEquals(1, companyDAO.deleteViaLatitudeAndLongitude(-5.345600,-3.987670));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.list();

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID
    @Test
    public void checkListById() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByCompanyId(102);

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR OFFICIALNAME
    @Test
    public void checkListByOfficialName() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByOfficialName("Grupo Casas Bahia");

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR TRADENAME
    @Test
    public void checkListByTradeName() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByTradeName("Casa Bahia Comercial Ltda");

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR CNPJ
    @Test
    public void checkListByCnpj() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByCnpj("90002001001127");

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkListByEmail() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByEmail("m***@empresa.com");

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR PHONE
    @Test
    public void checkListByPhone() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.listByPhone("(11) 9****-1709");

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE TOTAL DE EMPRESAS ATIVAS
    @Test
    public void checkListCountStatus() {
        CompanyDAO companyDAO = new CompanyDAO();

        Long companyCount = companyDAO.listCountStatus();

        assertNotNull(companyCount);
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS CORRELACIONADO COM STORE
    @Test
    public void checkListCountStoreByCompany() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<CompanyStoreCountDTO> companies = companyDAO.listCountStoreByCompany();

        assertFalse(companies.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS CORRELACIONADO COM OS USUARIOS
    @Test
    public void checkListCountUsersByCompany() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<CompanyUserAccountCountDTO> companies = companyDAO.listCountUsersByCompany();

        assertFalse(companies.isEmpty());
    }

}
