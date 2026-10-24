package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.dto.StoreCountByCityDTO;
import org.mot.mottainaijdbc.dto.StoreCountByStateDTO;
import org.mot.mottainaijdbc.dto.StoreEmployeeCountDTO;
import org.mot.mottainaijdbc.dto.StoreUserAccountCountDTO;
import org.mot.mottainaijdbc.model.Store;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StoreDAO {

    //CONSTRUTOR
    public StoreDAO() {
    }

    //METODO DE INSERCAO DOS REGISTROS NO BANCO
    public int insert(Store store) {

        //COMANDO SQL
        String sql =
                "INSERT INTO store(storeid, name, cnpj, email, phone, latitude, longitude, active, createdat, updatedat, deletedat, companyid, addressid) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, store.getStoreId());
            pstm.setString(2, store.getName().trim());
            pstm.setString(3, store.getCnpj().trim());
            pstm.setString(4, store.getEmail().trim());
            pstm.setString(5, store.getPhone().trim());
            pstm.setDouble(6, store.getLatitude());
            pstm.setDouble(7, store.getLongitude());
            pstm.setBoolean(8, store.isActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (store.getCreatedAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(store.getCreatedAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            if (store.getUpdatedAt() != null){
                pstm.setTimestamp(10, Timestamp.valueOf(store.getUpdatedAt()));
            }else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            if (store.getDeletedAt() != null){
                pstm.setTimestamp(11, Timestamp.valueOf(store.getDeletedAt()));
            }else {
                pstm.setNull(11, Types.TIMESTAMP);
            }
            pstm.setInt(12, store.getCompanyId());
            pstm.setInt(13, store.getAdressId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<Store> list() {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO SQL
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE LISTAGEM CORRELACIONADA COM EMPLOYEE
    public List<StoreEmployeeCountDTO> listCountEmployeeByStore() {
        List<StoreEmployeeCountDTO> stores = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT s.storeid, s.name AS store, COUNT(e.employeeid) AS totalemployee " +
                "FROM store s " +
                "LEFT JOIN employee e " +
                "ON e.storeid = s.storeid " +
                "GROUP BY s.storeid, s.name " +
                "ORDER BY totalemployee DESC";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql);

                 //EXECUTANDO INSTRUCAO SQL
                 ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                StoreEmployeeCountDTO responseDTO = new StoreEmployeeCountDTO(
                        resultSet.getInt("storeid"),
                        resultSet.getString("store"),
                        resultSet.getInt("totalemployee")
                );
                //INSERINDO OBJETO NA LISTA
                stores.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE LOJAS POR CIDADE
    public List<StoreCountByCityDTO> listCountStoreByCity() {
        List<StoreCountByCityDTO> stores = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT a.city, a.state, COUNT(s.storeid) AS totalstore " +
                "FROM store s " +
                "INNER JOIN address a " +
                "ON s.addressid = a.addressid " +
                "GROUP BY a.city, a.state " +
                "ORDER BY a.state, a.city";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO INSTRUCAO SQL
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                StoreCountByCityDTO responseDTO = new StoreCountByCityDTO(
                        resultSet.getString("city"),
                        resultSet.getString("state"),
                        resultSet.getInt("totalstore")
                );
                //INSERINDO OBJETO NA LISTA
                stores.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE CORRELACIONADO COM STORE E ADDRESS
    public List<StoreCountByStateDTO> listCountStoreByState() {
        List<StoreCountByStateDTO> stores = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT a.state, COUNT(s.storeid) AS totalstore " +
                "FROM Store s " +
                "INNER JOIN Address a " +
                "ON s.AddressId = a.AddressId " +
                "GROUP BY a.State " +
                "ORDER BY totalstore DESC";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO INSTRUCAO SQL
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                StoreCountByStateDTO responseDTO = new StoreCountByStateDTO(
                        resultSet.getString("state"),
                        resultSet.getInt("totalstore")
                );
                //INSERINDO OBJETO NA LISTA
                stores.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE LISTAGEM DOS USUARIOS POR LOJA - CORRELACIONADO
    public List<StoreUserAccountCountDTO> listCountUsersByStore() {
        List<StoreUserAccountCountDTO> accountCountDTOS = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT s.storeid, s.name AS store,COUNT(ua.userid) AS totalusers " +
                "FROM store s " +
                "LEFT JOIN employee e " +
                "ON e.storeid = s.storeid " +
                "LEFT JOIN useraccount ua " +
                "ON ua.employeeid = e.employeeid AND ua.active = true " +
                "GROUP BY s.storeid, s.name " +
                "ORDER BY totalusers DESC";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO INSTRUCAO SQL
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                StoreUserAccountCountDTO accountCountDTO = new StoreUserAccountCountDTO(
                        resultSet.getInt("storeid"),
                        resultSet.getString("store"),
                        resultSet.getInt("totalusers")
                );
                //INSERINDO OBJETO NA LISTA
                accountCountDTOS.add(accountCountDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return accountCountDTOS;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO DE QUEM ESTIVER ATIVO
    public List<Store> listByStatus() {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE active = true";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO INSTRUCAO SQL
                ResultSet resultSet = pstm.executeQuery()
        ) {
            //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }


    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA NOME
    public List<Store> listByName(String name) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE name LIKE ?";

        String nameIncident = "%"+ name +"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
        ) {

            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, nameIncident);

            try (
                    //EXECUTANDO A INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA CNPJ
    public List<Store> listByCnpj(String cnpj) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE cnpj LIKE ?";

        String cnpjIncident = "%"+ cnpj +"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, cnpjIncident);

            try (
                    //EXECUTANDO INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery()
            ) {

                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA EMAIL
    public List<Store> listByEmail(String email) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE email LIKE ?";

        String emailIncident = "%" + email + "%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, emailIncident);

            try (
                    //EXECUTANDO INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery();
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA TELEFONE
    public List<Store> listByPhone(String phone) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE phone LIKE ?";

        String phoneIncident = "%" + phone + "%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, phoneIncident);

            try (
                    //EXECUTANDO INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA ID DA EMPRESA
    public List<Store> listByCompanyId(int companyId) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE companyid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setInt(1, companyId);

            try (
                    //EXECUTANDO INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery();
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA ID DO ENDEREÇO
    public List<Store> listByAddressId(int addressid) {
        List<Store> stores;

        //COMANDO SQL
        String sql =
                "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                "FROM store " +
                "WHERE addressid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setInt(1, addressid);

            try (
                    //EXECUTANDO INSTRUCAO SQL
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                stores = mapResultSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DO TOTAL DE LOJAS ATIVAS
    public Long listCountStatus() {
        Long result = null;

        //COMANDO SQL
        String sql =
                "SELECT COUNT(storeid) " +
                "FROM store " +
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

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int update(Store store) {

        //COMANDO SQL
        String sql =
                "UPDATE store " +
                "SET name = ?, cnpj = ?, email = ?, phone = ?, latitude = ?, longitude = ?, " +
                        "active = ?, updatedat = ?, deletedat = ?, companyid = ?, addressid = ? " +
                "WHERE storeid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, store.getName().trim());
            pstm.setString(2, store.getCnpj().trim());
            pstm.setString(3, store.getEmail().trim());
            pstm.setString(4, store.getPhone().trim());
            pstm.setDouble(5, store.getLatitude());
            pstm.setDouble(6, store.getLongitude());
            pstm.setBoolean(7, store.isActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (store.getUpdatedAt() != null) {
                pstm.setTimestamp(8, Timestamp.valueOf(store.getUpdatedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }if (store.getDeletedAt() != null) {
                pstm.setTimestamp(9, Timestamp.valueOf(store.getDeletedAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            pstm.setInt(10, store.getCompanyId());
            pstm.setInt(11, store.getAdressId());
            pstm.setInt(12, store.getStoreId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO VIA CNPJ
    public int updateViaCnpj(Store store) {

        //COMANDO SQL
        String sql =
                "UPDATE store " +
                "SET name = ?, email = ?, phone = ?, latitude = ?, longitude = ?, " +
                        "active = ?, updatedat = ?, deletedat = ?, companyid = ?, addressid = ? " +
                "WHERE cnpj = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, store.getName().trim());
            pstm.setString(2, store.getEmail().trim());
            pstm.setString(3, store.getPhone().trim());
            pstm.setDouble(4, store.getLatitude());
            pstm.setDouble(5, store.getLongitude());
            pstm.setBoolean(6, store.isActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (store.getUpdatedAt() != null) {
                pstm.setTimestamp(7, Timestamp.valueOf(store.getUpdatedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }if (store.getDeletedAt() != null) {
                pstm.setTimestamp(8, Timestamp.valueOf(store.getDeletedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            pstm.setInt(9, store.getCompanyId());
            pstm.setInt(10, store.getAdressId());
            pstm.setString(11, store.getCnpj());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO VIA EMAIL
    public int updateViaEmail(Store store) {

        //COMANDO SQL
        String sql =
                "UPDATE store " +
                "SET name = ?, phone = ?, latitude = ?, longitude = ?, " +
                        "active = ?, updatedat = ?, deletedat = ?, companyid = ?, addressid = ? " +
                "WHERE email = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, store.getName().trim());
            pstm.setString(2, store.getPhone().trim());
            pstm.setDouble(3, store.getLatitude());
            pstm.setDouble(4, store.getLongitude());
            pstm.setBoolean(5, store.isActive());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (store.getUpdatedAt() != null) {
                pstm.setTimestamp(6, Timestamp.valueOf(store.getUpdatedAt()));
            }else {
                pstm.setNull(6, Types.TIMESTAMP);
            }if (store.getDeletedAt() != null) {
                pstm.setTimestamp(7, Timestamp.valueOf(store.getDeletedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            pstm.setInt(8, store.getCompanyId());
            pstm.setInt(9, store.getAdressId());
            pstm.setString(10, store.getEmail());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO
    public int delete(int storeCode) {

        //COMANDO SQL
        String sql =
                "DELETE FROM store " +
                "WHERE storeid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS PARAMETROS
            pstm.setInt(1, storeCode);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE APAGAR OS REGISTROS NO BANCO VIA CNPJ
    public int deleteViaCnpj(String cnpj) {

        //COMANDO SQL
        String sql =
                "DELETE FROM store " +
                "WHERE cnpj = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELO PARAMETRO
            pstm.setString(1, cnpj);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA EMAIL
    public int deleteViaEmail(String email) {

        //COMANDO SQL
        String sql =
                "DELETE FROM administration " +
                "WHERE email = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUICAO DAS OCORRENCIAS PELOS PARAMETROS
            pstm.setString(1,email);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<Store> mapResultSet(ResultSet resultSet) {
        List<Store> stores = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");

                Store store = new Store(
                        resultSet.getInt("storeid"),
                        resultSet.getString("name"),
                        resultSet.getString("cnpj"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getDouble("latitude"),
                        resultSet.getDouble("longitude"),
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
                        resultSet.getInt("companyid"),
                        resultSet.getInt("addressid")
                );
                //INSERINDO OBJETO NA LISTA
                stores.add(store);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }
}
