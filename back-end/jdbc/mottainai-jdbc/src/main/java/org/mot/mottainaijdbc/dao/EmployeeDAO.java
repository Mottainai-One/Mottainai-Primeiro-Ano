package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE active = true ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE name LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+ name+ "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE email LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+ email + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE cpf LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+ cpf + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE phone LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+ phone + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE storeid = ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setInt(1, storeId);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT employeeid, name, cpf, email, phone, active, " +
                            "hiredate, createdat, updatedat, deletedat, storeid, roleid " +
                            "FROM employee " +
                            "WHERE roleid = ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setInt(1, roleId);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //ATRIBUICAO DA LISTA
            employees = mapResultSet(resultSet);

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
        try {
            //COMANDO SQL
            String sql =
                    "SELECT COUNT(employeeid) " +
                            "FROM employee " +
                            "WHERE active = true";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

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


    //METODO DE INSERÇÃO DE REGISTRO NO BANCO
    public int insert(Employee employee) {
        try {
            //COMANDO SQL
            String sql =
                    "INSERT INTO employee(employeeid, name, cpf, email, phone, active, hiredate, createdat, updatedat, deletedat, storeid,roleid) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, employee.getEmployeeId());
            pstm.setString(2, employee.getName());
            pstm.setString(3, employee.getCpf());
            pstm.setString(4, employee.getEmail());
            pstm.setString(5, employee.getPhone());
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
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE employee " +
                            "SET name = ?, cpf = ?, email = ?, phone = ?, active = ?, " +
                            "hiredate = ?, updatedat = ?, deletedat = ?" +
                            " WHERE employeeid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName());
            pstm.setString(2, employee.getCpf());
            pstm.setString(3, employee.getEmail());
            pstm.setString(4, employee.getPhone());
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
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE employee " +
                            "SET name = ?, email = ?, phone = ?, active = ?, " +
                            "hiredate = ?, updatedat = ?, deletedat = ?" +
                            " WHERE cpf = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName());
            pstm.setString(2, employee.getEmail());
            pstm.setString(3, employee.getPhone());
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
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE employee " +
                            "SET name = ?, phone = ?, active = ?, " +
                            "hiredate = ?, updatedat = ?, deletedat = ?" +
                            " WHERE email = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, employee.getName());
            pstm.setString(2, employee.getPhone());
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
        try {
            //COMANDO SQL
            String sql = "DELETE FROM employee WHERE employeeid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

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
        try {
            //COMANDO SQL
            String sql = "DELETE FROM employee WHERE email = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

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
        try {
            //COMANDO SQL
            String sql = "DELETE FROM employee WHERE cpf = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

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

