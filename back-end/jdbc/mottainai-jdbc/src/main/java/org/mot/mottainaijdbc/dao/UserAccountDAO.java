package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.UserAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserAccountDAO {

    //CONSTRUTOR
    public UserAccountDAO() {
    }

    //METODO DE INSERCAO DOS REGISTROS NO BANCO
    public int insert(UserAccount userAccount) {

        //COMANDO SQL
        String sql =
                "INSERT INTO useraccount(userid, email, passwordhash, lastlogin, " +
                        "active, createdat, updatedat, deletedat, employeeid) " +
                "VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1,userAccount.getUserId());
            pstm.setString(2, userAccount.getEmail().trim());
            pstm.setString(3, userAccount.getPasswordHash().trim());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (userAccount.getLastLogin() != null) {
                pstm.setTimestamp(4, Timestamp.valueOf(userAccount.getLastLogin()));
            }else {
                pstm.setNull(4, Types.TIMESTAMP);
            }

            pstm.setBoolean(5, userAccount.isActive());

            if (userAccount.getCreatedAt() != null){
                pstm.setTimestamp(6, Timestamp.valueOf(userAccount.getCreatedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }
            if (userAccount.getUpdatedAt() != null){
                pstm.setTimestamp(7, Timestamp.valueOf(userAccount.getUpdatedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            if (userAccount.getDeletedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(userAccount.getDeletedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            pstm.setInt(9, userAccount.getEmployeeId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<UserAccount> list() {
        List<UserAccount> accountList;

        //COMANDO SQL
        String sql =
                "SELECT userid, email, passwordhash, lastlogin, active, " +
                        "createdat, updatedat, deletedat, employeeid " +
                "FROM useraccount";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            accountList = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    public List<UserAccount> listByStatus() {
        List<UserAccount> accountList;

        //COMANDO SQL
        String sql =
                "SELECT userid, email, passwordhash, lastlogin, active, " +
                        "createdat, updatedat, deletedat, employeeid " +
                "FROM useraccount " +
                "WHERE active = true";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            accountList = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA PASSWORDHASH
    public List<UserAccount> listByPasswordHash(String passwordHash) {
        List<UserAccount> accountList;

        //COMANDO SQL
        String sql =
                "SELECT userid, email, passwordhash, lastlogin, active, " +
                        "createdat, updatedat, deletedat, employeeid " +
                "FROM useraccount " +
                "WHERE passwordhash = ?";

        String passWordHashIncident = "%" + passwordHash + "%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //TROCANDO A OCORRENCIA PELO PARAMETRO COM % POR CONTA DA POSICAO
            pstm.setString(1, passWordHashIncident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                accountList = mapResultSet(resultSet);
            }


            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA EMAIL
    public List<UserAccount> listByEmail(String email) {
        List<UserAccount> accountList;

        //COMANDO SQL
        String sql =
                "SELECT userid, email, passwordhash, lastlogin, active, " +
                        "createdat, updatedat, deletedat, employeeid " +
                "FROM useraccount " +
                "WHERE email LIKE ?";

        String emailincident = "%" + email + "%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            pstm.setString(1, emailincident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                accountList = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
    }

    //METODO DO TOTAL DE REGISTROS ATIVOS
    public Long listCountStatus() {
        Long result = null;

        //COMANDO SQL
        String sql =
                "SELECT COUNT(userid) " +
                "FROM useraccount " +
                "WHERE active = true";

        try(
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery();
        ) {

            //ATRIBUINDO O RESULTADO DA PRIMEIRA COLUINA NA VARIAVEL
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

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int update(UserAccount userAccount) {

        //COMANDO SQL
        String sql =
                "UPDATE useraccount " +
                "SET email = ?, passwordhash = ?, lastlogin = ?, " +
                        "active = ?,  updatedat= ?, deletedat = ? " +
                "WHERE userid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, userAccount.getEmail().trim());
            pstm.setString(2, userAccount.getPasswordHash());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (userAccount.getLastLogin() != null) {
                pstm.setTimestamp(3, Timestamp.valueOf(userAccount.getLastLogin()));
            }else {
                pstm.setNull(3, Types.TIMESTAMP);
            }

            pstm.setBoolean(4, userAccount.isActive());

            if (userAccount.getUpdatedAt() != null) {
                pstm.setTimestamp(5, Timestamp.valueOf(userAccount.getUpdatedAt()));
            }else {
                pstm.setNull(5, Types.TIMESTAMP);
            }
            if (userAccount.getDeletedAt() != null) {
                pstm.setTimestamp(6, Timestamp.valueOf(userAccount.getDeletedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }
            pstm.setInt(7, userAccount.getUserId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO VIA EMAIL
    public int updateViaEmail(UserAccount userAccount) {

        //COMANDO SQL
        String sql =
                "UPDATE useraccount " +
                "SET passwordhash = ?, lastlogin = ?, " +
                        "active = ?,  updatedat= ?, deletedat = ? " +
                "WHERE email LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DA OCORRENCIA PELO DADO DO OBJETO
            pstm.setString(1, userAccount.getPasswordHash());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (userAccount.getLastLogin() != null) {
                pstm.setTimestamp(2, Timestamp.valueOf(userAccount.getLastLogin()));
            }else {
                pstm.setNull(2, Types.TIMESTAMP);
            }

            pstm.setBoolean(3, userAccount.isActive());

            if (userAccount.getUpdatedAt() != null) {
                pstm.setTimestamp(4, Timestamp.valueOf(userAccount.getUpdatedAt()));
            }else {
                pstm.setNull(4, Types.TIMESTAMP);
            }
            if (userAccount.getDeletedAt() != null) {
                pstm.setTimestamp(5, Timestamp.valueOf(userAccount.getDeletedAt()));
            }else {
                pstm.setNull(5, Types.TIMESTAMP);
            }
            pstm.setString(6, userAccount.getEmail());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO
    public int delete(int id) {

        //COMANDO SQL
        String sql =
                "DELETE FROM useraccount " +
                "WHERE userid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DA OCORRENCIA PELO DADO DO OBJETO
            pstm.setInt(1, id);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
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
            String sql =
                    "DELETE FROM useraccount " +
                    "WHERE email = ?";

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

    //METODO DE DELEÇÃO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA PASSWORDHASH
    public int deleteViaPasswordHash(String passWordHash) {
        try {
            //COMANDO SQL
            String sql =
                    "DELETE FROM useraccount " +
                    "WHERE passwordhash = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS PARÂMETROS
            pstm.setString(1,passWordHash);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<UserAccount> mapResultSet(ResultSet resultSet) {
        List<UserAccount> userAccountList = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");
                Timestamp lastLogin = resultSet.getTimestamp("lastlogin");

                UserAccount userAccount = new UserAccount(
                        resultSet.getInt("userid"),
                        resultSet.getString("email"),
                        resultSet.getString("passwordhash"),

                        //VALIDACAO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
                        lastLogin != null
                                ? lastLogin.toLocalDateTime()
                                : null,

                        resultSet.getBoolean("active"),

                        createdAt != null
                                ? createdAt.toLocalDateTime()
                                : null,
                        updatedAt != null
                                ? updatedAt.toLocalDateTime()
                                : null,
                        deletedAt != null
                                ? deletedAt.toLocalDateTime()
                                : null,
                        resultSet.getInt("employeeid")
                );
                //INSERINDO OBJETO NO ARRAY
                userAccountList.add(userAccount);
            }
            //EXCECAO
        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return userAccountList;
    }
}
