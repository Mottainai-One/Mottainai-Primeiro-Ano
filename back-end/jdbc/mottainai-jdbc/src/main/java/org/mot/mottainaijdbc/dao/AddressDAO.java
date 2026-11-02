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

    //METODO DE INSERCAO NO BANCO
    public int insert(Address address) {

        //COMANDO SQL
        String sql =
                "INSERT INTO address(addressid, zipcode, street, number, complement, neighborhood, city, state, createdat, updatedat, deletedat) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUIÇAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, address.getAddressId());
            pstm.setString(2, address.getZipCode().trim());
            pstm.setString(3, address.getStreet().trim());
            pstm.setString(4, address.getNumber().trim());
            pstm.setString(5, address.getComplement().trim());
            pstm.setString(6, address.getNeighborhood().trim());
            pstm.setString(7, address.getCity().trim());
            pstm.setString(8, address.getState().trim());

            //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
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

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
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
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //VALIDACAO DO PARAMETRO
            if (address.getAddressId() < 1) {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }
            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
                pstm.setString(1, address.getZipCode().trim());
                pstm.setString(2, address.getStreet().trim());
                pstm.setString(3, address.getNumber().trim());
                pstm.setString(4, address.getComplement().trim());
                pstm.setString(5, address.getNeighborhood().trim());
                pstm.setString(6, address.getCity().trim());
                pstm.setString(7, address.getState().trim());

                //VALIDACAO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
                if (address.getUpdateAt() != null) {
                    pstm.setTimestamp(8, Timestamp.valueOf(address.getUpdateAt()));
                } else {
                    pstm.setNull(8, Types.TIMESTAMP);
                }
                if (address.getDeleteAt() != null) {
                    pstm.setTimestamp(9, Timestamp.valueOf(address.getDeleteAt()));
                } else {
                    pstm.setNull(9, Types.TIMESTAMP);
                }
                pstm.setInt(10, address.getAddressId());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }

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
                 //ABRINDO CONEXAO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql);
        ) {

            //VALIDACAO DO PARAMETRO
            if (idAdress > 0) {
                //SUBSTITUICAO DA OCORRENCIA PELO PARAMETRO
                pstm.setInt(1, idAdress);

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();

            } else {
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE DELECAO DE REGISTRO NO BANCO VIA ZIPCODE E NUMBER
    public int deleteViaZipCodeAndNumber(String zipCode, String number) {
        //COMANDO SQL
        String sql =
                "DELETE FROM address " +
                "WHERE zipcode = ? AND number = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if ((zipCode.isBlank() || zipCode == null) || (number.isBlank() || number == null)){
                //NAO FACO NENHUMA ALTERACAO CASO O PARAMETRO ESTEJA ERRADO
                return 0;
            }

            else {
                //SUBSTITUICAO DA OCORRENCIA PELO PARAMETRO
                pstm.setString(1, zipCode.trim());
                pstm.setString(2, number.trim());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO PELA CIDADE
    public List<Address> listByCity(String city) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE city LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (city.isBlank() || city == null){
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }
            else {

                String cityIncident = "%"+ city.trim() +"%";

                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, cityIncident);

                try (
                        //EXECUTANDO O COMANDO
                     ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO PELO NUMERO - LOCALIZACAO
    public List<Address> listByNumber(String number) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE number = ?";

        try (
                //ABRINDO CONECAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
        ) {

            //VALIDACAO DO PARAMETRO
            if (number.isBlank() || number == null) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();

            } else {
                //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS
                pstm.setString(1, number.trim());

                try (
                        //EXECUTANDO O COMANDO
                     ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO PELA CIDADE
    public List<Address> listByState(String state) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE state LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (state.isBlank() || state == null){
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();

            } else {
                String stateIncident = "%"+ state.trim() +"%";

                //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, stateIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA CEP
    public List<Address> listByZipCode(String zipCode) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE zipcode LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (zipCode.isBlank() || zipCode == null) {

                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();

            } else {
                String zipCodeIncident = "%"+ zipCode.trim() +"%";

                //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, zipCodeIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA COMPLEMENTO
    public List<Address> listByComplement(String complement) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE complement LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //VALIDACAO DO PARAMETRO
            if (complement.isBlank() || complement == null) {
                // RETORNO TUDO NADA CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                String complementIncident = "%" + complement.trim() + "%";

                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, complementIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<Address> list() {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address ";

        try (   //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                addresses = mapResulSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA ID DO ENDERECO
    public List<Address> listByAddressId(int id) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE addressid = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (id < 1){
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                //SUBSTITUICAO DA OCORRENCIA PELO PARAMETRO
                pstm.setInt(1, id);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<Address> listByStreet(String street) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE street  LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //VALIDACAO DO PARAMETRO
            if (street.isBlank() || street == null) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                String streetIncident = "%"+ street.trim() +"%";

                //SUBSTITUICAO DA OCORRENCIA PELO PARAMETRO
                pstm.setString(1, streetIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return addresses;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<Address> listByNeighborhood(String neighborhood) {
        List<Address> addresses;

        //COMANDO SQL
        String sql =
                "SELECT addressid, zipcode, street, number, complement, " +
                        "neighborhood, city, state, createdat, updatedat, deletedat " +
                "FROM address " +
                "WHERE neighborhood LIKE ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                 Connection connection = DataBaseConnection.getInstance().connection();
                 PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //VALIDACAO DO PARAMETRO
            if (neighborhood.isBlank() || neighborhood == null) {

                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                String neighborhoodIncident = "%"+ neighborhood.trim() +"%";

                //SUBSTITUICAO DA OCORRENCIA PELO PARAMETRO
                pstm.setString(1, neighborhoodIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    addresses = mapResulSet(resultSet);
                }
            }
            //EXCECAO
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

                        //VALIDACAO DOS DADOS DO BANCO (DATE/TIMESTAMP) PARA INSTANCIA DO OBJETO
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

