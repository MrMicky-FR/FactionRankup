package fr.mrmicky.factionrankup.placeholder;

import fr.mrmicky.factionrankup.FactionRankup;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PlaceHolderApiExpansion extends PlaceholderExpansion {

    private final FactionRankup plugin;

    public PlaceHolderApiExpansion(FactionRankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors());
    }

    @Override
    public String getIdentifier() {
        return "factionrankup";
    }

    @Override
    public String getPlugin() {
        return "FactionRankup";
    }

    /**
     * This is the version of this expansion
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier is found
     * and needs a value We specify the value identifier in this method
     */
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {
        if (p == null) {
            return "";
        }

        if (identifier.equals("level")) {
            return String.valueOf(plugin.getFactionLevel(p));
        }
        return null;
    }
}
