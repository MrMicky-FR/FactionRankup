package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilitiesTask extends BukkitRunnable {

    private FactionRankup plugin;

    public AbilitiesTask(FactionRankup plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 5, 20);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            int level = plugin.getFactionLevel(p);

            if (level <= 0) {
                continue;
            }

            plugin.getLevelManager().getAbilitiesForLevel(level)
                    .filter(ability -> ability.getClass() == PotionAbility.class)
                    .map(PotionAbility.class::cast)
                    .filter(a -> a.getEffectType() != null)
                    .forEach(ability -> p.addPotionEffect(ability.getPotionEffect(), true));
        }
    }
}
