package org.mot.mottainaijdbc.connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBaseConnection {
    private static DataBaseConnection instance;
    private final String url;
    private final String user;
    private final String password;

    private DataBaseConnection() {
        // Environment variables take precedence; local .env remains optional.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        url = required(dotenv, "DB_URL");
        user = required(dotenv, "DB_USER");
        password = required(dotenv, "DB_PASSWORD");
    }

    private static String required(Dotenv dotenv, String name) {
        String value = dotenv.get(name);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Configuração obrigatória ausente: " + name);
        }
        return value;
    }

    public static synchronized DataBaseConnection getInstance() {
        if (instance == null) {
            instance = new DataBaseConnection();
        }
        return instance;
    }

    // Each caller owns its connection and must close it with try-with-resources.
    public Connection connection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível conectar ao banco de dados.", exception);
        }
    }
}
