package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.Administration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdministrationDAO {

    //CONSTRUTOR
    public AdministrationDAO() {
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int update(Administration administration) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE administration " +
                            "SET email = ?, passwordhash = ? " +
                            "WHERE administrationid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, administration.getEmail());
            pstm.setString(2, administration.getPasswordHash());
            pstm.setInt(3, administration.getId());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE ATUALIZAÇÃO DOS REGISTROS NO BANCO
    public int updateViaEmail(Administration administration) {
        try {
            //COMANDO SQL
            String sql =
                    "UPDATE administration " +
                            "SET passwordhash = ? " +
                            "WHERE email = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, administration.getPasswordHash());
            pstm.setString(2, administration.getEmail());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE INSERÇÃO DOS REGISTROS NO BANCO
    public int insert(Administration administration) {
        try {
            //COMANDO SQL
            String sql =
                    "INSERT INTO administration(email, passwordhash) " +
                            "VALUES (?,?)";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, administration.getEmail());
            pstm.setString(2, administration.getPasswordHash());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERAÇÃO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE DELEÇÃO DOS REGISTROS NO BANCO
    public int delete(int id) {
        try {
            //COMANDO SQL
            String sql = "DELETE FROM administration WHERE administrationid = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUIÇÃO DAS OCORRENCIAS PELOS PARÂMETROS
            pstm.setInt(1,id);

            //EXECUTANDO O COMANDO
            int alteration = pstm.executeUpdate();

            //RETORNO BOOLEANO DA ALTERAÇÃO
            return alteration;

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE DELEÇÃO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA EMAIL
    public int deleteViaEmail(String email) {
        try {
            //COMANDO SQL
            String sql = "DELETE FROM administration WHERE email = ?";

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
            String sql = "DELETE FROM administration WHERE passwordhash = ?";

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

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO
    public List<Administration> list() {
        List<Administration> administrations;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT administrationid, email, passwordhash " +
                            "FROM administration" ;

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            administrations = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return administrations;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA EMAIL
    public List<Administration> listByEmail(String email) {
        List<Administration> administrations;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT administrationid, email, passwordhash " +
                            "FROM administration " +
                            "WHERE email LIKE ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+email+"%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            administrations = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return administrations;
    }

    //METODO DE SELEÇÃO DOS REGISTROS NO BANCO VIA SENHA
    public List<Administration> listByPasswordHash(String passwordHash) {
        List<Administration> administrations;

        try {
            //COMANDO SQL
            String sql =
                    "SELECT administrationid, email, passwordhash " +
                            "FROM administration " +
                            "WHERE passwordhash = ?";

            //ABRINDO CONEXÃO E PREPARANDO O COMANDO
            Connection connection = DataBaseConnection.getInstance().connection();
            PreparedStatement pstm = connection.prepareStatement(sql);

            //SUBSTITUINDO A OCORRENCIA PELOS PARAMETROS COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
            pstm.setString(1, "%"+passwordHash+"%");

            //EXECUTANDO O COMANDO
            ResultSet resultSet = pstm.executeQuery();

            //CHAMANDO O METODO PARA ATRIBUIÇÃO DA LISTA
            administrations = mapResulSet(resultSet);

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return administrations;
    }

    //METODO PRIVADO QUE RETORNA A LISTA PARA O METODO LIST() - CLEAN CODE
    private List<Administration> mapResulSet(ResultSet resultSet) {
        List<Administration> administrationList = new ArrayList<>();

        try {
            //INSTANCIANDO O OBJETO A PARTIR DOS DADOS ARMAZANADOS NAS COLUNAS DO BANCO
            while (resultSet.next()) {
                Administration administration = new Administration(
                        resultSet.getInt("administrationid"),
                        resultSet.getString("email"),
                        resultSet.getString("passwordhash")
                );
                //ADICIONANDO OBJETO NA LISTA
                administrationList.add(administration);
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA COM OS REUSLTADOS
        return administrationList;
    }
}

