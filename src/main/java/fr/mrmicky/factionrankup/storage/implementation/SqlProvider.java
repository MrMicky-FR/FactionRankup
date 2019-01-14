package fr.mrmicky.factionrankup.storage.implementation;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.storage.StorageManager;
import fr.mrmicky.factionrankup.storage.StorageProvider;
import org.bukkit.Bukkit;

import java.sql.*;
import java.util.logging.Level;

/**
 * @author MrMicky
 */
public class SqlProvider implements StorageProvider {

    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS factionrankup_factions (" +
                    "`faction_id` VARCHAR(16) NOT NULL," +
                    "`level` INT NOT NULL," +
                    "PRIMARY KEY (`faction_id`)" +
                    ")";
    private static final String SELECT_ALL = "SELECT * FROM factionrankup_factions WHERE `level` > 0";
    private static final String INSERT_FACTION = "INSERT INTO factionrankup_factions (`faction_id`, `level`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `level` = ?";
    private static final String REMOVE_FACTION = "DELETE FROM factionrankup_factions WHERE `faction_id` = ?";

    private FactionRankup plugin;
    private StorageManager storageManager;
    private DatabaseCredentials credentials;

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

        runAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement(SELECT_ALL)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String faction = rs.getString("faction_id");
                        int level = rs.getInt("level");

                        storageManager.getFactionLevels().put(faction, level);
                    }
                }
            }
        });
    }

    private void connect() throws SQLException {
        plugin.getLogger().info("Connecting to MySQL...");
        long start = System.currentTimeMillis();
        if (connection != null && !connection.isClosed()) {
            return;
        }

        connection = DriverManager.getConnection(credentials.getConnectionURL(), credentials.getUsername(), credentials.getPassword());
        plugin.getLogger().info("MySQL connected in " + (System.currentTimeMillis() - start) + " ms !");
    }

    @Override
    public void shutdown() {
        try {
            if (connection != null) {
                connection.close();
                connection = null;
                plugin.getLogger().info("MySQL disconnected");
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
            try (PreparedStatement ps = connection.prepareStatement(INSERT_FACTION)) {
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
            try (PreparedStatement ps = connection.prepareStatement(REMOVE_FACTION)) {
                ps.setString(1, factionId);

                ps.executeUpdate();
            }
        });
    }

    private void runAsync(RunnableSQL runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                runnable.run();
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "SQL error", e);
            }
        });
    }

    interface RunnableSQL {

        void run() throws SQLException;
    }
}
