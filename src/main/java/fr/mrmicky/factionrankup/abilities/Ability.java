package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Ability {

    private static final Random RANDOM = new Random();

    private static Set<Ability> abilities = new HashSet<>();

    public static final Ability FLY = new Ability("Fly");
    public static final Ability MORE_DROPS = new Ability("MoreDrops");
    public static final Ability REDUCE_FALL = new Ability("ReduceFalls");
    public static final Ability SILKTOUCH = new Ability("SilkTouch");
    public static final Ability DOUBLE_XP = new Ability("DoubleXP");

    private String name;

    public Ability(String name) {
        this.name = name;

        abilities.add(this);
    }

    public boolean isActive(Player p) {
        int level = FactionRankup.getInstance().getFactionLevel(p);

        for (int i = level; i > 0; i--) {
            ConfigurationSection conf = FactionRankup.getInstance().getLevelsConfig().getConfigurationSection("levels." + i + ".ability");
            if (conf != null && conf.getString("type").equalsIgnoreCase("custom")
                    && conf.getString("name").equalsIgnoreCase(name)) {
                return !conf.contains("chances") || RANDOM.nextInt(100) < conf.getInt("chances");
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public static Set<Ability> getAbilities() {
        return new HashSet<>(abilities);
    }

    public static Ability getByName(String name) {
        for (Ability ability : abilities) {
            if (ability.getName().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
    }
}
