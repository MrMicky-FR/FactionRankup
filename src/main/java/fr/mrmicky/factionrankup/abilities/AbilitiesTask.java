package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;

public class AbilitiesTask extends BukkitRunnable {

    private final FactionRankup plugin;

    public AbilitiesTask(FactionRankup plugin) {
        this.plugin = plugin;

        runTaskTimer(plugin, 5, 20);
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            int level = plugin.getFactionLevel(player);

            if (level <= 0) {
                continue;
            }

            Collection<PotionEffect> effects = player.getActivePotionEffects();

            plugin.getLevelManager().getAbilitiesForLevel(level)
                    .filter(ability -> ability.getClass() == PotionAbility.class)
                    .map(PotionAbility.class::cast)
                    .filter(a -> a.getEffectType() != null)
                    .filter(a -> !hasEffect(a.getEffectType(), a.getEffectLevel(), effects))
                    .forEach(a -> player.addPotionEffect(a.createPotionEffect(), true));
        }
    }

    private boolean hasEffect(PotionEffectType type, int level, Collection<PotionEffect> effects) {
        return effects.stream().filter(e -> e.getType().equals(type)).anyMatch(e -> e.getAmplifier() >= level);
    }
}
