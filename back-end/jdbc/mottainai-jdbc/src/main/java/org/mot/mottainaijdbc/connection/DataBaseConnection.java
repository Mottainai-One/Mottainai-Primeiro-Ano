package org.mot.mottainaijdbc.connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class DataBaseConnection {
    //ATRIBUTOS
    private static DataBaseConnection instance;
    private Connection connection;

    //CONSTRUTOR
    private DataBaseConnection() {
        Dotenv dotenv = Dotenv.load();
        String url = dotenv.get("DB_URL");
        String user = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        //ABRIND CONEXÃO COM O BANCO A PARTIR DOS DADOS
        try {
            connection = DriverManager.getConnection(url, user, password);
        }catch (SQLException sqlE){
            throw new RuntimeException (sqlE);
        }
    }

    //METODO RESPONSAVEL POR ACIONAR A CONEXAO
    public static DataBaseConnection getInstance() {
        if (Objects.isNull(instance)){
            instance = new DataBaseConnection();
        }
        return instance;
    }

    //METODO RESPONSAVEL PELO RETORNO DA CONEXAO INSTANCIADA
    public Connection connection(){ //metodo responsavel pelo retorno da conexão
        return connection;
    }
}
