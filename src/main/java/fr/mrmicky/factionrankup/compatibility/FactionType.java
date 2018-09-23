package fr.mrmicky.factionrankup.compatibility;

import org.bukkit.Bukkit;

public enum FactionType {

    FACTIONS, FACTIONS_UUID, FACTIONS_ONE, LEGACY_FACTIONS, CUSTOM;

    public String getPluginName() {
        return this == CUSTOM ? "?" : this != LEGACY_FACTIONS ? "Factions" : "LegacyFactions";
    }

    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}
