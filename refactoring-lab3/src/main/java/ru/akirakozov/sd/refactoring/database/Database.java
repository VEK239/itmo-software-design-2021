package ru.akirakozov.sd.refactoring.database;

import java.sql.*;

public class Database {
    private static final String name = System.getProperty("DB_URL");

    synchronized public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(name);
    }
}
