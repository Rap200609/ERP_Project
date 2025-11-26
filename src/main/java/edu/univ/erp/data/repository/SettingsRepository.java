package edu.univ.erp.data.repository;

import edu.univ.erp.data.DatabaseConfig;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Optional;

public class SettingsRepository {
    private final DataSource mainDataSource;

    public SettingsRepository() {
        this(DatabaseConfig.getMainDataSource());
    }

    public SettingsRepository(DataSource mainDataSource) {
        this.mainDataSource = mainDataSource;
    }

    public Optional<String> getSettingValue(String key) throws Exception {
        String sql = "SELECT setting_value FROM settings WHERE setting_key=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, key);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.ofNullable(rs.getString("setting_value"));
                }
            }
        }
        return Optional.empty();
    }

    public void updateSettingValue(String key, String value) throws Exception {
        String sql = "UPDATE settings SET setting_value=? WHERE setting_key=?";
        try (Connection conn = mainDataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, value);
            stmt.setString(2, key);
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                try (PreparedStatement insertStmt = conn.prepareStatement(
                        "INSERT INTO settings (setting_key, setting_value) VALUES (?, ?)") ) {
                    insertStmt.setString(1, key);
                    insertStmt.setString(2, value);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
}
