package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.EmployeeRoleDAO;
import org.mot.mottainaijdbc.dto.EmployeeRoleEmployeeCountDTO;
import org.mot.mottainaijdbc.model.EmployeeRole;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeRoleDAOTest extends DatabaseTestCase {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        //TESE DO METODO DE INSERÇÃO DOS REGISTROS NO BANCO
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        EmployeeRole employeeRoleInsert = new EmployeeRole(900, "Auxiliar de Limpeza inserido", "Função de teste de inserção", 1, true, localDateTime, null, null);

        assertEquals(1, employeeRoleDAO.insert(employeeRoleInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        EmployeeRole employeeRoleUpdate = new EmployeeRole(9, "Auxiliar de Limpeza", "Função de porteiro", 1, true, localDateTime, null, null);
        assertEquals(1, employeeRoleDAO.update(employeeRoleUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO PELO NOME
    @Test
    public void checkUpdateByName() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        EmployeeRole employeeRoleUpdate = new EmployeeRole(9, "Auxiliar de Limpeza", "Função de porteiro", 1, true, localDateTime, null, null);

        assertEquals(1, employeeRoleDAO.updateViaName(employeeRoleUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        assertEquals(1, employeeRoleDAO.delete(9));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO PELO NAME
    @Test
    public void checkDeleteViaName() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        assertEquals(1, employeeRoleDAO.deleteViaName("Auxiliar de Limpeza"));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.list();
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR ROLEID
    @Test
    public void checkListByEmployeeRoleId() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.listByEmployeeRoleId(1);
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR NAME
    @Test
    public void checkListByName() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.listByName("erente");
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR DESCRIPTION
    @Test
    public void checkListByDescription() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.listByDescription("racional");
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO POR NIVEL DE PERMISSAO
    @Test
    public void checkListByPermissionLevel() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.listByPermissionLevel(1);
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    @Test
    public void checkListByStatus() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRole> employeeRoles = employeeRoleDAO.listByStatus();
        assertFalse(employeeRoles.isEmpty());
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    @Test
    public void checkListCountStatus() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        Long employee = employeeRoleDAO.listCountStatus();

        assertNotNull(employee);
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO CORRELACIONADO COM EMPLOYEE
    @Test
    public void checkListEmployeeByRole() {
        EmployeeRoleDAO employeeRoleDAO = new EmployeeRoleDAO();

        List<EmployeeRoleEmployeeCountDTO> employeeRoles = employeeRoleDAO.listEmployeeByRole();
        assertFalse(employeeRoles.isEmpty());
    }
}
