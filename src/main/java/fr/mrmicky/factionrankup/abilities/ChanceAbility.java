package fr.mrmicky.factionrankup.abilities;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Random;

/**
 * @author MrMicky
 */
public class ChanceAbility extends Ability {

    private static final Random RANDOM = new Random();

    private int chance;

    public ChanceAbility(String name, ConfigurationSection config) {
        this(name, config.getInt("chances", 100));
    }

    public ChanceAbility(String name, int chance) {
        super(name);
        this.chance = chance;
    }

    public boolean isActive() {
        return chance > RANDOM.nextInt(100);
    }
}