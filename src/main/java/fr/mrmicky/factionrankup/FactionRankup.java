package fr.mrmicky.factionrankup;

import fr.mrmicky.factionrankup.abilities.AbilitiesTask;
import fr.mrmicky.factionrankup.abilities.LevelManager;
import fr.mrmicky.factionrankup.commands.CommandFactionRankup;
import fr.mrmicky.factionrankup.commands.CommandRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.FactionType;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.implementations.factions.MFactionsManager;
import fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid.FactionsUUIDManager;
import fr.mrmicky.factionrankup.economy.VaultAdapter;
import fr.mrmicky.factionrankup.listeners.AbilitiesListener;
import fr.mrmicky.factionrankup.listeners.RankupListener;
import fr.mrmicky.factionrankup.placeholder.PlaceHolderApiExpansion;
import fr.mrmicky.factionrankup.storage.StorageManager;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import fr.mrmicky.factionrankup.utils.ConfigWrapper;
import fr.mrmicky.factionrankup.utils.FastReflection;
import fr.mrmicky.factionrankup.utils.Migration;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;
import java.util.logging.Level;

public class FactionRankup extends JavaPlugin {

    private static FactionRankup instance;

    private FactionType factionType;
    private StorageManager storageManager;
    private LevelManager levelManager;

    private ConfigWrapper levels;
    private ConfigWrapper messages;

    private VaultAdapter vaultAdapter;

    public static FactionRankup getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        if (Compatibility.get() == null) {
            if (getServer().getPluginManager().getPlugin("Factions") != null) {
                if (FastReflection.optionalClass("com.massivecraft.factions.FPlayer").isPresent()) {
                    factionType = FactionType.FACTIONS_UUID;
                } else {
                    factionType = FactionType.FACTIONS;
                }
            } else {
                getLogger().severe("No Factions plugin founded, disabling plugin");
                getLogger().severe("Currently supported plugins: Factions and FactionsUUID");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        } else {
            factionType = FactionType.CUSTOM;
            getLogger().info("*** Using custom Faction ***");
        }

        instance = this;

        FastInvManager.register(this);

        Migration.migrateV2toV3(this);

        saveDefaultConfig();
        levels = new ConfigWrapper(this, "levels.yml");
        messages = new ConfigWrapper(this, "messages.yml");

        levelManager = new LevelManager(this);
        levelManager.loadLevels();

        if (factionType == FactionType.CUSTOM || factionType.isPluginEnabled()) {
            start();
        } else {
            getLogger().warning("The plugin Factions is not enabled yet, delaying start...");

            getServer().getScheduler().runTask(this, () -> {
                getLogger().info("Trying to enable again...");
                if (factionType.isPluginEnabled()) {
                    start();
                } else {
                    getLogger().severe("The plugin Factions is not enabled after start, disabling...");
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

        if (Compatibility.get() != null) {
            Compatibility.get().disable();
        }
    }

    private void start() {
        if (getConfig().getBoolean("use-vault-money")) {
            VaultAdapter vault = new VaultAdapter();

            if (!vault.setupEconomy()) {
                getLogger().severe("Vault economy is enabled but Vault is not found, disabling...");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            getLogger().info("Vault support enabled");

            vaultAdapter = vault;
        }

        try {
            if (factionType == FactionType.FACTIONS) {
                Compatibility.setFactionManager(new MFactionsManager(this));
            } else if (factionType == FactionType.FACTIONS_UUID) {
                Compatibility.setFactionManager(new FactionsUUIDManager(this));
            }
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while enabling faction support", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        if (Compatibility.get() == null) {
            throw new IllegalStateException("Compatibility not loaded");
        }

        Migration.migrateV3_1toV3_2(this);

        try {
            storageManager = new StorageManager(this);
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "An error occurred while loading factions", e);
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new AbilitiesTask(this);

        getServer().getPluginManager().registerEvents(new RankupListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilitiesListener(this), this);

        getCommand("frankup").setExecutor(new CommandRankup(this));
        getCommand("factionrankup").setExecutor(new CommandFactionRankup(this));

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderApiExpansion(this).register();
        }

        getLogger().info("Using faction adapter " + factionType.getName());
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

    public VaultAdapter getVaultAdapter() {
        return vaultAdapter;
    }

    public boolean isUsingVault() {
        return vaultAdapter != null;
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
    public void setFactionLevel(Player player, int level) {
        Objects.requireNonNull(player, "player");
        setFactionLevel(Compatibility.get().getFactionByPlayer(player), level);
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

    public int getFactionLevel(Player player) {
        Objects.requireNonNull(player, "player");
        return getFactionLevel(Compatibility.get().getFactionByPlayer(player));
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
