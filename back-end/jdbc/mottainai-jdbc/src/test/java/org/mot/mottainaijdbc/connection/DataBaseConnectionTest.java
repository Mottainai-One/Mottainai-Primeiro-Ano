package org.mot.mottainaijdbc.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataBaseConnectionTest {
    @Test
    void connectsAndExecutesQueryAgainstPostgres() throws Exception {
        DataBaseConnection database = DataBaseConnection.getInstance();
        Connection connection = database.connection();
        assertSame(database, DataBaseConnection.getInstance());
        assertTrue(connection.isValid(5));
        assertEquals("PostgreSQL", connection.getMetaData().getDatabaseProductName());
        try (Statement statement = connection.createStatement()) {
            statement.setQueryTimeout(5);
            try (ResultSet result = statement.executeQuery("SELECT 1")) {
                assertTrue(result.next());
                assertEquals(1, result.getInt(1));
            }
        }
    }
}
