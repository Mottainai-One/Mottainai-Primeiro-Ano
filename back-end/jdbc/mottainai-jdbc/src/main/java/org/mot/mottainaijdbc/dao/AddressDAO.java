package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.Address;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AddressDAO {

    //CONSTRUTOR
    public AddressDAO() {
    }

    //METODO DE INSERÇÃO NO BANCO
    public int insert(Address address) {

        //COMANDO SQL
        String sql =
                "INSERT INTO address(addressid, zipcode, street, number, complement, neighborhood, city, state, createdat, updatedat, deletedat) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, address.getAddressId());
            pstm.setString(2, address.getZipCode());
            pstm.setString(3, address.getStreet());
            pstm.setString(4, address.getNumber());
            pstm.setString(5, address.getComplement());
            pstm.setString(6, address.getNeighborhood());
            pstm.setString(7, address.getCity());
            pstm.setString(8, address.getState());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (address.getCreateAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(address.getCreateAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            if (address.getUpdateAt() != null){
                pstm.setTimestamp(10, Timestamp.valueOf(address.getUpdateAt()));
            }else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            if (address.getDeleteAt() != null){
                pstm.setTimestamp(11, Timestamp.valueOf(address.getDeleteAt()));
            }else {
                pstm.setNull(11, Types.TIMESTAMP);
            }

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //  METODO DE ALTERACAO NO BANCO (UPDATE)
    public int update(Address address) {
        //COMANDO SQL
        String sql =
                "UPDATE address " +
                "SET  zipCode =?, street = ?, number = ?, " +
                        "complement = ?, neighborhood = ?, city = ?, state = ?,  " +
                        "updatedat = ?, deletedAt = ? " +
                "WHERE addressid = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, address.getZipCode());
            pstm.setString(2, address.getStreet());
            pstm.setString(3, address.getNumber());
            pstm.setString(4, address.getComplement());
            pstm.setString(5, address.getNeighborhood());
            pstm.setString(6, address.getCity());
            pstm.setString(7, address.getState());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (address.getUpdateAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(address.getUpdateAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            if (address.getDeleteAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(address.getDeleteAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            pstm.setInt(10, address.getAddressId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //METODO DE DELECAO DE REGISTRO NO BANCO
    public int delete(int idAdress) {
        //COMANDO SQL
        String sql =
                "DELETE FROM address " +
                "WHERE addressid = ?";

        try (
             //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql);
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELO PARÂMETRO
            pstm.setInt(1, idAdress);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DE REGISTRO NO BANCO VIA ZIPCODE E NUMBER
    public int deleteViaZipCodeAndNumber(String zipCode, int number) {
        //COMANDO SQL
        String sql =
                "DELETE FROM address " +
                "WHERE zipcode = ? AND number = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUIÇÃO DAS OCORRENCIAS PELO PARÂMETRO
            pstm.setString(1, zipCode);
            pstm.setInt(2, number);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO PELA CIDADE
    public List<Address> listByCity(String city) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE city LIKE ?";

        String emailIncident = "%"+ city +"%";

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
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO PELO NUMERO - LOCALIZAÇÃO
    public List<Address> listByNumber(int number) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE number = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql);
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS
            pstm.setInt(1, number);

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA CEP
    public List<Address> listByZipCode(String zipCode) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE zipcode LIKE ?";

        String zipCodeIncident = "%"+ zipCode +"%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, zipCodeIncident);

            try (//EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA COMPLEMENTO
    public List<Address> listByComplement(String complement) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE complement LIKE ?";

        String complementIncident = "%" + complement + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, complementIncident);

            try (//EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO
    public List<Address> list() {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address ";

        try (//ABRINDO CONEXÃO E PREPARANDO O COMANDO
             Connection connection = DataBaseConnection.getInstance().connection();
             PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (//EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCEÇÃO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<Address> mapResulSet(ResultSet resultSet) {
        List<Address> addresses = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()){
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");

                Address address = new Address(
                        resultSet.getInt("addressid"),
                        resultSet.getString("zipcode"),
                        resultSet.getString("street"),
                        resultSet.getString("number"),
                        resultSet.getString("complement"),
                        resultSet.getString("neighborhood"),
                        resultSet.getString("city"),
                        resultSet.getString("state"),

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
                //ADICIONANDO OBJETO NA LISTA
                addresses.add(address);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //RETORNO DOS RESULTADOS
        return addresses;
    }
}

