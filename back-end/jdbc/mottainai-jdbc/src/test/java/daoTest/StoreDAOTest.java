package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.StoreDAO;
import org.mot.mottainaijdbc.dto.StoreCountByCityDTO;
import org.mot.mottainaijdbc.dto.StoreCountByStateDTO;
import org.mot.mottainaijdbc.dto.StoreEmployeeCountDTO;
import org.mot.mottainaijdbc.dto.StoreUserAccountCountDTO;
import org.mot.mottainaijdbc.model.Store;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoreDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        StoreDAO storeDAO = new StoreDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        Store storeInsert = new Store(102, "Loja Pastelaria Brasileira - São Paulo", "90087649000143", "m***@empresa.com", "(11) 9****-1709", -5.567, 9.456, true, localDateTime, null, null, 87, 102);

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

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO PELO CNPJ
    @Test
    public void checkUpdateViaCnpj() {
        StoreDAO storeDAO = new StoreDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        Store storeUpdate = new Store(102, "Loja Pizzaria Della Roma - São Paulo", "90087649000143", "m***@empresa.com",  "(11) 9****-1709", -3.98767, -7.3456, true, localDateTime, null, null, 87, 102);

        assertEquals(1, storeDAO.updateViaCnpj(storeUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO PELO EMAIL
    @Test
    public void checkUpdateViaEmail() {
        StoreDAO storeDAO = new StoreDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        Store storeUpdate = new Store(102, "Loja Pizzaria Della Roma - São Paulo", "90087649000143", "m***@empresa.com",  "(11) 9****-1709", -3.98767, -7.3456, true, localDateTime, null, null, 87, 102);

        assertEquals(1, storeDAO.updateViaEmail(storeUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        StoreDAO storeDAO = new StoreDAO();

        assertEquals(1, storeDAO.delete(101));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR CNPJ
    @Test
    public void checkDeleteViaCnpj() {
        StoreDAO storeDAO = new StoreDAO();

        assertEquals(1, storeDAO.deleteViaCnpj("90087649000143"));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkDeleteViaEmail() {
        StoreDAO storeDAO = new StoreDAO();

        assertEquals(1, storeDAO.deleteViaEmail("m***@empresa.com"));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.list();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO CORRELACIONADO COM EMPLOYEE
    @Test
    public void checkListCountEmployeeByStore() {
        StoreDAO storeDAO = new StoreDAO();

        List<StoreEmployeeCountDTO> stores = storeDAO.listCountEmployeeByStore();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO CORRELACIONADO COM ADDRESS
    @Test
    public void checkListCountStoreByCity() {
        StoreDAO storeDAO = new StoreDAO();

        List<StoreCountByCityDTO> stores = storeDAO.listCountStoreByCity();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO CORRELACIONADO COM ADDRESS
    @Test
    public void checkListCountStoreByState() {
        StoreDAO storeDAO = new StoreDAO();

        List<StoreCountByStateDTO> stores = storeDAO.listCountStoreByState();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO CORRELACIONADO COM USERACCOUNT
    @Test
    public void checkListCountUsersByStore() {
        StoreDAO storeDAO = new StoreDAO();

        List<StoreUserAccountCountDTO> stores = storeDAO.listCountUsersByStore();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    @Test
    public void checkListByStatus() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByStatus();

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR NOME
    @Test
    public void checkListByName() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByName("Loja 001");

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO CNPJ
    @Test
    public void checkListByCnpj() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByCnpj("91000001000191");

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkListByEmail() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByEmail("KJsCtcqNYn2/tfhr91nMwKEcPbyzrLTo6SA/8TlUMVU6R+3Y8LyAtWWe8uqG0sXLhpKOh1E=");

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR TELEFONE
    @Test
    public void checkListByPhone() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByPhone("tm0cg4OFiuzAs27uD82/GLZ6urACgTCLRCI0ATMYZiyIj0uUg/b7");

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID DA EMPRESA
    @Test
    public void checkListByCompanyId() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByCompanyId(1);

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID DA LOJA
    @Test
    public void checkListByStoreId() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByStoreId(1);

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID DO ENDERECO
    @Test
    public void checkListByAddressId() {
        StoreDAO storeDAO = new StoreDAO();

        List<Store> stores = storeDAO.listByAddressId(1);

        assertFalse(stores.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID DO ENDERECO
    @Test
    public void checkListCountStatus() {
        StoreDAO storeDAO = new StoreDAO();

        Long storeCount = storeDAO.listCountStatus();

        assertNotNull(storeCount);
    }



}
