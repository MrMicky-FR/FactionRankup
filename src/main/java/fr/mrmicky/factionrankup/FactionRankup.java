package fr.mrmicky.factionrankup;

import fr.mrmicky.factionrankup.commands.CommandFactionrankup;
import fr.mrmicky.factionrankup.commands.CommandRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.FactionType;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.implementations.factions.MFactionsManager;
import fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid.FactionsUUIDManager;
import fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions.LegacyFactionsManager;
import fr.mrmicky.factionrankup.listeners.AbilitiesListener;
import fr.mrmicky.factionrankup.listeners.RankupListener;
import fr.mrmicky.factionrankup.utils.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class FactionRankup extends JavaPlugin {

    //public static final String USER_ID = "BETA-589";
    public static final String USER_ID = "%%__USER__%%";
    public static final String NONCE_ID = "%%__NONCE__%%";

    private static FactionRankup instance;
    public ConfigHandler.Configurations config;
    public ConfigHandler.Configurations levels;
    public ConfigHandler.Configurations messages;
    private FactionType factionType = FactionType.FACTIONS;
    private ConfigHandler.Configurations data;

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

        registerConfigurations();

        if (Compatibility.get() == null) {
            if (getServer().getPluginManager().getPlugin("Factions") != null) {
                try {
                    Class.forName("com.massivecraft.factions.FPlayer");
                    factionType = FactionType.FACTIONS_UUID;
                } catch (ClassNotFoundException ignored) {
                    // Not FactionsUUID
                }
            } else if (getServer().getPluginManager().getPlugin("LegacyFactions") != null) {
                factionType = FactionType.LEGACY_FACTIONS;
            } else {
                getLogger().severe("No Factions plugin founded, disabling plugin");
                getLogger().severe("Currently supported plugins: Factions, FactionsUUID, LegacyFaction");
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            switch (factionType) {
                case FACTIONS:
                    Compatibility.setFactionManager(new MFactionsManager());
                    break;
                case FACTIONS_UUID:
                    Compatibility.setFactionManager(new FactionsUUIDManager());
                    break;
                case LEGACY_FACTIONS:
                    Compatibility.setFactionManager(new LegacyFactionsManager());
                    break;
            }
        } else {
            factionType = FactionType.CUSTOM;
            getLogger().info("*** Using custom Faction ***");
        }

        if (Compatibility.isPluginFactionEnabled()) {
            start(c);
        } else {
            getLogger().warning("The plugin Factions is not enabled yet, delaying start...");

            new BukkitRunnable() {

                int i = 10;

                @Override
                public void run() {
                    if (Compatibility.isPluginFactionEnabled()) {
                        start(c);
                        cancel();
                    } else if (i-- >= 0) {
                        getLogger().severe("The plugin Factions is not enabled after 5 seconds, disabling :(");
                        cancel();
                        getServer().getPluginManager().disablePlugin(FactionRankup.this);
                    }
                }
            }.runTaskTimer(this, 10, 10);
        }
    }

    private void start(Checker c) {
        new AbilitiesTask(this);

        new RankupListener(this);
        new AbilitiesListener(this);

        new CommandRankup(this);
        new CommandFactionrankup(this);

        registerNewFactions();

        getLogger().info("Thank you " + c.getUsername() + "for purchasing FactionRankup :)");
        getLogger().info("The plugin has been successfully activated");
    }

    public FactionType getFactionType() {
        return factionType;
    }

    private void registerConfigurations() {
        try {
            ConfigHandler.registerConfig("data", "data.yml", this);
            ConfigHandler.registerConfig("config", "config.yml", this);

            ConfigHandler.registerConfig("levels", "levels.yml", this);
            ConfigHandler.registerConfig("messages", "messages.yml", this);

            ConfigHandler.loadAll();
            ConfigHandler.saveAll();

            data = ConfigHandler.getConfig("data");
            config = ConfigHandler.getConfig("config");

            levels = ConfigHandler.getConfig("levels");
            messages = ConfigHandler.getConfig("messages");
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Cannot load configurations files", e);
        }
    }

    private void registerNewFactions() {
        if (data.contains("Factions")) {
            return;
        }

        for (IFaction faction : Compatibility.get().getAllFactions()) {
            if (!data.contains(faction.getName())) {
                if (!faction.get().equals(Compatibility.get().getSafezone().get())
                        && !faction.get().equals(Compatibility.get().getWarzone().get())
                        && !faction.get().equals(Compatibility.get().getWilderness().get())) {
                    setFactionLevel(faction.getName(), 0);
                }
            }
        }
        ConfigHandler.save("data");
    }

    public void setFactionLevel(Player p, int level) {
        setFactionLevel(Compatibility.get().getFactionByPlayer(p), level);
    }

    public void setFactionLevel(IFaction faction, int level) {
        setFactionLevel(faction.getName(), level);
    }

    public void setFactionLevel(String faction, int level) {
        data.set("Factions." + faction + ".Level", level);
        ConfigHandler.save("data");
    }

    public void removeFactionLevel(String faction) {
        data.set("Factions." + faction, null);
        ConfigHandler.save("data");
    }

    public int getFactionLevel(Player p) {
        return getFactionLevel(Compatibility.get().getFactionByPlayer(p));
    }

    public int getFactionLevel(IFaction faction) {
        return getFactionLevel(faction.getName());
    }

    public int getFactionLevel(String faction) {
        return data.getInt("Factions." + faction + ".Level");
    }
}
