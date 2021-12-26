package ru.akirakozov.sd.refactoring.servlet;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtils {
    private static final String DATA_BASE_NAME = System.getProperty("DB_URL");

    static void clear() throws SQLException {
        try (Statement statement = DriverManager.getConnection(DATA_BASE_NAME).createStatement()) {
            String sqlQuery = "DELETE FROM PRODUCT";
            statement.executeUpdate(sqlQuery);
        }
    }

    static void add(String name, int price) throws SQLException {
        try (Statement statement = DriverManager.getConnection(DATA_BASE_NAME).createStatement()) {
            String sqlQuery = "INSERT INTO PRODUCT (NAME, PRICE) VALUES (\"" + name + "\"," + price + ")";
            statement.executeUpdate(sqlQuery);
        }
    }
}
