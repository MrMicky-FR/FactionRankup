package fr.mrmicky.factionrankup.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigHandler {

    private static List<Configurations> configs = new ArrayList<>();

    public static boolean unregisterConfig(String String) {
        return configs.remove(getConfig(String));
    }

    public static boolean registerConfig(String String1, String String2, JavaPlugin plugin) {
        File File = new File(plugin.getDataFolder(), String2);
        if (!File.exists()) {
            File.getParentFile().mkdirs();
            try {
                copy(plugin.getResource(String2), File);
            } catch (Exception localException) {
            }
        }
        Configurations Configurations1 = new Configurations(String1, File);
        for (Configurations Configurations2 : configs) {
            if (Configurations2.equals(Configurations1)) {
                return false;
            }
        }
        configs.add(Configurations1);
        return true;
    }

    public static Configurations getConfig(String paramString) {
        for (Configurations Configurations : configs) {
            if (Configurations.getConfigId().equalsIgnoreCase(paramString)) {
                return Configurations;
            }
        }
        return null;
    }

    public static boolean save(String paramString) {
        Configurations Configurations = getConfig(paramString);
        if (Configurations == null) {
            return false;
        }
        try {
            Configurations.save();
        } catch (Exception localException) {
            print("Error while saving config " + paramString);
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveAll() {
        try {
            for (Configurations Configurations : configs) {
                Configurations.save();
            }
        } catch (Exception localException) {
            print("Error while saving all configurations.");
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean load(String paramString) {
        Configurations Configurations = getConfig(paramString);
        if (Configurations == null) {
            return false;
        }
        try {
            Configurations.load();
        } catch (Exception localException) {
            print("Error while loading config: " + paramString);
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean loadAll() {
        try {
            for (Configurations Configurations : configs) {
                Configurations.load();
            }
        } catch (Exception localException) {
            print("Error while loading all configurations.");
            localException.printStackTrace();
            return false;
        }
        return true;
    }

    private static void print(String paramString) {
        System.out.println("Config: " + paramString);
    }

    private static void copy(InputStream InputStream, File File) {
        try {
            FileOutputStream FileOutputStream = new FileOutputStream(File);
            byte[] OfByte = new byte['`'];
            int i;
            while ((i = InputStream.read(OfByte)) > 0) {
                FileOutputStream.write(OfByte, 0, i);
            }
            FileOutputStream.close();
            InputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Configurations extends YamlConfiguration {
        private String id;
        private File file;

        private Configurations(String paramString, File File) {
            this.id = paramString;
            this.file = File;
        }

        public String getConfigId() {
            return this.id;
        }

        public File getFile() {
            return this.file;
        }

        public void save() {
            try {
                save(this.file);
            } catch (Exception e) {

            }
        }

        public void load() {
            try {
                load(this.file);
            } catch (Exception e) {

            }
        }

        public boolean equals(Configurations Configurations) {
            return Configurations.getConfigId().equalsIgnoreCase(this.id);
        }
    }
}
