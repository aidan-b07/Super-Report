package dev.aidanbooth.superreport.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class Database implements AutoCloseable {

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet row) throws SQLException;
    }

    @FunctionalInterface
    public interface SqlWork<T> {
        T run(Connection connection) throws SQLException;
    }

    private static final int VALIDATION_TIMEOUT_SECONDS = 2;

    private final String url;
    private final String username;
    private final String password;
    private Connection connection;

    private Database(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static Database sqlite(File file) {
        loadDriver("org.sqlite.JDBC");
        return new Database("jdbc:sqlite:" + file.getAbsolutePath(), null, null);
    }

    public static Database mysql(String host, int port, String database, String username, String password) {
        // Connector/J 8 renamed the driver class, older servers still ship the old one
        if (!loadDriver("com.mysql.cj.jdbc.Driver")) {
            loadDriver("com.mysql.jdbc.Driver");
        }
        return new Database("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    private static boolean loadDriver(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public synchronized void connect() throws SQLException {
        connection();
        Migrations.run(this);
    }

    private Connection connection() throws SQLException {
        if (connection == null || connection.isClosed() || !isAlive(connection)) {
            closeQuietly();
            connection = username == null
                    ? DriverManager.getConnection(url)
                    : DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

    // SQLite is a local file and never times out. 1.8's bundled sqlite-jdbc also predates JDBC 4 and has no isValid()
    private boolean isAlive(Connection conn) throws SQLException {
        return username == null || conn.isValid(VALIDATION_TIMEOUT_SECONDS);
    }

    public synchronized int update(String sql, Object... params) throws SQLException {
        return update(connection(), sql, params);
    }

    public synchronized <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        return query(connection(), sql, mapper, params);
    }

    public synchronized <T> Optional<T> queryFirst(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        List<T> rows = query(connection(), sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    public synchronized <T> T transaction(SqlWork<T> work) throws SQLException {
        Connection conn = connection();
        boolean previousAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            T result = work.run(conn);
            conn.commit();
            return result;
        } catch (SQLException | RuntimeException e) {
            conn.rollback();
            throw e;
        } finally {
            conn.setAutoCommit(previousAutoCommit);
        }
    }

    synchronized void execute(String sql) throws SQLException {
        try (Statement statement = connection().createStatement()) {
            statement.execute(sql);
        }
    }

    public static <T> List<T> query(Connection conn, String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        try (PreparedStatement statement = prepare(conn, sql, params);
             ResultSet rows = statement.executeQuery()) {
            List<T> results = new ArrayList<>();
            while (rows.next()) {
                results.add(mapper.map(rows));
            }
            return results;
        }
    }

    public static int update(Connection conn, String sql, Object... params) throws SQLException {
        try (PreparedStatement statement = prepare(conn, sql, params)) {
            return statement.executeUpdate();
        }
    }

    private static PreparedStatement prepare(Connection conn, String sql, Object... params) throws SQLException {
        PreparedStatement statement = conn.prepareStatement(sql);
        try {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            return statement;
        } catch (SQLException e) {
            statement.close();
            throw e;
        }
    }

    @Override
    public synchronized void close() {
        closeQuietly();
    }

    private void closeQuietly() {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (SQLException ignored) {
        }
        connection = null;
    }
}
