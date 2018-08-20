package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class AbilitiesTask extends BukkitRunnable {

    private FactionRankup main;

    public AbilitiesTask(FactionRankup main) {
        this.main = main;
        runTaskTimer(main, 20, 60);
    }

    @Override
    public void run() {
        for (World w : Bukkit.getWorlds()) {
            for (Player p : w.getPlayers()) {
                if (Compatibility.get().hasFaction(p)) {
                    int level = main.getFactionLevel(p);
                    for (int i = level; i > 1; i--) {
                        ConfigurationSection conf = main.levels.getConfigurationSection("levels." + i + ".ability");
                        if (conf != null && conf.getString("type").equalsIgnoreCase("effect")) {
                            PotionEffectType effect = PotionEffectType.getByName(conf.getString("effect").toUpperCase());
                            p.addPotionEffect(new PotionEffect(effect, 50000, conf.getInt("level") - 1), true);
                        }
                    }
                }
            }
        }
    }
}
