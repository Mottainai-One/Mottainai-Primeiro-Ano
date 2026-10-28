package daoTest;

import org.junit.jupiter.api.Test;
import org.mot.mottainaijdbc.dao.EmployeeDAO;
import org.mot.mottainaijdbc.model.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}
