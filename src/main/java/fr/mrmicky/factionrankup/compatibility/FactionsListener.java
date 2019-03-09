package fr.mrmicky.factionrankup.compatibility;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class FactionsListener implements Listener {

    private FactionRankup plugin;

    public FactionsListener(Plugin plugin) {
        this.plugin = FactionRankup.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void handleJoin(Player p, IFaction faction, int playersSize, Cancellable event) {
        int level = plugin.getFactionLevel(faction);
        int max = level <= 0 ? plugin.getConfig().getInt("max-members-level-0") : plugin.getLevelsConfig().getInt("levels." + level + ".max-members");
        if (playersSize >= max) {
            p.sendMessage(plugin.getMessage("max-players")
                    .replace("%faction%", faction.getName())
                    .replace("%next_level%", String.valueOf(level + 1))
                    .replace("%maxplayers%", String.valueOf(max)));
            event.setCancelled(true);
        }
    }

    protected void handleDisband(IFaction faction) {
        faction.getPlayers().forEach(p -> p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType())));

        plugin.deleteFactionLevel(faction);
    }
}
