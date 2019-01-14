package fr.mrmicky.factionrankup;

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
import fr.mrmicky.factionrankup.utils.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class FactionRankup extends JavaPlugin {

    //public static final String USER_ID = "BETA-589";
    public static final String USER_ID = "%%__USER__%%";
    public static final String NONCE_ID = "%%__NONCE__%%";

    private static FactionRankup instance;

    private FactionType factionType = FactionType.FACTIONS;

    private ConfigWrapper levels;
    private ConfigWrapper messages;
    private ConfigWrapper data;

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

        Version.init(this);
        FastInv.init(this);

        MigrationV2toV3.migrate(this);

        saveDefaultConfig();
        levels = new ConfigWrapper(this, "levels.yml");
        messages = new ConfigWrapper(this, "messages.yml");
        data = new ConfigWrapper(this, "data.yml");

        if (Compatibility.get() == null) {
            if (getServer().getPluginManager().getPlugin("Factions") != null) {
                try {
                    Class.forName("de.erethon.factionsone.FactionsOneAPI");
                    factionType = FactionType.FACTIONS_ONE;
                } catch (ClassNotFoundException ignored) {
                    try {
                        Class.forName("com.massivecraft.factions.FPlayer");
                        factionType = FactionType.FACTIONS_UUID;
                    } catch (ClassNotFoundException ignored1) {
                    }
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

        new AbilitiesTask(this);

        getServer().getPluginManager().registerEvents(new RankupListener(this), this);
        getServer().getPluginManager().registerEvents(new AbilitiesListener(this), this);

        getCommand("frankup").setExecutor(new CommandRankup(this));
        getCommand("factionrankup").setExecutor(new CommandFactionrankup(this));

        getLogger().info("Thank you " + c.getUsername() + "for purchasing FactionRankup :)");
        getLogger().info("The plugin has been successfully activated");
    }

    public FactionType getFactionType() {
        return factionType;
    }

    public FileConfiguration getLevelsConfig() {
        return levels.getConfig();
    }

    public void reloadAllConfigs() {
        reloadConfig();
        messages.load();
        levels.load();
    }

    public String getMessage(String key) {
        return ChatUtils.color(messages.getConfig().getString(key).replace("%prefix%", messages.getConfig().getString("prefix")));
    }

    public void setFactionLevel(Player p, int level) {
        setFactionLevel(Compatibility.get().getFactionByPlayer(p), level);
    }

    public void setFactionLevel(IFaction faction, int level) {
        setFactionLevel(faction.getName(), level);
    }

    public void setFactionLevel(String faction, int level) {
        data.getConfig().set("Factions." + faction + ".Level", level);
        data.save();
    }

    public void removeFactionLevel(String faction) {
        data.getConfig().set("Factions." + faction, null);
        data.save();
    }

    public int getFactionLevel(Player p) {
        return getFactionLevel(Compatibility.get().getFactionByPlayer(p));
    }

    public int getFactionLevel(IFaction faction) {
        return getFactionLevel(faction.getName());
    }

    public int getFactionLevel(String faction) {
        return data.getConfig().getInt("Factions." + faction + ".Level");
    }
}
