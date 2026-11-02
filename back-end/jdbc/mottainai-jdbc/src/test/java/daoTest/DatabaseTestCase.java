package daoTest;

import org.junit.jupiter.api.BeforeEach;
import org.mot.mottainaijdbc.connection.DataBaseConnection;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** Resets the disposable CI database so each DAO test has independent fixtures. */
abstract class DatabaseTestCase {
    @BeforeEach
    void resetDatabase() throws IOException, SQLException {
        try (InputStream fixture = getClass().getResourceAsStream("/dao-test-fixtures.sql")) {
            if (fixture == null) {
                throw new IllegalStateException("DAO test fixture resource was not found");
            }

            String sql = new String(fixture.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection connection = DataBaseConnection.getInstance().connection();
                 Statement statement = connection.createStatement()) {
                for (String command : sql.split(";")) {
                    if (!command.isBlank()) {
                        statement.execute(command);
                    }
                }
            }
        }
    }
}
