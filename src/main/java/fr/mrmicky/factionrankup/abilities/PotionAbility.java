package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * @author MrMicky
 */
public class PotionAbility extends Ability {

    private PotionEffectType effectType;
    private int level;

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

    public PotionEffect getPotionEffect() {
        return new PotionEffect(effectType, 50000, level - 1);
    }
}
