package fr.mrmicky.factionrankup.storage;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.storage.implementation.DatabaseCredentials;
import fr.mrmicky.factionrankup.storage.implementation.SqlProvider;
import fr.mrmicky.factionrankup.storage.implementation.YamlProvider;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public class StorageManager {

    private final Map<String, Integer> factionLevels = new HashMap<>();
    private final StorageProvider provider;

    public StorageManager(FactionRankup plugin) {
        ConfigurationSection sqlSection = plugin.getConfig().getConfigurationSection("database");

        if (sqlSection.getBoolean("enabled")) {
            DatabaseCredentials credentials = DatabaseCredentials.fromConfig(sqlSection);

            provider = new SqlProvider(plugin, this, credentials);
        } else {
            provider = new YamlProvider(plugin, this);
        }

        try {
            provider.init();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setFactionLevel(String factionId, int level) {
        if (level <= 0) {
            deleteFaction(factionId);
            return;
        }

        factionLevels.put(factionId, level);

        provider.setFactionLevel(factionId, level);
    }

    public void deleteFaction(String factionId) {
        factionLevels.remove(factionId);

        provider.deleteFaction(factionId);

    }

    public StorageProvider getProvider() {
        return provider;
    }

    public Map<String, Integer> getFactionLevels() {
        return factionLevels;
    }
}
