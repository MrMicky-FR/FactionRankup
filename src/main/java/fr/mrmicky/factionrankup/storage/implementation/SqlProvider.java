package fr.mrmicky.factionrankup.storage.implementation;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.storage.StorageManager;
import fr.mrmicky.factionrankup.storage.StorageProvider;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SqlProvider implements StorageProvider {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS factionrankup_factions (" +
                    "`id` INT UNSIGNED NOT NULL AUTO_INCREMENT, " +
                    "`faction_id` VARCHAR(48) NOT NULL UNIQUE," +
                    "`level` INT UNSIGNED NOT NULL," +
                    "PRIMARY KEY (`id`)" +
                    ")";
    private static final String SELECT_ALL = "SELECT * FROM `factionrankup_factions`";
    private static final String INSERT_FACTION = "INSERT INTO `factionrankup_factions` (`faction_id`, `level`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `level` = ?";
    private static final String REMOVE_FACTION = "DELETE FROM `factionrankup_factions` WHERE `faction_id` = ?";

    private final FactionRankup plugin;
    private final StorageManager storageManager;
    private final DatabaseCredentials credentials;

    private Connection connection;

    public SqlProvider(FactionRankup plugin, StorageManager storageManager, DatabaseCredentials credentials) {
        this.plugin = plugin;
        this.storageManager = storageManager;
        this.credentials = credentials;
    }

    @Override
    public void init() throws Exception {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Could not find MySQL drivers");
        }

        connect();

        try (Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
        }

        loadFactionsLevels();

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            try {
                loadFactionsLevels();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "A database error occurred while loading levels", e);
            }
        }, 12000, 12000); // 10 minutes
    }

    private void connect() throws SQLException {
        plugin.getLogger().info("Connecting to MySQL...");

        long startTime = System.currentTimeMillis();
        if (connection != null && !connection.isClosed()) {
            return;
        }

        connection = DriverManager.getConnection(credentials.getConnectionURL(), credentials.getUsername(), credentials.getPassword());
        plugin.getLogger().info("Connected to MySQL connected in " + (System.currentTimeMillis() - startTime) + " ms");
    }

    @Override
    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                plugin.getLogger().info("Disconnected from MySQL database");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "MySQL disconnect error", e);
        }
    }

    private Connection getConnection() throws SQLException {
        if (connection != null && !connection.isClosed() && connection.isValid(3)) {
            return connection;
        }

        connect();

        return connection;
    }

    @Override
    public void setFactionLevel(String factionId, int level) {
        runAsync(() -> {
            try (PreparedStatement ps = getConnection().prepareStatement(INSERT_FACTION)) {
                ps.setString(1, factionId);
                ps.setInt(2, level);
                ps.setInt(3, level);

                ps.executeUpdate();
            }
        });
    }

    @Override
    public void deleteFaction(String factionId) {
        runAsync(() -> {
            try (PreparedStatement ps = getConnection().prepareStatement(REMOVE_FACTION)) {
                ps.setString(1, factionId);

                ps.executeUpdate();
            }
        });
    }

    private void loadFactionsLevels() throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL)) {
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String faction = rs.getString("faction_id");
                    int level = rs.getInt("level");

                    storageManager.getFactionLevels().put(faction, level);
                }
            }
        }
    }

    private void runAsync(SQLRunnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "A database error occurred", e);
            }
        });
    }

    interface SQLRunnable {

        void run() throws SQLException;
    }
}
