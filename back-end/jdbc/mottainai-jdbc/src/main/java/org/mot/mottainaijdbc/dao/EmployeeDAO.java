package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.dto.EmployeeCountByCityDTO;
import org.mot.mottainaijdbc.dto.EmployeeCountByStateDTO;
import org.mot.mottainaijdbc.model.Employee;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    //CONSTRUTOR
    public EmployeeDAO() {
    }

    //METODO  DE LISTAGEM DOS REGISTROS
    public List<Employee> list() {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByStatus() {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE active = true ";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (
                //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByName(String name) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE name LIKE ? ";

        String nameIncident = "%"+ name+ "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, nameIncident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByEmail(String email) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE email LIKE ? ";

        String emailIncident = "%"+ email + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, emailIncident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByCpf(String cpf) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE cpf LIKE ? ";

        String cpfIncident = "%"+ cpf + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, cpfIncident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByPhone(String phone) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE phone LIKE ? ";

        String phoneIncident = "%"+ phone + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, phoneIncident);

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByStoreId(int storeId) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE storeid = ? ";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS
            pstm.setInt(1, storeId);

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS PELOS USUÁRIOS QUE ESTÃO ATIVOS
    public List<Employee> listByRoleId(int roleId) {
        List<Employee> employees;

        //COMANDO SQL
        String sql =
                "SELECT employeeid, name, cpf, email, phone, active, " +
                        "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                "FROM employee " +
                "WHERE roleid = ? ";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setInt(1, roleId);

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //ATRIBUICAO DA LISTA
                employees = mapResultSet(resultSet);
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return employees;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public Long listCountStatus() {
        Long result = null;

        //COMANDO SQL
        String sql =
                "SELECT COUNT(employeeid) " +
                "FROM employee " +
                "WHERE active = true";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
                //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
        ) {
            //ATRIBUINDO O RESULTADO À VARIÁVEL
            while (resultSet.next()) {
                result = resultSet.getLong(1); //pega o resultado numerico da primeira coluna
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return result;
    }

    //METODO DE SELEÇÃO CORRELACIONAO COM STORE - TOTAL DE USUÁRIOS POR CIDADE
    public List<EmployeeCountByCityDTO> listCountEmployeeByCity() {
        List<EmployeeCountByCityDTO> employeeCountByCityDTOS = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT a.city, a.state, COUNT(e.employeeid) AS totalemployees " +
                "FROM employee e " +
                "INNER JOIN store s " +
                "ON e.storeid = s.storeid " +
                "INNER JOIN Address a " +
                "ON s.addressid = a.addressid " +
                "GROUP BY a.city, a.state " +
                "ORDER BY a.state, a.city";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery()
        ) {

            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                EmployeeCountByCityDTO responseDTO = new EmployeeCountByCityDTO(
                        resultSet.getString("city"),
                        resultSet.getString("state"),
                        resultSet.getInt("totalemployees")
                );
                //INSERINDO OBJETO NA LISTA
                employeeCountByCityDTOS.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeCountByCityDTOS;
    }

    //METODO DE SELEÇÃO CORRELACIONADO COM STORE E ADDRESS - TOTAL DE USUÁRIOS POR ESTADO
    public List<EmployeeCountByStateDTO> listCountEmployeeByState() {
        List<EmployeeCountByStateDTO> employeeCountByStateDTOS = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT a.state, COUNT(e.employeeid) AS totalemployees " +
                "FROM employee e " +
                "INNER JOIN store s " +
                "ON e.storeid = s.storeid " +
                "INNER JOIN address a " +
                "ON s.addressid = a.addressid " +
                "GROUP BY a.state " +
                "ORDER BY totalemployees DESC";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                EmployeeCountByStateDTO responseDTO = new EmployeeCountByStateDTO(
                        resultSet.getString("state"),
                        resultSet.getInt("totalemployees")
                );
                //INSERINDO OBJETO NA LISTA
                employeeCountByStateDTOS.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeCountByStateDTOS;
    }


    //METODO DE INSERÇÃO DE REGISTRO NO BANCO
    public int insert(Employee employee) {

        //COMANDO SQL
        String sql =
                "INSERT INTO employee(employeeid, name, cpf, email, phone, active, hiredate, createdat, updatedat, deletedat, storeid,roleid) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, employee.getEmployeeId());
            pstm.setString(2, employee.getName().trim());
            pstm.setString(3, employee.getCpf().trim());
            pstm.setString(4, employee.getEmail().trim());
            pstm.setString(5, employee.getPhone().trim());
            pstm.setBoolean(6, employee.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (employee.getHireDate() != null) {
                pstm.setDate(7, Date.valueOf(employee.getHireDate()));
            }else {
                pstm.setNull(7, Types.DATE);
            }
            if (employee.getCreatedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(employee.getCreatedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            if (employee.getUpdatedAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(employee.getUpdatedAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            if (employee.getDeletedAt() != null){
                pstm.setTimestamp(10, Timestamp.valueOf(employee.getDeletedAt()));
            }else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            pstm.setInt(11, employee.getStoreId());
            pstm.setInt(12, employee.getRoleId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int update(Employee employee) {

        //COMANDO SQL
        String sql =
                "UPDATE employee " +
                "SET name = ?, cpf = ?, email = ?, phone = ?, active = ?, " +
                        "hiredate = ?, updatedat = ?, deletedat = ?" +
                " WHERE employeeid = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName().trim());
            pstm.setString(2, employee.getCpf().trim());
            pstm.setString(3, employee.getEmail().trim());
            pstm.setString(4, employee.getPhone().trim());
            pstm.setBoolean(5, employee.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (employee.getHireDate() != null) {
                pstm.setDate(6, Date.valueOf(employee.getHireDate()));
            }else {
                pstm.setNull(6, Types.DATE);
            }
            if (employee.getUpdatedAt() != null){
                pstm.setTimestamp(7, Timestamp.valueOf(employee.getUpdatedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            if (employee.getDeletedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(employee.getDeletedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            pstm.setInt(9, employee.getEmployeeId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int updateViaCpf(Employee employee) {

        //COMANDO SQL
        String sql =
                "UPDATE employee " +
                "SET name = ?, email = ?, phone = ?, active = ?, " +
                        "hiredate = ?, updatedat = ?, deletedat = ?" +
                " WHERE cpf = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName().trim());
            pstm.setString(2, employee.getEmail().trim());
            pstm.setString(3, employee.getPhone().trim());
            pstm.setBoolean(4, employee.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (employee.getHireDate() != null) {
                pstm.setDate(5, Date.valueOf(employee.getHireDate()));
            }else {
                pstm.setNull(5, Types.DATE);
            }
            if (employee.getUpdatedAt() != null){
                pstm.setTimestamp(6, Timestamp.valueOf(employee.getUpdatedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }
            if (employee.getDeletedAt() != null){
                pstm.setTimestamp(7, Timestamp.valueOf(employee.getDeletedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            pstm.setString(8, employee.getCpf());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int updateViaEmail(Employee employee) {

        //COMANDO SQL
        String sql =
                "UPDATE employee " +
                "SET name = ?, phone = ?, active = ?, " +
                        "hiredate = ?, updatedat = ?, deletedat = ?" +
                " WHERE email = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName().trim());
            pstm.setString(2, employee.getPhone().trim());
            pstm.setBoolean(3, employee.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (employee.getHireDate() != null) {
                pstm.setDate(4, Date.valueOf(employee.getHireDate()));
            }else {
                pstm.setNull(4, Types.DATE);
            }
            if (employee.getUpdatedAt() != null){
                pstm.setTimestamp(5, Timestamp.valueOf(employee.getUpdatedAt()));
            }else {
                pstm.setNull(5, Types.TIMESTAMP);
            }
            if (employee.getDeletedAt() != null){
                pstm.setTimestamp(6, Timestamp.valueOf(employee.getDeletedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }
            pstm.setString(7, employee.getEmail());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO QUE APAGA UM REGISTRO NO BANCO
    public int delete(int employeeId) {

        //COMANDO SQL
        String sql =
                "DELETE FROM employee " +
                "WHERE employeeid = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS PARÂMETROS
            pstm.setInt(1, employeeId);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    //METODO DE DELEÇÃO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA EMAIL
    public int deleteViaEmail(String email) {

        //COMANDO SQL
        String sql =
                "DELETE FROM employee " +
                "WHERE email = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS PARÂMETROS
            pstm.setString(1,email);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE DELEÇÃO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA CPF
    public int deleteViaCpf(String cpf) {

        //COMANDO SQL
        String sql =
                "DELETE FROM employee " +
                "WHERE cpf = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS PARÂMETROS
            pstm.setString(1,cpf);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<Employee> mapResultSet(ResultSet resultSet) {
        List<Employee> employees = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");
                Date date = resultSet.getDate("hiredate");

                Employee employee = new Employee(
                        resultSet.getInt("employeeid"),
                        resultSet.getString("name"),
                        resultSet.getString("cpf"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getBoolean("active"),

                        //VALIDAÇÃO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
                        date != null
                                ? date.toLocalDate()
                                : null,
                        createdAt != null
                                ? createdAt.toLocalDateTime()
                                : null,
                        updatedAt != null
                                ? updatedAt.toLocalDateTime()
                                : null,
                        deletedAt != null
                                ? deletedAt.toLocalDateTime()
                                : null,
                        resultSet.getInt("storeid"),
                        resultSet.getInt("roleid")
                );
                //INSERINDO OBJETO NO ARRAY
                employees.add(employee);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employees;
    }
}

