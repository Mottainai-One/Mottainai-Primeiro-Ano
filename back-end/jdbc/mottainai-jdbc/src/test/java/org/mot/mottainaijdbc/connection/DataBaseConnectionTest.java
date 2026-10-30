package org.mot.mottainaijdbc.connection;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataBaseConnectionTest {
    @Test
    void connectsAndExecutesQueryAgainstPostgres() throws Exception {
        DataBaseConnection database = DataBaseConnection.getInstance();
        assertSame(database, DataBaseConnection.getInstance());
        try (Connection connection = database.connection(); Statement statement = connection.createStatement()) {
            assertTrue(connection.isValid(5));
            assertEquals("PostgreSQL", connection.getMetaData().getDatabaseProductName());
            statement.setQueryTimeout(5);
            try (ResultSet result = statement.executeQuery("SELECT 1")) {
                assertTrue(result.next());
                assertEquals(1, result.getInt(1));
            }
        }
    }

    @Test
    void closingOneConnectionDoesNotInvalidateAnother() throws Exception {
        DataBaseConnection database = DataBaseConnection.getInstance();
        try (Connection first = database.connection()) {
            try (Connection second = database.connection()) {
                assertNotSame(first, second);
                assertTrue(second.isValid(5));
            }
            assertTrue(first.isValid(5));
        }
    }
}
