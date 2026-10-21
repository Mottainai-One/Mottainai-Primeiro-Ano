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

    //MÉTODOS
    public int insert(Store store) {
        try {
            //COMANDO SQL
            String sql =
                    "INSERT INTO store(storeid, name, cnpj, email, phone, latitude, longitude, active, createdat, updatedat, deletedat, companyid, addressid) " +
                    "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, store.getStoreId());
            pstm.setString(2, store.getName());
            pstm.setString(3, store.getCnpj());
            pstm.setString(4, store.getEmail().trim());
            pstm.setString(5, store.getPhone());
            pstm.setDouble(6, store.getLatitude());
            pstm.setDouble(7, store.getLongitude());
            pstm.setBoolean(8, store.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
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

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO
    public List<Store> list() {
        List<Store> stores;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                            "createdat, updatedat, deletedat, companyid, addressid " +
                    "FROM store";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE EMPREGADOS POR LOJA
    public List<StoreEmployeeCountDTO> listCountEmployeeByStore() {
        List<StoreEmployeeCountDTO> stores = new ArrayList<>();

        try {
            //COMANDO SQL
            String sql =
                    "SELECT s.storeid, s.name AS store, COUNT(e.employeeid) AS totalemployee " +
                    "FROM store s " +
                    "LEFT JOIN employee e " +
                    "ON e.storeid = s.storeid " +
                    "GROUP BY s.storeid, s.name " +
                    "ORDER BY totalemployee DESC";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

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

        try {
            //COMANDO SQL
            String sql =
                    "SELECT a.city, a.state, COUNT(s.storeid) AS totalstore " +
                    "FROM store s " +
                    "INNER JOIN address a " +
                    "ON s.addressid = a.addressid " +
                    "GROUP BY a.city, a.state " +
                    "ORDER BY a.state, a.city";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

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

    //METODO DE LOJAS POR ESTADO
    public List<StoreCountByStateDTO> listCountStoreByState() {
        List<StoreCountByStateDTO> stores = new ArrayList<>();

        try {
            //COMANDO SQL
            String sql =
                    "SELECT a.state, COUNT(s.storeid) AS totalstore " +
                    "FROM Store s " +
                    "INNER JOIN Address a " +
                    "ON s.AddressId = a.AddressId " +
                    "GROUP BY a.State " +
                    "ORDER BY totalstore DESC";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

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

    //METODO DE LISTAGEM DOS USUÁRIOS POR LOJA - CORRELACIONADO
    public List<StoreUserAccountCountDTO> listCountUsersByStore() {
        List<StoreUserAccountCountDTO> accountCountDTOS = new ArrayList<>();

        try {
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

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

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

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO DE QUEM ESTIVER ATIVO
    public List<Store> listByStatus() {
        List<Store> stores;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                            "createdat, updatedat, deletedat, companyid, addressid " +
                    "FROM store " +
                    "WHERE active = true";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }


    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA NOME
    public List<Store> listByName(String name) {
        List<Store> stores;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                        "createdat, updatedat, deletedat, companyid, addressid " +
                    "FROM store " +
                    "WHERE name LIKE ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            String nameIncident = "%"+ name +"%";

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, nameIncident);

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA CNPJ
    public List<Store> listByCnpj(String cnpj) {
        List<Store> stores;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT storeid, name, cnpj, email, phone, latitude, longitude, active, " +
                            "createdat, updatedat, deletedat, companyid, addressid " +
                    "FROM store " +
                    "WHERE cnpj LIKE ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);
            ResultSet resultSet = pstm.executeQuery();

            String cnpjIncident = "%"+ cnpj +"%";

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, cnpjIncident);

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            stores = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return stores;
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
