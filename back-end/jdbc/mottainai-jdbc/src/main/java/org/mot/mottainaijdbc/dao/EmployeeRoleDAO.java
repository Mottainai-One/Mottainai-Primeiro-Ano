package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.dto.EmployeeRoleEmployeeCountDTO;
import org.mot.mottainaijdbc.model.EmployeeRole;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeRoleDAO {

    //METODO DE INSERCAO DE REGISTRO NO BANCO
    public int insert(EmployeeRole employeeRole) {

        //COMANDO SQL
        String sql =
                "INSERT INTO employeerole(roleid, name, description, permissionlevel, active, createdat, updatedat, deletedat) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, employeeRole.getIdRole());
            pstm.setString(2, employeeRole.getName().trim());
            pstm.setString(3, employeeRole.getDescription().trim());
            pstm.setInt(4, employeeRole.getPermissionLevel());
            pstm.setBoolean(5, employeeRole.getActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (employeeRole.getCreatedAt() != null){
                pstm.setTimestamp(6, Timestamp.valueOf(employeeRole.getCreatedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }
            if (employeeRole.getUpdatedAt() != null){
                pstm.setTimestamp(7, Timestamp.valueOf(employeeRole.getUpdatedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            if (employeeRole.getDeletedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(employeeRole.getDeletedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int update(EmployeeRole employeeRole) {

        //COMANDO SQL
        String sql =
                "UPDATE employeerole " +
                "SET  name = ?, description = ?, permissionlevel = ?, " +
                        "active = ?, updatedat = ?, deletedat = ? " +
                "WHERE roleid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //VALIDACAO DO PARAMETRO
            if (employeeRole.getIdRole() < 1) {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }
            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
                pstm.setString(1, employeeRole.getName().trim());
                pstm.setString(2, employeeRole.getDescription().trim());
                pstm.setInt(3, employeeRole.getPermissionLevel());
                pstm.setBoolean(4, employeeRole.getActive());

                //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
                if (employeeRole.getUpdatedAt() != null) {
                    pstm.setTimestamp(5, Timestamp.valueOf(employeeRole.getUpdatedAt()));
                } else {
                    pstm.setNull(5, Types.TIMESTAMP);
                }
                if (employeeRole.getDeletedAt() != null) {
                    pstm.setTimestamp(6, Timestamp.valueOf(employeeRole.getDeletedAt()));
                } else {
                    pstm.setNull(6, Types.TIMESTAMP);
                }
                pstm.setInt(7, employeeRole.getIdRole());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int updateViaName(EmployeeRole employeeRole) {

        //COMANDO SQL
        String sql =
                "UPDATE employeerole " +
                "SET description = ?, permissionlevel = ?, " +
                        "active = ?, updatedat = ?, deletedat = ? " +
                "WHERE name = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (employeeRole.getName().isBlank() || employeeRole.getName() == null) {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }

            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
                pstm.setString(1, employeeRole.getDescription().trim());
                pstm.setInt(2, employeeRole.getPermissionLevel());
                pstm.setBoolean(3, employeeRole.getActive());

                //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
                if (employeeRole.getUpdatedAt() != null) {
                    pstm.setTimestamp(4, Timestamp.valueOf(employeeRole.getUpdatedAt()));
                } else {
                    pstm.setNull(4, Types.TIMESTAMP);
                }
                if (employeeRole.getDeletedAt() != null) {
                    pstm.setTimestamp(5, Timestamp.valueOf(employeeRole.getDeletedAt()));
                } else {
                    pstm.setNull(5, Types.TIMESTAMP);
                }
                pstm.setString(6, employeeRole.getName());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO
    public int delete(int idRole) {

        //COMANDO SQL
        String sql =
                "DELETE FROM employeerole " +
                "WHERE roleid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (idRole < 1) {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }

            else {
                //SUBSTITUICAO DA OCORRENCIA PELO PARÂMETRO
                pstm.setInt(1, idRole);

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO VIA NOME DA REGRA
    public int deleteViaName(String name) {

        //COMANDO SQL
        String sql =
                "DELETE FROM employeerole " +
                "WHERE name = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ){
            //VALIDACAO DO PARAMETRO
            if (name.isBlank() || name == null) {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }
            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS PARAMETROS
                pstm.setString(1, name.trim());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRole> list() {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT  roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ){

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            employeeRoles = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA ID DO CARGO
    public List<EmployeeRole> listByEmployeeRoleId(int id) {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole " +
                "WHERE roleid = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (id < 1) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }
            else {
                pstm.setInt(1, id);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    employeeRoles = mapResultSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }


    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRoleEmployeeCountDTO> listEmployeeByRole() {
        List<EmployeeRoleEmployeeCountDTO> employeeRoles = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT er.roleid, er.name AS employeerole, COUNT(e.employeeid) AS totalemployee " +
                "FROM employeerole er " +
                "LEFT JOIN employee e " +
                "ON e.roleid = er.roleid " +
                "GROUP BY er.roleid, er.name "+
                "ORDER BY totalemployee";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                EmployeeRoleEmployeeCountDTO responseDTO = new EmployeeRoleEmployeeCountDTO(
                        resultSet.getInt("roleid"),
                        resultSet.getString("employeerole"),
                        resultSet.getInt("totalemployee")
                );
                //INSERINDO OBJETO NA LISTA
                employeeRoles.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRole> listByName(String name) {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT  roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole " +
                "WHERE name LIKE ?";

        String nameIncident = "%"+ name +"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (name.isBlank() || name == null) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, nameIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    employeeRoles = mapResultSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRole> listByDescription(String description) {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT  roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole " +
                "WHERE description LIKE ?";

        String descriptionIncident = "%"+ description +"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (description.isBlank() || description == null) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, descriptionIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                    employeeRoles = mapResultSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRole> listByPermissionLevel(int permissionLevel) {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT  roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole " +
                "WHERE permissionlevel = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (permissionLevel < 1) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO
                pstm.setInt(1, permissionLevel);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    employeeRoles = mapResultSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO BANCO
    public List<EmployeeRole> listByStatus() {
        List<EmployeeRole> employeeRoles;

        //COMANDO SQL
        String sql =
                "SELECT  roleid, name, description, permissionlevel, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM employeerole " +
                "WHERE active = true";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            employeeRoles = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public Long listCountStatus() {
        Long result = null;

        //COMANDO SQL
        String sql =
                "SELECT COUNT(roleid) " +
                "FROM employeerole " +
                "WHERE active = true";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //ATRIBUINDO O RESULTADO DA PRIMEIRA COLUNA NA VARIÁVEL
            while (resultSet.next()) {
                result = resultSet.getLong(1);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return result;
    }


    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<EmployeeRole> mapResultSet(ResultSet resultSet) {
        List<EmployeeRole> employeeRoles = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()){
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");

                EmployeeRole employeeRole = new EmployeeRole(
                        resultSet.getInt("roleid"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getInt("permissionlevel"),
                        resultSet.getBoolean("active"),

                        //VALIDAÇÃO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
                        createdAt != null
                                ? createdAt.toLocalDateTime()
                                : null,
                        updatedAt != null
                                ? updatedAt.toLocalDateTime()
                                : null,
                        deletedAt != null
                                ? deletedAt.toLocalDateTime()
                                : null
                );
                //INSERINDO OBJETO NA LISTA
                employeeRoles.add(employeeRole);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return employeeRoles;
    }
}

