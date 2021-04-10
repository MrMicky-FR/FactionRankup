package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Level {

    private final List<Ability> abilities = new ArrayList<>();

    private final int level;
    private final double cost;
    private final int maxMembers;
    private final String name;
    private final Material type;
    private final int data;
    private final List<String> description;

    public Level(int level, double cost, int maxMembers, String name, Material type, int data, List<String> description) {
        this.level = level;
        this.cost = cost;
        this.maxMembers = maxMembers;
        this.name = name;
        this.type = type;
        this.data = data;
        this.description = description;
    }

    public int getLevel() {
        return level;
    }

    public double getCost() {
        return cost;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public String getName() {
        return name;
    }

    public Material getType() {
        return type;
    }

    public int getData() {
        return data;
    }

    public List<String> getDescription() {
        return description;
    }

    public List<Ability> getAbilities() {
        return abilities;
    }

    public static Level fromConfig(ConfigurationSection section, int i) {
        LevelManager levelManager = FactionRankup.getInstance().getLevelManager();

        int cost = section.getInt("cost");
        int maxMembers = section.getInt("max-members");
        String name = section.getString("name");
        Material type = Optional.ofNullable(Material.matchMaterial(section.getString("item.type"))).orElse(Material.STONE);
        int data = section.getInt("item.data");
        List<String> description = section.getStringList("item.description");

        Level level = new Level(i, cost, maxMembers, name, type, data, description);

        levelManager.createAbility(section.getConfigurationSection("ability")).ifPresent(level.getAbilities()::add);

        ConfigurationSection abilitiesSection = section.getConfigurationSection("abilities");

        if (abilitiesSection != null) {
            for (String s : abilitiesSection.getKeys(false)) {
                levelManager.createAbility(section.getConfigurationSection(s)).ifPresent(level.getAbilities()::add);
            }
        }

        return level;
    }
}
