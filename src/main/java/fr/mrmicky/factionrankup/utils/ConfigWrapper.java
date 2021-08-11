package fr.mrmicky.factionrankup.utils;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigWrapper {

    private final Plugin plugin;
    private final String name;

    private final File file;
    private FileConfiguration config;

    public ConfigWrapper(Plugin plugin, String name) {
        this.plugin = plugin;
        this.name = name;

        file = new File(plugin.getDataFolder(), name);

        if (!file.exists()) {
            plugin.saveResource(name, false);
        }

        load();
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);

        InputStream in = plugin.getResource(name);
        if (in != null) {
            config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
        }
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException ex) {
            plugin.getLogger().log(Level.SEVERE, "Could not save " + file, ex);
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }
}
