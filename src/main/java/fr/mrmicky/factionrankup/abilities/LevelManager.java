package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.stream.Stream;

/**
 * @author MrMicky
 */
public class LevelManager {

    private final Map<String, BiFunction<String, ConfigurationSection, Ability>> abilities = new HashMap<>();
    private final List<Level> levels = new ArrayList<>();

    private final FactionRankup plugin;

    public LevelManager(FactionRankup plugin) {
        this.plugin = plugin;

        registerAbility("Command", (name, conf) -> new CommandAbility(name, conf.getString("command", "")));
        registerAbility("PotionEffect", PotionAbility::new);
        registerAbility("Fly", (name, conf) -> new Ability(name));
        registerAbility("ReduceFalls", ChanceAbility::new);
        registerAbility("SilkTouch", ChanceAbility::new);
        registerAbility("DoubleXP", ChanceAbility::new);
        registerAbility("InstantCrops", ChanceAbility::new);
        registerAbility("DropMultiplier", DropsMultiplierAbility::new);
    }

    public void loadLevels() {
        ConfigurationSection levelsConfig = plugin.getLevelsConfig().getConfigurationSection("levels");

        for (String key : levelsConfig.getKeys(false)) {
            int level = ChatUtils.parseInt(key);

            if (level < 0) {
                plugin.getLogger().warning("Invalid level: " + key);
                continue;
            }

            levels.add(Level.fromConfig(levelsConfig.getConfigurationSection(key), level));
        }
    }

    public List<Level> getLevels() {
        return levels;
    }

    public void registerAbility(String ability, BiFunction<String, ConfigurationSection, Ability> function) {
        abilities.put(ability, function);
    }

    public Optional<Ability> createAbility(ConfigurationSection config) {
        String name = config.getString("name");

        if (name != null && name.equalsIgnoreCase("MoreDrops")) {
            name = "DropMultiplier";
        }

        if (name == null || !abilities.containsKey(name)) {
            return Optional.empty();
        }

        return Optional.ofNullable(abilities.get(name).apply(name, config));
    }

    public Stream<Ability> getAbilitiesForLevel(int level) {
        if (level <= 0) {
            return Stream.empty();
        }

        List<Ability> levelAbilities = new ArrayList<>();

        levels.stream().filter(l -> l.getLevel() <= level).map(Level::getAbilities).forEach(levelAbilities::addAll);

        Collections.reverse(levelAbilities);

        return levelAbilities.stream();
    }

    public Stream<Ability> getAbilitiesForLevel(int level, String name) {
        return getAbilitiesForLevel(level).filter(ability -> ability.getName().equalsIgnoreCase(name));
    }

    public Level getLevel(int level) {
        return levels.get(level - 1);
    }

    public int getLevelCount() {
        return levels.size() + 1;
    }

}
