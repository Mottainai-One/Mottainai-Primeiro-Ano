package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.AdministrationDAO;
import org.mot.mottainaijdbc.model.Administration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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

    //TESTE DO METODO DE DELEÇÃO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        AdministrationDAO admDao = new AdministrationDAO();

        int alteration = admDao.delete(101);

        assertEquals(1, alteration);
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        AdministrationDAO admDao = new AdministrationDAO();

        List<Administration> administrations = admDao.list();

        assertFalse(administrations.isEmpty());
    }
}
