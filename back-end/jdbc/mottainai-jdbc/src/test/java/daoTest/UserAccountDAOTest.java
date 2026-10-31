package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.UserAccountDAO;
import org.mot.mottainaijdbc.model.UserAccount;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserAccountDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        LocalDateTime localDateTimeLastLogin = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        LocalDateTime localDateTimeCreatedAt = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        UserAccount userAccountInsert = new UserAccount(501, "USER_901", "$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6", localDateTimeLastLogin, true,localDateTimeCreatedAt, null, null, 501 );

        assertEquals(1, userAccountDAO.insert(userAccountInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        LocalDateTime localDateTimeLastLogin = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        LocalDateTime localDateTimeCreatedAt = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        UserAccount userAccountUpdate = new UserAccount(501, "USER_901", "$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6", localDateTimeLastLogin, false,localDateTimeCreatedAt, null, null, 101);

        assertEquals(1, userAccountDAO.update(userAccountUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkUpdateViaEmail() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        LocalDateTime localDateTimeLastLogin = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        LocalDateTime localDateTimeCreatedAt = LocalDateTime.of(2026, 02, 01, 15, 30, 10);

        UserAccount userAccountUpdate = new UserAccount(501, "USER_901", "$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6", localDateTimeLastLogin, false,localDateTimeCreatedAt, null, null, 101);

        assertEquals(1, userAccountDAO.updateViaEmail(userAccountUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        assertEquals(1, userAccountDAO.delete(501));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkDeleteViaEmail() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        assertEquals(1, userAccountDAO.deleteViaEmail("USER_901"));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR SENHA
    @Test
    public void checkDeleteViaPasswordHash() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        assertEquals(1, userAccountDAO.deleteViaPasswordHash("$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6"));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        List<UserAccount> userAccounts = userAccountDAO.list();

        assertFalse(userAccounts.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO ID
    @Test
    public void checkListByUserAccountId() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        List<UserAccount> userAccounts = userAccountDAO.listByUserAccountId(501);

        assertFalse(userAccounts.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO STATUS
    @Test
    public void checkListByStatus() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        List<UserAccount> userAccounts = userAccountDAO.listByStatus();

        assertFalse(userAccounts.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELA SENHA
    @Test
    public void checkListByPasswordHash() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        List<UserAccount> userAccounts = userAccountDAO.listByPasswordHash("$9b$12$IXmU5oLQCmpUXCfYVoJ39GDKlllRwIyGjc0xgNWjLvGbc91EJqWA6");

        assertFalse(userAccounts.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO EMAIL
    @Test
    public void checkListByEmail() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        List<UserAccount> userAccounts = userAccountDAO.listByEmail("USER_901");

        assertFalse(userAccounts.isEmpty());
    }

    //TESTE DO METODO DO TOTAL DE USUARIOS ATIVOS
    @Test
    public void checkListCountStatus() {

        UserAccountDAO userAccountDAO = new UserAccountDAO();

        Long userAccountCount = userAccountDAO.listCountStatus();

        assertNotNull(userAccountCount);
    }
}
