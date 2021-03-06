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

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        if (identifier.equals("level")) {
            return Integer.toString(plugin.getFactionLevel(player));
        }
        return null;
    }
}
