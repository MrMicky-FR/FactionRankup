package fr.mrmicky.factionrankup.utils;

import org.bukkit.plugin.Plugin;

import java.io.*;
import java.util.logging.Level;

public class MigrationV2toV3 {

    public static void migrate(Plugin plugin) {
        try {
            File invFile = new File(plugin.getDataFolder(), "Inventory.yml");

            if (!invFile.exists()) {
                return;
            }

            plugin.getLogger().warning("Migrating FactionRankup v2 to v3 !");

            File folderDir = plugin.getDataFolder();
            File backupDir = new File(plugin.getDataFolder().getParentFile(), "FactionRankup-v2-backup");

            folderDir.renameTo(backupDir);

            File backupData = new File(backupDir, "data.yml");
            File fileData = new File(plugin.getDataFolder(), "data.yml");

            plugin.getDataFolder().mkdirs();

            try (InputStream in = new FileInputStream(backupData);
                 OutputStream out = new FileOutputStream(fileData)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }

            plugin.getLogger().info("Migration to v3 done");
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Cannot migrate to v3", e);
        }
    }
}
