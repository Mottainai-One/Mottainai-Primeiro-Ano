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

    //METODO DE INSERÇÃO DOS REGISTROS NO BANCO
    public int insert(UserAccount userAccount) {
        try {
            //COMANDO SQL
            String sql =
                    "INSERT INTO useraccount(userid, email, passwordhash, lastlogin, " +
                            "active, createdat, updatedat, deletedat, employeeid) " +
                    "VALUES (?, ?, ?, ?, ?, ? , ?, ?, ?)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1,userAccount.getUserId());
            pstm.setString(2, userAccount.getEmail());
            pstm.setString(3, userAccount.getPasswordHash());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
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

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO
    public List<UserAccount> list() {
        List<UserAccount> accountList;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT userid, email, passwordhash, lastlogin, active, " +
                            "createdat, updatedat, deletedat, employeeid " +
                    "FROM useraccount";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            accountList = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO DE QUEM ESTÁ ATIVO
    public List<UserAccount> listByStatus() {
        List<UserAccount> accountList;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT userid, email, passwordhash, lastlogin, active, " +
                            "createdat, updatedat, deletedat, employeeid " +
                            "FROM useraccount " +
                            "WHERE active = true";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            accountList = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountList;
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

                        //VALIDAÇÃO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
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
