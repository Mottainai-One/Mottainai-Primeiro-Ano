package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.AddressDAO;
import org.mot.mottainaijdbc.model.Address;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddressDAOTest {

    @Test
    public void checkInsert() {
        AddressDAO addressDAO = new AddressDAO();

        LocalDateTime dateTime = LocalDateTime.of(2026, 1, 1, 15, 9, 30);

        Address addressInsert = new Address(101, "12345678", "Av. Manuel Bandeirantes", "9871", "prédio", "Vila Madalena", "São Paulo", "SP", dateTime, null, null);

        assertEquals(1, addressDAO.insert(addressInsert));
    }

    @Test
    public void checkUpdate() {
        AddressDAO addressDAO = new AddressDAO();

        LocalDateTime dateTimeUpdate = LocalDateTime.of(2026, 01, 10, 14, 30, 30);

        Address addressUpdate = new Address(101, "12345678", "Av. Casperlíbero", "3333", "prédio", "Vila Maria", "São Paulo", "SP",dateTimeUpdate , null, null);

        assertEquals(1, addressDAO.update(addressUpdate));
    }
}

