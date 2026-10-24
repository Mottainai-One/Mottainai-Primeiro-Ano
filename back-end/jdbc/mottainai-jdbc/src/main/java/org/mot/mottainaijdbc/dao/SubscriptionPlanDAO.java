package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.SubscriptionPlan;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubscriptionPlanDAO {

    //CONSTRUTOR
    public SubscriptionPlanDAO() {
    }

    //METODO DE INSERÇÃO DOS REGISTROS NO BANCO
    public int insert(SubscriptionPlan subscriptionPlan) {

        //COMANDO SQL
        String sql =
                "INSERT INTO subscriptionplan(planid, name, description, price, " +
                        "storelimit, userlimit, active, createdat, updatedat, deletedat) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, subscriptionPlan.getPlanId());
            pstm.setString(2, subscriptionPlan.getName());
            pstm.setString(3, subscriptionPlan.getDescription());
            pstm.setBigDecimal(4, subscriptionPlan.getPrice());
            pstm.setInt(5, subscriptionPlan.getStoreLimit());
            pstm.setInt(6, subscriptionPlan.getUserLimit());
            pstm.setBoolean(7, subscriptionPlan.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (subscriptionPlan.getCreatedAt() != null) {
                pstm.setTimestamp(8, Timestamp.valueOf(subscriptionPlan.getCreatedAt()));
            } else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            if (subscriptionPlan.getUpdatedAt() != null) {
                pstm.setTimestamp(9, Timestamp.valueOf(subscriptionPlan.getUpdatedAt()));
            } else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            if (subscriptionPlan.getDeletedAt() != null) {
                pstm.setTimestamp(10, Timestamp.valueOf(subscriptionPlan.getDeletedAt()));
            } else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            pstm.setInt(11, subscriptionPlan.getCompanyId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO
    public List<SubscriptionPlan> list() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        //RETORNO DA LISTA
        return subscriptionPlanList;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA NOME
    public List<SubscriptionPlan> listByName(String name) {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE name LIKE ?";

        String nameIncident = "%" + name + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, nameIncident );

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
                subscriptionPlanList = mapResulSet(resultSet);
            }
            //EXCECAO
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        //RETORNO DA LISTA
        return subscriptionPlanList;
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<SubscriptionPlan> mapResulSet(ResultSet resultSet) {
        List<SubscriptionPlan> subscriptionPlans = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");

                SubscriptionPlan subscriptionPlan = new SubscriptionPlan(
                        resultSet.getInt("planid"),
                        resultSet.getString("name"),
                        resultSet.getString("description"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getInt("storelimit"),
                        resultSet.getInt("userlimit"),
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
                                : null,
                        resultSet.getInt("companyid")
                );
                //ADICIONANDO O OBJETO NO ARRAY
                subscriptionPlans.add(subscriptionPlan);
            }
            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return subscriptionPlans;
    }
}
