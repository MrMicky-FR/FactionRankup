package fr.mrmicky.factionrankup;

import fr.mrmicky.factionrankup.abilities.AbilitiesTask;
import fr.mrmicky.factionrankup.abilities.LevelManager;
import fr.mrmicky.factionrankup.commands.CommandFactionrankup;
import fr.mrmicky.factionrankup.commands.CommandRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.FactionType;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.implementations.factions.MFactionsManager;
import fr.mrmicky.factionrankup.compatibility.implementations.factionsone.FactionsOneManager;
import fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid.FactionsUUIDManager;
import fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions.LegacyFactionsManager;
import fr.mrmicky.factionrankup.listeners.AbilitiesListener;
import fr.mrmicky.factionrankup.listeners.RankupListener;
import fr.mrmicky.factionrankup.storage.StorageManager;
import fr.mrmicky.factionrankup.utils.*;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class FactionRankup extends JavaPlugin {

    //public static final String USER_ID = "BETA-589";
    public static final String USER_ID = "%%__USER__%%";
    public static final String NONCE_ID = "%%__NONCE__%%";

    private static FactionRankup instance;

    private FactionType factionType = FactionType.FACTIONS;
    private StorageManager storageManager;
    private LevelManager levelManager;

    private ConfigWrapper levels;
    private ConfigWrapper messages;

    public static FactionRankup getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        Checker c = new Checker(this);
        if (!c.isValid()) {
            return;
        }

        instance = this;

        FastInvManager.register(this);

        Migration.migrateV2toV3(this);

        saveDefaultConfig();
        levels = new ConfigWrapper(this, "levels.yml");
        messages = new ConfigWrapper(this, "messages.yml");

        levelManager = new LevelManager(this);
        levelManager.loadLevels();

        if (Compatibility.get() == null) {
            if (getServer().getPluginManager().getPlugin("Factions") != null) {
                if (FastReflection.optionalClass("de.erethon.factionsone.FactionsOneAPI").isPresent()) {
                    factionType = FactionType.FACTIONS_ONE;
                } else if (FastReflection.optionalClass("com.massivecraft.factions.FPlayer").isPresent()) {
                    factionType = FactionType.FACTIONS_UUID;
                }
            } else if (getServer().getPluginManager().getPlugin("LegacyFactions") != null) {
                factionType = FactionType.LEGACY_FACTIONS;
            } else {
                getLogger().severe("No Factions plugin founded, disabling plugin");
                getLogger().severe("Currently supported plugins: Factions, FactionsUUID, FactionsOne and LegacyFaction");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            factionType = FactionType.CUSTOM;
            getLogger().info("*** Using custom Faction ***");
        }

        if (factionType == FactionType.CUSTOM || factionType.isPluginEnabled()) {
            start(c);
        } else {
            getLogger().warning("The plugin Factions is not enabled yet, delaying start...");

            getServer().getScheduler().runTask(this, () -> {
                getLogger().info("Trying to enable again...");
                if (factionType.isPluginEnabled()) {
                    start(c);
                } else {
                    getLogger().severe("The plugin Factions is not enabled after start, disabling :(");
                    getServer().getPluginManager().disablePlugin(this);
                }
            });
        }
    }

    @Override
    public void onDisable() {
        if (storageManager != null && storageManager.getProvider() != null) {
            storageManager.getProvider().shutdown();
        }
    }

    private void start(Checker c) {
        switch (factionType) {
            case FACTIONS:
                Compatibility.setFactionManager(new MFactionsManager());
                break;
            case FACTIONS_UUID:
                Compatibility.setFactionManager(new FactionsUUIDManager());
                break;
            case FACTIONS_ONE:
                Compatibility.setFactionManager(new FactionsOneManager());
                break;
            case LEGACY_FACTIONS:
                Compatibility.setFactionManager(new LegacyFactionsManager());
                break;
        }

        Migration.migrateV3_1toV3_2(this);

        new AbilitiesTask(this);

        storageManager = new StorageManager(this);

        getServer().getPluginManager().registerEvents(new RankupListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilitiesListener(this), this);

        getCommand("frankup").setExecutor(new CommandRankup(this));
        getCommand("factionrankup").setExecutor(new CommandFactionrankup(this));

        getLogger().info("Thank you " + c.getUsername() + "for purchasing FactionRankup :)");
    }

    public FactionType getFactionType() {
        return factionType;
    }

    public FileConfiguration getLevelsConfig() {
        return levels.getConfig();
    }

    public LevelManager getLevelManager() {
        return levelManager;
    }

    public void reloadAllConfigs() {
        reloadConfig();
        messages.load();
        levels.load();
    }

    public String getMessage(String key) {
        return ChatUtils.color(messages.getConfig().getString(key).replace("%prefix%", messages.getConfig().getString("prefix")));
    }

    /*
     * Level management
     */
    public void setFactionLevel(Player p, int level) {
        Objects.requireNonNull(p, "player");
        setFactionLevel(Compatibility.get().getFactionByPlayer(p), level);
    }

    public void setFactionLevel(IFaction faction, int level) {
        if (faction != null) {
            setFactionLevelById(faction.getId(), level);
        }
    }

    public void setFactionLevelById(String factionId, int level) {
        storageManager.setFactionLevel(factionId, level);
    }

    public void deleteFactionLevel(IFaction faction) {
        storageManager.deleteFaction(faction.getId());
    }

    public int getFactionLevel(Player p) {
        Objects.requireNonNull(p, "player");
        return getFactionLevel(Compatibility.get().getFactionByPlayer(p));
    }

    public int getFactionLevel(IFaction faction) {
        return faction != null ? getFactionLevelById(faction.getId()) : 0;
    }

    public int getFactionLevelById(String factionId) {
        return storageManager.getFactionLevels().getOrDefault(factionId, 0);
    }

    @Deprecated
    public void setFactionLevel(String factionName, int level) {
        setFactionLevel(Compatibility.get().getFactionByName(factionName), level);
    }

    @Deprecated
    public int getFactionLevel(String factionName) {
        return getFactionLevel(Compatibility.get().getFactionByName(factionName));
    }
}
