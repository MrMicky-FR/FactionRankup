package fr.mrmicky.factionrankup.compatibility;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class FactionsListener implements Listener {

    private FactionRankup main;

    public FactionsListener(Plugin plugin) {
        main = FactionRankup.getInstance();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    protected void handleJoin(Player p, IFaction faction, int playersSize, Cancellable event) {
        int level = main.getFactionLevel(faction);
        int max = level <= 0 ? main.getConfig().getInt("max-members-level-0") : main.getLevelsConfig().getInt("levels." + level + ".max-members");
        if (playersSize >= max) {
            p.sendMessage(main.getMessage("max-players")
                    .replace("%faction%", faction.getName())
                    .replace("%next_level%", String.valueOf(level + 1))
                    .replace("%maxplayers%", String.valueOf(max)));
            event.setCancelled(true);
        }
    }

    protected void handleDisband(IFaction faction) {
        int factionLevel = main.getFactionLevel(faction);
        List<PotionEffectType> effects = new ArrayList<>();

        for (int i = factionLevel; i > 1; i--) {
            ConfigurationSection conf = main.getLevelsConfig().getConfigurationSection("levels." + i + ".ability");
            if (conf != null && conf.getString("type").equalsIgnoreCase("effect")) {
                effects.add(PotionEffectType.getByName(conf.getString("effect").toUpperCase()));
            }
        }

        faction.getPlayers().forEach(p -> effects.forEach(p::removePotionEffect));

        main.deleteFactionLevel(faction);
    }
}
