package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.CompanyDAO;
import org.mot.mottainaijdbc.model.Company;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CompanyDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        CompanyDAO companyDAO = new CompanyDAO();

        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyInsert = new Company(101, "Grupo Casas Bahia", "Casa Bahia Comercial Ltda", "90002001001127", "m***@empresa.com", "(11) 9****-1709", -3.98767, -5.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.insert(companyInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        CompanyDAO companyDAO = new CompanyDAO();
        LocalDateTime dateTime = LocalDateTime.of(2026, 02, 01, 14, 30, 03);

        Company companyUpdate = new Company(101, "Grupo Casas Bahia", "Casa Bahia Comercial Ltda", "98302041203127", "m***@empresa.com", "(11) 9****-1709", -5.98767, -2.3456, true, dateTime, null, null, 101);

        assertEquals(1, companyDAO.update(companyUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        CompanyDAO companyDAO = new CompanyDAO();

        assertEquals(1, companyDAO.delete(101));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        CompanyDAO companyDAO = new CompanyDAO();

        List<Company> companies = companyDAO.list();

        assertFalse(companies.isEmpty());
    }

}
