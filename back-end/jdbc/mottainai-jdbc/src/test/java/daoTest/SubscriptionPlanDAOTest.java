package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.SubscriptionPlanDAO;
import org.mot.mottainaijdbc.model.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SubscriptionPlanDAOTest extends DatabaseTestCase {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceInsert = BigDecimal.valueOf(1299.99);

        SubscriptionPlan subscriptionPlanInsert = new SubscriptionPlan(900, "Plano de teste inserido", "Plano exclusivo para teste de inserção", priceInsert, 50, 150, true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.insert(subscriptionPlanInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceUpdate = BigDecimal.valueOf(780.99);

        SubscriptionPlan subscriptionPlanUpdate = new SubscriptionPlan( 101,"Plano Black ","Plano de assinatura de 1 mês por um terço do preço", priceUpdate, 100, 150,true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.update(subscriptionPlanUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO PELO NOME
    @Test
    public void checkUpdateViaName() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceUpdate = BigDecimal.valueOf(780.99);

        SubscriptionPlan subscriptionPlanUpdate = new SubscriptionPlan( 102,"Plano Black Friday","Plano de assinatura de 1 mês por um terço do preço", priceUpdate, 100, 150,true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.updateViaName(subscriptionPlanUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO PELOS LIMITES DE USUARIO E LOJA
    @Test
    public void checkUpdateViaAmount() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceUpdate = BigDecimal.valueOf(780.99);

        SubscriptionPlan subscriptionPlanUpdate = new SubscriptionPlan( 101,"Plano Black ","Plano de assinatura de 1 mês por um terço do preço", priceUpdate, 8, 24,true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.updateViaAmount(subscriptionPlanUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        assertEquals(1, subscriptionPLanDAO.delete(101));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO PELO NOME
    @Test
    public void checkDeleteViaName() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        assertEquals(1, subscriptionPLanDAO.deleteViaName("Plano Black Friday"));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO PELO LIMITE DE USUARIO E LOJA
    @Test
    public void checkDeleteViaAmount() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        assertEquals(1, subscriptionPLanDAO.deleteViaAmount(24, 8));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.list();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ID
    @Test
    public void checkListBySubscriptionPlanId() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listBySubscriptionPlanId(1);

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR NOME
    @Test
    public void checkListByName() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listByName("ree");

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR DESCRICAO
    @Test
    public void checkListByDescription() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listByDescription("co");

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    @Test
    public void checkListByStatus() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listByStatus();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DO PLANO MAIS BARATO
    @Test
    public void checkListMinPlan() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMinPlan();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //METODO DE LISTAGEM DO PLANO QUE TEM O MENOR LIMITE DE LOJA
    @Test
    public void checkListMinStoreLimit() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMinStoreLimit();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //METODO DE LISTAGEM DO PLANO QUE TEM O MAIOR LIMITE DE LOJA
    @Test
    public void checkListMaxStoreLimit() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMaxStoreLimit();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //METODO DE LISTAGEM DO PLANO QUE TEM O MENOR LIMITE DE USUARIOS
    @Test
    public void checkListMinUserLimit() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMinUserLimit();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //METODO DE LISTAGEM DO PLANO QUE TEM O MAIOR LIMITE DE USUARIOS
    @Test
    public void checkListMaxUserLimit() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMaxUserLimit();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //METODO DE LISTAGEM DO PLANO QUE TEM O MAIOR PRECO
    @Test
    public void checkListMaxPlan() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        List<SubscriptionPlan> subscriptionPlans = subscriptionPLanDAO.listMaxPlan();

        assertFalse(subscriptionPlans.isEmpty());
    }

    //TESTE DO METODO DO TOTAL DE REGISTROS ATIVOS
    @Test
    public void checkListCountStatus() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        Long subscriptionPlanCount = subscriptionPLanDAO.listCountStatus();

        assertNotNull(subscriptionPlanCount);
    }
}
