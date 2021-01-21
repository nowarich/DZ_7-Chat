package ru.geekbains.DZ;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;

public class UserRepository {
    public void update(User user) {
        Objects.requireNonNull(user, "User cannot be null");
        Connection connection = null;
        try {
            connection = DatabaseService.connect();
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement("UPDATE users SET name = ? WHERE id = ?");

            statement.setString(1, user.getName());
            statement.setLong(2, user.getId());

            statement.executeUpdate();
            connection.commit();
        } catch (Exception e) {
            DatabaseService.rollback(connection);
            throw new RuntimeException("SWW", e);
        } finally {
            DatabaseService.close(connection);
        }
    }
}
