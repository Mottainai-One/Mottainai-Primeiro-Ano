package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.SubscriptionPlanDAO;
import org.mot.mottainaijdbc.model.SubscriptionPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubscriptionPlanDAOTest {
    @Test
    public void checkInsert() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceInsert = BigDecimal.valueOf(1299.99);

        SubscriptionPlan subscriptionPlanInsert = new SubscriptionPlan(101, "Plano Black Friday", "Plano de assinatura de 1 mês pela metade do preço", priceInsert, 50, 150, true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.insert(subscriptionPlanInsert));
    }

    @Test
    public void checkUpdate() {

        SubscriptionPlanDAO subscriptionPLanDAO = new SubscriptionPlanDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 02, 01, 15, 30, 10);
        BigDecimal priceUpdate = BigDecimal.valueOf(780.99);

        SubscriptionPlan subscriptionPlanUpdate = new SubscriptionPlan( 101,"Plano Black ","Plano de assinatura de 1 mês por um terço do preço", priceUpdate, 100, 300,true, localDateTime, null, null);

        assertEquals(1, subscriptionPLanDAO.update(subscriptionPlanUpdate));
    }

}
