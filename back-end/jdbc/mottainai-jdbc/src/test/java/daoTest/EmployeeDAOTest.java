package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.EmployeeDAO;
import org.mot.mottainaijdbc.dto.EmployeeCountByCityDTO;
import org.mot.mottainaijdbc.dto.EmployeeCountByStateDTO;
import org.mot.mottainaijdbc.model.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeDAOTest {

    //TESTE DO METODO DE INSERCAO DOS REGISTROS NO BANCO
    @Test
    public void checkInsert() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        LocalDate localDate = LocalDate.of(2026,2,16);
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        Employee employeeInsert = new Employee(101, "EMP_901", "CPF_TKN_9DBA1F", "EMAIL_EMP_701", "PHONE_EMP_701",true, localDate, localDateTime, null, null, 24, 5 );

        assertEquals(1, employeeDAO.insert(employeeInsert));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    @Test
    public void checkUpdate() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        LocalDate localDate = LocalDate.of(2026,2,16);
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        Employee employeeUpdate = new Employee(101, "EMP_901", "CPF_TKN_9DBA1F", "EMAIL_EMP_701", "PHONE_EMP_701", true, localDate, localDateTime, null, null, 24, 5 );

        assertEquals(1, employeeDAO.update(employeeUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO POR CPF
    @Test
    public void checkUpdateViaCpf() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        LocalDate localDate = LocalDate.of(2026,2,16);
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        Employee employeeUpdate = new Employee(101, "EMP_901", "CPF_TKN_9DBA1F", "EMAIL_EMP_701", "PHONE_EMP_701", true, localDate, localDateTime, null, null, 24, 5 );

        assertEquals(1, employeeDAO.updateViaCpf(employeeUpdate));
    }

    //TESTE DO METODO DE ATUALIZACAO DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkUpdateViaEmail() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        LocalDate localDate = LocalDate.of(2026,2,16);
        LocalDateTime localDateTime = LocalDateTime.of(2026, 2, 1, 14, 30, 10);

        Employee employeeUpdate = new Employee(101, "EMP_901", "CPF_TKN_9DBA1F", "EMAIL_EMP_701", "PHONE_EMP_701", true, localDate, localDateTime, null, null, 24, 5 );

        assertEquals(1, employeeDAO.updateViaEmail(employeeUpdate));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO
    @Test
    public void checkDelete() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertEquals(1, employeeDAO.delete(101));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR EMAIL
    @Test
    public void checkDeleteViaEmail() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertEquals(1, employeeDAO.deleteViaEmail("EMAIL_EMP_701"));
    }

    //TESTE DO METODO DE DELECAO DOS REGISTROS NO BANCO POR CPF
    @Test
    public void checkDeleteViaCpf() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        assertEquals(1, employeeDAO.deleteViaCpf("P7dm18cGwjc2V+hIL8m4NRtB0JTWWiZwmT0Pjkyk0DrWjxJA7noG"));
    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO
    @Test
    public void checkList() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.list();

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO VIA ID
    @Test
    public void checkListViaId() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByEmployeeId(101);

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO QUE ESTAO ATIVOS
    @Test
    public void checkListViaStatus() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByStatus();

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO NOME
    @Test
    public void checkListViaName() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByName("EMP_901");

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO NOME
    @Test
    public void checkListViaEmail() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByEmail("EMAIL_EMP_701");

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO CPF
    @Test
    public void checkListViaCpf() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByCpf("CPF_TKN_9DBA1F");

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO CPF
    @Test
    public void checkListViaPhone() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByPhone("PHONE_EMP_701");

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO ID DE STORE
    @Test
    public void checkListViaStoreId() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByStoreId(24);

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DE LISTAGEM DOS REGISTROS NO BANCO PELO ROLEID
    @Test
    public void checkListViaRoleId() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<Employee> employees = employeeDAO.listByRoleId(5);

        assertFalse(employees.isEmpty());

    }

    //TESTE DO METODO DO TOTAL DOS REGISTROS ATIVOS
    @Test
    public void checkLisCountStatus() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        Long employeeCount = employeeDAO.listCountStatus();

        assertNotNull(employeeCount);

    }

    //TESTE DO METODO CORRELACIONADO COM STORE AND ADDRESS - FUNCIONARIOS POR CIDADE
    @Test
    public void checkLisCountEmployeeByCity() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<EmployeeCountByCityDTO> employeeCount = employeeDAO.listCountEmployeeByCity();

        assertFalse(employeeCount.isEmpty());

    }

    //TESTE DO METODO CORRELACIONADO COM STORE AND ADDRESS - FUNCIONARIOS POR ESTADO
    @Test
    public void checkLisCountEmployeeByState() {
        EmployeeDAO employeeDAO = new EmployeeDAO();

        List<EmployeeCountByStateDTO> employeeCount = employeeDAO.listCountEmployeeByState();

        assertFalse(employeeCount.isEmpty());

    }


}
