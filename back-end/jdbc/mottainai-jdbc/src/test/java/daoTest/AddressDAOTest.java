package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.AddressDAO;
import org.mot.mottainaijdbc.model.Address;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddressDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        AddressDAO addressDAO = new AddressDAO();

        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 1, 15, 9, 30);

        Address addressInsert = new Address(102, "12345678", "Av. Manuel Bandeirantes", "9871", "prédio", "Vila Madalena", "São Paulo", "SP", dateTime, null, null);

        assertEquals(1, addressDAO.insert(addressInsert));
    }


    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        AddressDAO addressDAO = new AddressDAO();

        LocalDateTime dateTimeUpdate = LocalDateTime.of(2026, 01, 10, 14, 30, 30);

        Address addressUpdate = new Address(102, "12345678", "Av. Casperlíbero", "3333", "prédio", "Vila Maria", "São Paulo", "SP",dateTimeUpdate , null, null);

        assertEquals(1, addressDAO.update(addressUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        AddressDAO addressDAO = new AddressDAO();

        assertEquals(1, addressDAO.delete(101));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO VIA ZIPCODE E NUMBER
    @Test
    public void checkDeleteViaZipCodeAndNumber() {
        AddressDAO addressDAO = new AddressDAO();

        assertEquals(1, addressDAO.deleteViaZipCodeAndNumber("12345678", "9871"));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.list();

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELA CIDADE
    @Test
    public void checkListByCity() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByCity("São Paulo");

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELA NUMERO
    @Test
    public void checkListByNumber() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByNumber("9871");

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO CEP
    @Test
    public void checkListByZipCode() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByZipCode("12345678");

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO COMPLEMENTO
    @Test
    public void checkListByComplement() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByComplement("prédio");

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO ID
    @Test
    public void checkListByAddressId() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByAddressId(102);

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELA CIDADE
    @Test
    public void checkListByStreet() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByStreet("Av. Manuel Bandeirantes");

        assertFalse(addresses.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO BAIRRO
    @Test
    public void checkListByNeighborhood() {
        AddressDAO addressDAO = new AddressDAO();

        List<Address> addresses = addressDAO.listByNeighborhood("Vila Madalena");

        assertFalse(addresses.isEmpty());
    }
}

