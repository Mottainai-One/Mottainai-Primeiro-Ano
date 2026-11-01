package org.mot.mottainaijdbc.connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DataBaseConnection {
    //ATRIBUTOS
    private static DataBaseConnection instance;
    private final String url;
    private final String user;
    private final String password;

    //CONSTRUTOR
    private DataBaseConnection() {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        this.url = getConfig("DB_URL", dotenv);
        this.user = getConfig("DB_USER", dotenv);
        this.password = getConfig("DB_PASSWORD", dotenv);
    }

        //ABRIND CONEXÃO COM O BANCO A PARTIR DOS DADOS
        try {
            connection = DriverManager.getConnection(url, user, password);
        }catch (SQLException sqlE){
            throw new RuntimeException (sqlE);
        }
        return value;
    }

    //METODO RESPONSAVEL POR ACIONAR A CONEXAO
    public static DataBaseConnection getInstance() {
        if (Objects.isNull(instance)){
            instance = new DataBaseConnection();
        }
        return instance;
    }

    //METODO RESPONSAVEL PELO RETORNO DA CONEXAO INSTANCIADA
    public Connection connection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException sqlE) {
            throw new RuntimeException("Erro ao se conectar com o banco de dados ",sqlE);
        }
    }
}
