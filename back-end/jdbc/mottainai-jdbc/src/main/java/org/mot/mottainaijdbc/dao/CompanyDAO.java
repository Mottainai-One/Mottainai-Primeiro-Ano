package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.dto.CompanyStoreCountDTO;
import org.mot.mottainaijdbc.dto.CompanyUserAccountCountDTO;
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

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ){
            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            companies = mapResultSet(resultSet);

            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companies;
    }

    //METODO DE LISTAGEM DOS REGISTROS CORRELACIONADA COM STORE
    public List<CompanyStoreCountDTO> listCountStoreByCompany() {
        List<CompanyStoreCountDTO> companyStoreCountDTOS = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT c.companyid, c.tradename AS company, COUNT(s.storeid) AS totalstore " +
                "FROM company c " +
                "LEFT JOIN store s " +
                "ON s.companyid = c.companyid " +
                "GROUP BY c.companyid, c.tradename\n" +
                "ORDER BY totalstore";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery()
        ) {

            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                CompanyStoreCountDTO responseDTO = new CompanyStoreCountDTO(
                        resultSet.getInt("companyid"),
                        resultSet.getString("company"),
                        resultSet.getInt("totalstore")
                );
                //INSERINDO OBJETO NA LISTA
                companyStoreCountDTOS.add(responseDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return companyStoreCountDTOS;
    }

    //METODO DE LISTAGEM DOS REGISTROS CORRELCIONADA COM STORE E USERACCOUNT
    public List<CompanyUserAccountCountDTO> listCountUsersByCompany() {
        List<CompanyUserAccountCountDTO> countDTOS = new ArrayList<>();

        //COMANDO SQL
        String sql =
                "SELECT c.companyid, c.tradename AS company,COUNT(ua.userid) AS totalusers " +
                "FROM company c " +
                "LEFT JOIN store s " +
                "ON s.CompanyId = c.CompanyId " +
                "LEFT JOIN employee e " +
                "ON e.storeid = s.storeid " +
                "LEFT JOIN useraccount ua " +
                "ON ua.employeeid = e.employeeid " +
                "GROUP BY c.companyid, c.tradename " +
                "ORDER BY totalusers DESC";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                  Connection connection = DataBaseConnection.getInstance().connection();
                  PreparedStatement pstm = connection.prepareStatement(sql);

                  //EXECUTANDO O COMANDO
                  ResultSet resultSet = pstm.executeQuery()
        ) {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                CompanyUserAccountCountDTO companyUserAccountCountDTO = new CompanyUserAccountCountDTO(
                        resultSet.getInt("companyid"),
                        resultSet.getString("company"),
                        resultSet.getInt("totalusers")
                );
                //INSERINDO OBJETO NA LISTA
                countDTOS.add(companyUserAccountCountDTO);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return countDTOS;
    }

    //METODO DE LISTAGEM DOS REGISTROS
    public List<Company> listByOfficialName(String officialName) {
        List<Company> companies;

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company " +
                "WHERE officialname LIKE ? ";

        String officialNameIncident = "%" + officialName + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1,  officialNameIncident);

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                companies = mapResultSet(resultSet);
            }

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

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company " +
                "WHERE tradename LIKE ? ";

        String tradeNameIncident = "%" + tradeName + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, tradeNameIncident );

            try (
                    //EXECUTANDO O COMANDO
                    ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                companies = mapResultSet(resultSet);
            }
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

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company " +
                "WHERE cnpj LIKE ? ";

        String cnpjIncident = "%" + cnpj + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, cnpjIncident );

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                companies = mapResultSet(resultSet);
            }

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

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company " +
                "WHERE email LIKE ? ";

        String emailIncident = "%" + email + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, emailIncident );

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                companies = mapResultSet(resultSet);
            }

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

        //COMANDO SQL
        String sql =
                "SELECT companyid, officialname, tradename, cnpj, email, phone, latitude, " +
                        "longitude, active, createdat, updatedat, deletedat " +
                "FROM company " +
                "WHERE phone LIKE ? ";

        String phoneIncident = "%" + phone + "%";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, phoneIncident );

            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
                companies = mapResultSet(resultSet);
            }

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

        //COMANDO SQL
        String sql =
                "SELECT COUNT(companyid) " +
                "FROM company " +
                "WHERE active = true";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                while (resultSet.next()) {
                    result = resultSet.getLong(1); //pega o resultado numerico da primeira coluna
                }
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

        //COMANDO SQL
        String sql =
                "INSERT INTO company(companyid, officialname, tradename, cnpj, email, phone, " +
                        "latitude, longitude, active, createdat, updatedat, deletedat) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

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

        //COMANDO SQL
        String sql =
                "UPDATE company " +
                "SET  officialname = ?, tradename = ?, cnpj = ?, email = ?, phone = ?, latitude = ?, " +
                        "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                "WHERE companyid = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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

        //COMANDO SQL
        String sql =
                "UPDATE company " +
                "SET  officialname = ?, tradename = ?, email = ?, phone = ?, latitude = ?, " +
                        "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                "WHERE cnpj = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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

        //COMANDO SQL
        String sql =
                "UPDATE company " +
                "SET  officialname = ?, tradename = ?, phone = ?, latitude = ?, " +
                        "longitude = ?, active = ?, updatedat = ?, deletedat = ? " +
                "WHERE email = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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

        //COMANDO SQL
        String sql =
                "DELETE FROM company " +
                "WHERE companyid = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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
        //COMANDO SQL
        String sql =
                "DELETE FROM company " +
                "WHERE cnpj = ?";

        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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
        //COMANDO SQL
        String sql =
                "DELETE FROM company " +
                "WHERE longitude = ? AND latitude = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
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
                                ? updatedAt.toLocalDateTime()
                                : null,
                        deletedAt != null
                                ? deletedAt.toLocalDateTime()
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

