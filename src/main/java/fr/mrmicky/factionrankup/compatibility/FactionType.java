package fr.mrmicky.factionrankup.compatibility;

import org.bukkit.Bukkit;

public enum FactionType {

    FACTIONS("Faction"),
    FACTIONS_UUID("FactionsUUID"),
    CUSTOM("Custom");

    private final String name;

    FactionType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getPluginName() {
        return this != CUSTOM ? "Factions" : "?";
    }

    public boolean isPluginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled(getPluginName());
    }
}
