package org.mot.mottainaijdbc.connection;

import io.github.cdimascio.dotenv.Dotenv;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DataBaseConnection {
    private static DataBaseConnection instance;
    private final Connection connection;

    private DataBaseConnection() {
        // Environment variables take precedence; local .env remains optional.
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        String url = required(dotenv, "DB_URL");
        String user = required(dotenv, "DB_USER");
        String password = required(dotenv, "DB_PASSWORD");
        try {
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException exception) {
            throw new IllegalStateException("Não foi possível conectar ao banco de dados.", exception);
        }
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

    // The legacy API exposes one shared connection; callers must not close it.
    public Connection connection() {
        return connection;
    }
}
