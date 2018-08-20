package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;

public enum Version {

    V1_7_1, V1_7_2, V1_7_3, V1_7_R4, V1_8_R1, V1_8_R2, V1_8_R3, V1_9_R1, V1_9_R2, V1_10_R1, V1_11_R1, V1_12_R1, V1_13_R1;

    private static String versionPackage;
    private static Version version;

    public boolean isVersion() {
        return this == version;
    }

    public boolean isVersionOrHigher() {
        return version.ordinal() >= ordinal();
    }

    public boolean isVersionOrLower() {
        return version.ordinal() <= ordinal();
    }

    public static void init(FactionRankup main) {
        try {
            versionPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            version = Version.valueOf(versionPackage.toUpperCase());
        } catch (Exception e) {
            main.getLogger().severe("Your server version (" + versionPackage + ") is not supported. The plugin should work but somes bugs can occures");
        }
    }

    public static Version getVersion() {
        return version;
    }

    public static String getVersionPackage() {
        return versionPackage;
    }
}