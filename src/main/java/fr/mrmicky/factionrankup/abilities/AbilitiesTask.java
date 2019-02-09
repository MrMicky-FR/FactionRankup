package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilitiesTask extends BukkitRunnable {

    private FactionRankup main;

    public AbilitiesTask(FactionRankup main) {
        this.main = main;

        runTaskTimer(main, 5, 20);
    }

    @Override
    public void run() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            int level = main.getFactionLevel(p);

            if (level <= 0) {
                continue;
            }

            main.getLevelManager().getAbilitiesForLevel(level)
                    .filter(ability -> ability.getClass() == PotionAbility.class)
                    .map(PotionAbility.class::cast)
                    .filter(a -> a.getEffectType() != null)
                    .forEach(ability -> p.addPotionEffect(ability.getPotionEffect(), true));
        }
    }
}
