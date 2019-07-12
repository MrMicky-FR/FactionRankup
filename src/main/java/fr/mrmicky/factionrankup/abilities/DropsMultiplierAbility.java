package fr.mrmicky.factionrankup.abilities;

import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MrMicky
 */
public class DropsMultiplierAbility extends ChanceAbility {

    private final int min;
    private final int max;

    public DropsMultiplierAbility(String name, ConfigurationSection config) {
        this(name, config.getInt("chances", 100), config.getInt("min", 1), config.getInt("max", 1));
    }

    public DropsMultiplierAbility(String name, int chance, int min, int max) {
        super(name, chance);
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int nextRandomMultiplier() {
        if (min == max) {
            return min;
        }
        return ThreadLocalRandom.current().nextInt(min, max);
    }
}
