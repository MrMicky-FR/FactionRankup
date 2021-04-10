package fr.mrmicky.factionrankup.storage.implementation;

import org.bukkit.configuration.ConfigurationSection;

public class DatabaseCredentials {

    private final String address;
    private final String database;
    private final String username;
    private final String password;
    private final int port;

    public DatabaseCredentials(String address, String database, String username, String password, int port) {
        this.address = address;
        this.database = database;
        this.username = username;
        this.password = password;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public String getConnectionURL() {
        return "jdbc:mysql://" + address + ':' + port + '/' + database;
    }

    public static DatabaseCredentials fromConfig(ConfigurationSection config) {
        String address = config.getString("address");
        String database = config.getString("database");
        String username = config.getString("username");
        String password = config.getString("password");
        int port = config.getInt("port");

        return new DatabaseCredentials(address, database, username, password, port);
    }
}
