package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.EmployeeRoleDAO;
import org.mot.mottainaijdbc.model.EmployeeRole;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeRoleDAOTest {

    @Test
    public void checkInsert() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        //TESE DO METODO DE INSERÇÃO DOS REGISTROS NO BANCO
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        EmployeeRole employeeRoleInsert = new EmployeeRole(9, "Auxiliar de Limpeza", "Função de auxiliar na limpeza do estabelecimento", 1, true, localDateTime, null, null);

        assertEquals(1, employeeRoleDAO.insert(employeeRoleInsert));
    }
    @Test
    public void checkUpdate() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        //TESE DO METODO DE INSERÇÃO DOS REGISTROS NO BANCO
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);


        EmployeeRole employeeRoleUpdate = new EmployeeRole(9, "Porteiro", "Função de porteiro", 1, true, localDateTime, null, null);
        assertEquals(1, employeeRoleDAO.update(employeeRoleUpdate));
    }
}
