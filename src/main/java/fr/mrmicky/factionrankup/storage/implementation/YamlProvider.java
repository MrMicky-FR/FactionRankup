package fr.mrmicky.factionrankup.storage.implementation;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.storage.StorageManager;
import fr.mrmicky.factionrankup.storage.StorageProvider;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

/**
 * @author MrMicky
 */

/* Data structure:

factions:
  1: # id
    level: 1
*/
public class YamlProvider implements StorageProvider {

    private final FactionRankup plugin;
    private final StorageManager storageManager;

    private final File storageFile;
    private FileConfiguration storageConfig;

    private ConfigurationSection factionsSection;

    public YamlProvider(FactionRankup plugin, StorageManager storageManager) {
        this.plugin = plugin;
        this.storageManager = storageManager;

        storageFile = new File(plugin.getDataFolder(), "storage.yml");
    }

    @Override
    public void init() {
        if (!storageFile.exists()) {
            storageConfig = new YamlConfiguration();

            factionsSection = storageConfig.createSection("factions");

            return;
        }

        storageConfig = YamlConfiguration.loadConfiguration(storageFile);

        factionsSection = storageConfig.getConfigurationSection("factions");

        for (String key : factionsSection.getKeys(false)) {
            storageManager.getFactionLevels().put(key, factionsSection.getInt(key + ".level", 0));
        }
    }

    @Override
    public void shutdown() {
    }

    @Override
    public void setFactionLevel(String factionId, int level) {
        factionsSection.set(factionId + ".level", level);

        save();
    }

    @Override
    public void deleteFaction(String factionId) {
        factionsSection.set(factionId, null);

        save();
    }

    private void save() {
        try {
            storageConfig.save(storageFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot save storage to file", e);
        }
    }
}
