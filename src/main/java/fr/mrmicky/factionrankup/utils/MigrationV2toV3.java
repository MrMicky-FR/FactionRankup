package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.compatibility.Compatibility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class MigrationV2toV3 {

    public static void migrateV2toV3(Plugin plugin) {
        File invFile = new File(plugin.getDataFolder(), "Inventory.yml");

        if (!invFile.exists()) {
            return;
        }

        plugin.getLogger().warning("Migrating FactionRankup v2 to v3 !");

        try {
            File originalPluginDir = plugin.getDataFolder();
            File backupDir = new File(plugin.getDataFolder().getParentFile(), "FactionRankup-v2-backup");

            originalPluginDir.renameTo(backupDir);

            File backupData = new File(backupDir, "data.yml");

            if (backupData.exists()) {
                plugin.getDataFolder().mkdirs();

                copyFile(backupData, new File(plugin.getDataFolder(), "data.yml"));
            }
            plugin.getLogger().info("Migration to v3 done");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot migrate to v3", e);
        }

    }

    public static void migrateV3_1toV3_2(Plugin plugin) {
        File dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            return;
        }

        plugin.getLogger().warning("Migrating Data.yml from v3 to v3.2");

        try {
            File backupFile = new File(plugin.getDataFolder(), "data-backup.yml");

            dataFile.renameTo(backupFile);

            YamlConfiguration dataConfig = YamlConfiguration.loadConfiguration(backupFile);

            ConfigurationSection section = dataConfig.getConfigurationSection("Factions");

            if (section != null) {
                Map<String, String> nameToId = new HashMap<>();
                Map<String, Integer> factionsLevels = new HashMap<>();
                Map<String, Integer> unknownFactions = new HashMap<>();
                Compatibility.get().getAllFactions().forEach(f -> nameToId.put(f.getName(), f.getId()));

                for (String factionName : section.getKeys(false)) {
                    int level = section.getInt(factionName + ".Level");
                    String id = nameToId.get(factionName);

                    if (level <= 0) {
                        continue;
                    }

                    if (id == null || id.trim().isEmpty()) {
                        plugin.getLogger().warning("Cannot find faction with name '" + factionName);
                        unknownFactions.put(factionName, level);
                    } else {
                        factionsLevels.put(id, level);
                    }
                }

                if (!factionsLevels.isEmpty()) {
                    YamlConfiguration saveConfig = new YamlConfiguration();
                    ConfigurationSection save = saveConfig.createSection("factions");

                    factionsLevels.forEach((fac, lvl) -> save.set(fac + ".level", lvl));

                    saveConfig.save(new File(plugin.getDataFolder(), "storage.yml"));
                }

                if (!unknownFactions.isEmpty()) {
                    File f = new File(plugin.getDataFolder(), "invalid-factions.txt");

                    try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(f)))) {
                        writer.println("# Some factions was invalid during migration");
                        writer.println('#');

                        unknownFactions.forEach((fac, lvl) -> writer.println(fac + ':' + lvl));
                    }
                }

                plugin.getLogger().info("Migration done !");
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error during migration", e);
        }
    }

    private static void copyFile(File source, File target) throws IOException {
        try (InputStream in = new FileInputStream(source);
             OutputStream out = new FileOutputStream(target)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
        }
    }
}
