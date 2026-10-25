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

    //METODO DE INSERCAO DOS REGISTROS NO BANCO
    public int insert(SubscriptionPlan subscriptionPlan) {

        //COMANDO SQL
        String sql =
                "INSERT INTO subscriptionplan(planid, name, description, price, " +
                        "storelimit, userlimit, active, createdat, updatedat, deletedat) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, subscriptionPlan.getPlanId());
            pstm.setString(2, subscriptionPlan.getName().trim());
            pstm.setString(3, subscriptionPlan.getDescription().trim());
            pstm.setBigDecimal(4, subscriptionPlan.getPrice());
            pstm.setInt(5, subscriptionPlan.getStoreLimit());
            pstm.setInt(6, subscriptionPlan.getUserLimit());
            pstm.setBoolean(7, subscriptionPlan.isActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
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

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<SubscriptionPlan> list() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
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

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA NOME
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
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, nameIncident );

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO QUE ARMAZENA O RETORNO DO .mapResulSet()
                subscriptionPlanList = mapResulSet(resultSet);
            }
            //EXCECAO
        } catch (SQLException ex) {
            throw new RuntimeException(ex);
        }
        //RETORNO DA LISTA
        return subscriptionPlanList;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA DESCRICAO
    public List<SubscriptionPlan> listByDescription(String description) {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE description LIKE ?";

        String descriptionIncident = "%" + description + "%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, descriptionIncident );

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

    //METODO DE SELECAO DOS REGISTROS NO BANCO DE QUEM ESTA ATIVO
    public List<SubscriptionPlan> listByStatus() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE active = true";


        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
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

    //METODO DO TOTAL DE PLANOS ATIVOS
    public Long listCountStatus() {
        Long result = null;

        //COMANDO SQL
        String sql =
                "SELECT COUNT(planid) " +
                "FROM subscriptionplan " +
                "WHERE active = true";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {

            //ATRIBUINDO O RESULTADO DA PRIMEIRA COLUNA NA VARIAVEL
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

    //METODO DE LISTAGEM DOS REGISTROS DO PLANO QUE TEM O PRECO MAIS BAIXO
    public List<SubscriptionPlan> listMinPlan() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE price = (SELECT MIN(price) FROM subscriptionplan)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE LISTAGEM DOS REGISTROS DOS PLANOS QUE TEM O MAIOR PRECO
    public List<SubscriptionPlan> listMinStoreLimit() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE storelimit = (SELECT MIN(storelimit) FROM subscriptionplan)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO PLANO QUE TEM MAIOR NUMERO DE LOJAS
    public List<SubscriptionPlan> listMaxStoreLimit() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE storelimit = (SELECT MAX(storelimit) FROM subscriptionplan)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery();
        ) {

            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO PLANO QUE TEM O MENOR NUMERO DE LOJAS
    public List<SubscriptionPlan> listMinUserLimit() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE userlimit = (SELECT MIN(userlimit) FROM subscriptionplan)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO PLANO QUE TEM O MAIOR LIMITE DE USUÁRIO
    public List<SubscriptionPlan> listMaxUserLimit() {
        List<SubscriptionPlan> subscriptionPlanList;

        //COMANDO SQL
        String sql =
                "SELECT planid, name, description, price, storelimit, userlimit, " +
                        "active, createdat, updatedat, deletedat " +
                "FROM subscriptionplan " +
                "WHERE userlimit = (SELECT MAX(userlimit) FROM subscriptionplan)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE LISTAGEM DOS REGISTROS DO PLANO QUE TEM O MARIO PREÇO
    public List<SubscriptionPlan> listMaxPlan() {
        List<SubscriptionPlan> subscriptionPlanList;
        try {
            //COMANDO SQL
            String sql =
                    "SELECT planid, name, description, price, storelimit, userlimit, " +
                            "active, createdat, updatedat, deletedat " +
                    "FROM subscriptionplan " +
                    "WHERE price = (SELECT MAX(price) FROM subscriptionplan)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO QUE ARMAZENA O RESULTSET
            subscriptionPlanList = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DO RESULTADO
        return subscriptionPlanList;
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int update(SubscriptionPlan subscriptionPlan) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE subscriptionplan " +
                    "SET name = ?, description = ?, price = ?, storelimit = ?, userlimit = ?, " +
                            "active = ?,  updatedat = ?, deletedat = ? " +
                    "WHERE planid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, subscriptionPlan.getName());
            pstm.setString(2, subscriptionPlan.getDescription());
            pstm.setBigDecimal(3, subscriptionPlan.getPrice());
            pstm.setInt(4, subscriptionPlan.getStoreLimit());
            pstm.setInt(5, subscriptionPlan.getUserLimit());
            pstm.setBoolean(6, subscriptionPlan.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (subscriptionPlan.getUpdatedAt() != null) {
                pstm.setTimestamp(7, Timestamp.valueOf(subscriptionPlan.getUpdatedAt()));
            } else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            if (subscriptionPlan.getUpdatedAt() != null) {
                pstm.setTimestamp(8, Timestamp.valueOf(subscriptionPlan.getUpdatedAt()));
            } else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            pstm.setInt(9, subscriptionPlan.getPlanId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int updateViaAmount(SubscriptionPlan subscriptionPlan) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE subscriptionplan " +
                    "SET description = ?, price = ?, " +
                            "active = ?,  updatedat = ?, deletedat = ? " +
                    "WHERE storelimit = ? AND userlimit = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, subscriptionPlan.getDescription());
            pstm.setBigDecimal(2, subscriptionPlan.getPrice());
            pstm.setBoolean(3, subscriptionPlan.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (subscriptionPlan.getUpdatedAt() != null) {
                pstm.setTimestamp(4, Timestamp.valueOf(subscriptionPlan.getUpdatedAt()));
            } else {
                pstm.setNull(4, Types.TIMESTAMP);
            }
            if (subscriptionPlan.getUpdatedAt() != null) {
                pstm.setTimestamp(5, Timestamp.valueOf(subscriptionPlan.getUpdatedAt()));
            } else {
                pstm.setNull(5, Types.TIMESTAMP);
            }
            pstm.setInt(6, subscriptionPlan.getStoreLimit());
            pstm.setInt(7, subscriptionPlan.getUserLimit());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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

                        //VALIDACAO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
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
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return subscriptionPlans;
    }
}
