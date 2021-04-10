package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionAbility extends Ability {

    private final PotionEffectType effectType;
    private final int level;

    public PotionAbility(String name, ConfigurationSection config) {
        this(name, PotionEffectType.getByName(config.getString("effect")), config.getInt("level", 1));

        if (effectType == null) {
            FactionRankup.getInstance().getLogger().warning("Invalid potion type: " + config.getString("effect"));
        }
    }

    public PotionAbility(String name, PotionEffectType effectType, int level) {
        super(name);
        this.effectType = effectType;
        this.level = level;
    }

    public PotionEffectType getEffectType() {
        return effectType;
    }

    public int getLevel() {
        return level;
    }

    public int getEffectLevel() {
        return level - 1;
    }

    public PotionEffect createPotionEffect() {
        return new PotionEffect(effectType, 50000, getEffectLevel());
    }
}
