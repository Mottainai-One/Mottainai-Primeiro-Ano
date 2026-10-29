package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.UserAccountDAO;
import org.mot.mottainaijdbc.model.UserAccount;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserAccountDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        LocalDateTime localDateTimeLastLogin = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        LocalDateTime localDateTimeCreatedAt = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        UserAccount userAccountInsert = new UserAccount(101, "USER_901", "$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6", localDateTimeLastLogin, true,localDateTimeCreatedAt, null, null, 101 );

        assertEquals(1, userAccountDAO.insert(userAccountInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        LocalDateTime localDateTimeLastLogin = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        LocalDateTime localDateTimeCreatedAt = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        UserAccount userAccountUpdate = new UserAccount(101, "USER_901", "$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6", localDateTimeLastLogin, false,localDateTimeCreatedAt, null, null, 101);

        assertEquals(1, userAccountDAO.update(userAccountUpdate));
    }
}
