package ru.geekbains.DZ;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseService {
    private DatabaseService() {
    }

    public static Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/test?serverTimezone=UTC", "root", "");
        } catch (Exception e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public static void rollback(Connection connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.rollback();
        } catch (SQLException e) {
            throw new RuntimeException("SWW", e);
        }
    }

    public static void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException("SWW", e);
        }
    }
}
