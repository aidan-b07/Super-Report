package dev.aidanbooth.superreport.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

// Reading columns that can be null
final class Rows {

    private Rows() {
    }

    static UUID uuid(ResultSet row, String column) throws SQLException {
        String value = row.getString(column);
        return value == null ? null : UUID.fromString(value);
    }

    static Long nullableLong(ResultSet row, String column) throws SQLException {
        long value = row.getLong(column);
        return row.wasNull() ? null : value;
    }

    static String text(UUID uuid) {
        return uuid == null ? null : uuid.toString();
    }
}
