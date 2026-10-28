package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.StoreDAO;
import org.mot.mottainaijdbc.model.Store;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class StoreDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        StoreDAO storeDAO = new StoreDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        Store storeInsert = new Store(101, "Loja Pastelaria Brasileira - São Paulo", "90087649000143", "m***@empresa.com", "(11) 9****-1709", -5.567, 9.456, true, localDateTime, null, null, 87, 101);

        assertEquals(1, storeDAO.insert(storeInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        StoreDAO storeDAO = new StoreDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        Store storeUpdate = new Store(101, "Loja Pizzaria Della Roma - São Paulo", "90000987654321", "m***@empresa.com",  "(11) 9****-1709", -3.98767, -7.3456, true, localDateTime, null, null, 87, 101);

        assertEquals(1, storeDAO.update(storeUpdate));
    }
}
