package org.mot.mottainaijdbc.dao;

import org.mot.mottainaijdbc.connection.DataBaseConnection;
import org.mot.mottainaijdbc.model.Address;
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

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int update(Administration administration) {

        //COMANDO SQL
        String sql =
                "UPDATE administration " +
                "SET email = ?, passwordhash = ? " +
                "WHERE administrationid = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql);
        ) {
            if (administration.getId() < 1) {
                return 0;
            }

            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
                pstm.setString(1, administration.getEmail().trim());
                pstm.setString(2, administration.getPasswordHash().trim());
                pstm.setInt(3, administration.getId());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE ATUALIZACAO DOS REGISTROS NO BANCO
    public int updateViaEmail(Administration administration) {

        //COMANDO SQL
        String sql =
                "UPDATE administration " +
                "SET passwordhash = ? " +
                "WHERE email = ?";
        try (
                //ABRINDO CONEXÃO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            if (administration.getEmail().isBlank()) {
                return 0;
            }

            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
                pstm.setString(1, administration.getPasswordHash().trim());
                pstm.setString(2, administration.getEmail().trim());

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE INSERCAO DOS REGISTROS NO BANCO
    public int insert(Administration administration) {

        //COMANDO SQL
        String sql =
                "INSERT INTO administration(email, passwordhash) " +
                "VALUES (?,?)";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            //SUBSTITUICAO DAS OCORRENCIAS PELOS DADOS DO OBJETO
            pstm.setString(1, administration.getEmail().trim());
            pstm.setString(2, administration.getPasswordHash().trim());

            //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
            return pstm.executeUpdate();

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO
    public int delete(int id) {
        //COMANDO SQL
        String sql =
                "DELETE FROM administration " +
                "WHERE administrationid = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            if (id > 0) {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS PARAMETROS
                pstm.setInt(1, id);

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA EXECUCAO
                return pstm.executeUpdate();
            }

            else {
                return 0;
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
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
        ){
            if (email.isBlank()) {
                return 0;
            }
            else {
                //SUBSTITUICAO DAS OCORRENCIAS PELOS PARAMETROS
                pstm.setString(1, email);

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE DELECAO DOS REGISTROS NO BANCO DE MODO DIVERSICADO - PODE ALTERAR VIA PASSWORDHASH
    public int deleteViaPasswordHash(String passWordHash) {
        //COMANDO SQL
        String sql =
                "DELETE FROM administration " +
                "WHERE passwordhash = ?";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            if (passWordHash.isBlank()) {
                return 0;
            }

            else {
                //SUBSTITUICAO DA OCORRENCIA PELO PARÂMETRO
                pstm.setString(1, passWordHash);

                //EXECUTANDO O COMANDO E RETORNANDO O INTEIRO DA ALTERACAO VIA .executeUpdate();
                return pstm.executeUpdate();
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO
    public List<Administration> list() {
        List<Administration> administrations;

        //COMANDO SQL
        String sql =
                "SELECT administrationid, email, passwordhash " +
                "FROM administration" ;

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {
            try (
                    //EXECUTANDO O COMANDO
                 ResultSet resultSet = pstm.executeQuery()
            ) {
                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                administrations = mapResulSet(resultSet);
            }

            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return administrations;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA ID DA ADMINISTRACAO
    public List<Administration> listByAdministrationId(int id) {
        List<Administration> administrations;

        //COMANDO SQL
        String sql =
                "SELECT administrationid, email, passwordhash " +
                "FROM administration " +
                "WHERE administrationid = ?";
        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            if (id > 0) {
                pstm.setInt(1, id);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    administrations = mapResulSet(resultSet);
                }
            }

            else {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }
            //EXCECAO
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        //RETORNO DA LISTA
        return administrations;
    }


    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA EMAIL
    public List<Administration> listByEmail(String email) {
        List<Administration> administrations;

        //COMANDO SQL
        String sql =
                "SELECT administrationid, email, passwordhash " +
                "FROM administration " +
                "WHERE email LIKE ?";

        String emailIncident = "%"+email+"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            if (email.isBlank()) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }
            else {
                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, emailIncident);

                //EXECUTANDO O COMANDO
                ResultSet resultSet = pstm.executeQuery();

                //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                administrations = mapResulSet(resultSet);
            }
            //EXCECAO
        } catch (SQLException sqlE) {
            throw new RuntimeException(sqlE);
        }
        //RETORNO DA LISTA
        return administrations;
    }

    //METODO DE SELECAO DOS REGISTROS NO BANCO VIA SENHA
    public List<Administration> listByPasswordHash(String passwordHash) {
        List<Administration> administrations;

        //COMANDO SQL
        String sql =
                "SELECT administrationid, email, passwordhash " +
                "FROM administration " +
                "WHERE passwordhash = ?";

        String passWordHashIncident = "%"+passwordHash+"%";

        try (
                //ABRINDO CONEXAO E PREPARANDO O COMANDO
                Connection connection = DataBaseConnection.getInstance().connection();
                PreparedStatement pstm = connection.prepareStatement(sql)
        ) {

            if (passwordHash.isBlank()) {
                //RETORNO TUDO CASO O PARAMETRO ESTEJA ERRADO
                return list();
            }

            else {
                //SUBSTITUINDO A OCORRENCIA PELO PARAMETRO COM % PARA PROCURAR OS DADOS QUE TEM O CONTEUDO
                pstm.setString(1, passWordHashIncident);

                try (
                        //EXECUTANDO O COMANDO
                        ResultSet resultSet = pstm.executeQuery()
                ) {
                    //CHAMANDO O METODO PARA ATRIBUICAO DA LISTA
                    administrations = mapResulSet(resultSet);
                }
            }
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
        //RETORNO DA LISTA COM OS RESULTADOS
        return administrationList;
    }
}

