package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.AdministrationDAO;
import org.mot.mottainaijdbc.model.Administration;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AdministrationDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        AdministrationDAO admDao = new AdministrationDAO();

        Administration administrationInsert = new Administration( "mottainai_adm@gmail.com", "pHAPS4453!#WERTY0987");

        int alteration = admDao.insert(administrationInsert);

        assertEquals(1, alteration);
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        AdministrationDAO admDao = new AdministrationDAO();

        Administration administrationUpdate = new Administration(101, "adm_mottainai@gmail.com", "mot2026_inter#10");

        int alteration = admDao.update(administrationUpdate);

        assertEquals(1, alteration);
    }
}
