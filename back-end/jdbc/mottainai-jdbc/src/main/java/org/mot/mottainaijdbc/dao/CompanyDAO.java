package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompanyDAO {

    //CONSTRUTOR
    public CompanyDAO() {
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> list() {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                    "FROM company";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByOfficialName(String officialName) {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                            "FROM company " +
                            "WHERE officialname LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  "%" + officialName + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByTradeName(String tradeName) {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                            "FROM company " +
                            "WHERE tradename LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  "%" + tradeName + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByCnpj(String cnpj) {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                            "FROM company " +
                            "WHERE cnpj LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  "%" + cnpj + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByEmail(String email) {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                            "FROM company " +
                            "WHERE email LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  "%" + email + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByPhone(String phone) {
        List<Company> companies;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                            "longitude, active, createdat, updatedat, deletedat " +
                            "FROM company " +
                            "WHERE phone LIKE ? ";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  "%" + phone + "%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public Long listCountStatus() {
        Long result = null;
        try {
            //COMANDO SQL
            String sql =
                    "SELECT COUNT(companyid) " +
                            "FROM company " +
                            "WHERE active = true";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

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

    //METODO DE INSERÇÃO NO BANCO
    public int insert(Company company) {
        try {
            //COMANDO SQL
            String sql =
                    "INSERT INTO company(companyid, officialname, tradename, cnpj, email, phone, latitude, longitude, active, createdat, updatedat, deletedat) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setInt(1, company.getCompanyId());
            pstm.setString(2, company.getOfficialName());
            pstm.setString(3, company.getTradeName());
            pstm.setString(4, company.getCnpj());
            pstm.setString(5, company.getEmail().trim());
            pstm.setString(6, company.getPhone());
            pstm.setDouble(7, company.getLatitude());
            pstm.setDouble(8, company.getLongitude());
            pstm.setBoolean(9, company.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (company.getCreatedAt() != null){
                pstm.setTimestamp(10, Timestamp.valueOf(company.getCreatedAt()));
            }else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            if (company.getUpdatedAt() != null){
                pstm.setTimestamp(11, Timestamp.valueOf(company.getUpdatedAt()));
            }else {
                pstm.setNull(11, Types.TIMESTAMP);
            }
            if (company.getDeletedAt() != null){
                pstm.setTimestamp(12, Timestamp.valueOf(company.getDeletedAt()));
            }else {
                pstm.setNull(12, Types.TIMESTAMP);
            }

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO NO BANCO
    public int update(Company company) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE company " +
                            "SET  officialname = ?, tradename = ?, cnpj = ?, email = ?, phone = ?, latitude = ?, " +
                            "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                            "WHERE companyid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, company.getOfficialName());
            pstm.setString(2, company.getTradeName());
            pstm.setString(3, company.getCnpj());
            pstm.setObject(4, company.getEmail());
            pstm.setString(5, company.getPhone());
            pstm.setDouble(6, company.getLatitude());
            pstm.setDouble(7, company.getLongitude());
            pstm.setBoolean(8, company.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (company.getUpdatedAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(company.getUpdatedAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            if (company.getDeletedAt() != null){
                pstm.setTimestamp(10, Timestamp.valueOf(company.getDeletedAt()));
            }else {
                pstm.setNull(10, Types.TIMESTAMP);
            }
            pstm.setInt(11, company.getCompanyId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO NO BANCO
    public int updateViaCnpj(Company company) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE company " +
                            "SET  officialname = ?, tradename = ?, email = ?, phone = ?, latitude = ?, " +
                            "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                            "WHERE cnpj = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, company.getOfficialName());
            pstm.setString(2, company.getTradeName());
            pstm.setObject(3, company.getEmail());
            pstm.setString(4, company.getPhone());
            pstm.setDouble(5, company.getLatitude());
            pstm.setDouble(6, company.getLongitude());
            pstm.setBoolean(7, company.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (company.getUpdatedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(company.getUpdatedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            if (company.getDeletedAt() != null){
                pstm.setTimestamp(9, Timestamp.valueOf(company.getDeletedAt()));
            }else {
                pstm.setNull(9, Types.TIMESTAMP);
            }
            pstm.setString(10, company.getCnpj());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE ATUALIZAÇÃO NO BANCO
    public int updateViaEmail(Company company) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE company " +
                            "SET  officialname = ?, tradename = ?, phone = ?, latitude = ?, " +
                            "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                            "WHERE email = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, company.getOfficialName());
            pstm.setString(2, company.getTradeName());
            pstm.setString(3, company.getPhone());
            pstm.setDouble(4, company.getLatitude());
            pstm.setDouble(5, company.getLongitude());
            pstm.setBoolean(6, company.isActive());

            //VALIDAÇÃO DOS DADOS(LocalDate/LocalDateTime) PARA REGISTRO NO BANCO
            if (company.getUpdatedAt() != null){
                pstm.setTimestamp(7, Timestamp.valueOf(company.getUpdatedAt()));
            }else {
                pstm.setNull(7, Types.TIMESTAMP);
            }
            if (company.getDeletedAt() != null){
                pstm.setTimestamp(8, Timestamp.valueOf(company.getDeletedAt()));
            }else {
                pstm.setNull(8, Types.TIMESTAMP);
            }
            pstm.setString(9, company.getEmail());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE APAGAR OS REGISTROS NO BANCO
    public int delete(int companyId) {
        try {
            //COMANDO SQL
            String sql = "DELETE FROM company WHERE companyid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELO PARÂMETRO
            pstm.setInt(1, companyId);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE APAGAR OS REGISTROS NO BANCO VIA CNPJ
    public int deleteViaCnpj(String cnpj) {
        try {
            //COMANDO SQL
            String sql = "DELETE FROM company WHERE cnpj = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELO PARÂMETRO
            pstm.setString(1, cnpj);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO DE APAGAR OS REGISTROS NO BANCO VIA LOCALIZAÇÃO - LATITUDE E LONGITUDE
    public int deleteViaLatitudeAndLongitude(double longitude, double latitude) {
        try {
            //COMANDO SQL
            String sql = "DELETE FROM company WHERE longitude = ? AND latitude = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELO PARÂMETRO
            pstm.setDouble(1, longitude);
            pstm.setDouble(2, latitude);

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<Company> mapResultSet(ResultSet resultSet) {
        List<Company> companies = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Timestamp createdAt = resultSet.getTimestamp("createdat");
                Timestamp updatedAt = resultSet.getTimestamp("updatedat");
                Timestamp deletedAt = resultSet.getTimestamp("deletedat");

                Company company = new Company(
                        resultSet.getInt("companyid"),
                        resultSet.getString("officialname"),
                        resultSet.getString("tradename"),
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
                                ? createdAt.toLocalDateTime()
                                : null,
                        deletedAt != null
                                ? createdAt.toLocalDateTime()
                                : null
                );
                //ADICIONANDO OBJETO NA LISTA
                companies.add(company);
            }

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        //RETORNO DA LISTA
        return companies;
    }
}

