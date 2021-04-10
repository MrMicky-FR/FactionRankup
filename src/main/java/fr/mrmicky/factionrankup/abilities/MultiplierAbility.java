package fr.mrmicky.factionrankup.abilities;

import org.bukkit.configuration.ConfigurationSection;

import java.util.concurrent.ThreadLocalRandom;

public class MultiplierAbility extends ChanceAbility {

    private final int min;
    private final int max;

    public MultiplierAbility(String name, ConfigurationSection config) {
        super(name, config);
        min = config.getInt("min", 2);
        max = config.getInt("max", 2);
    }

    public MultiplierAbility(String name, int chance, int min, int max) {
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
