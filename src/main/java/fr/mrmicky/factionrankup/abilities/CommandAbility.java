package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author MrMicky
 */
public class CommandAbility extends Ability {

    private final String command;
    private final boolean allPlayers;

    public CommandAbility(String name, ConfigurationSection config) {
        this(name, config.getString("command"), config.getBoolean("all-players"));
    }

    public CommandAbility(String name, String command, boolean allPlayers) {
        super(name);
        this.command = (command != null ? command : "");
        this.allPlayers = allPlayers;
    }

    public String getCommand() {
        return command;
    }

    public void dispatchCommand(IFaction faction, int level) {
        if (command == null) {
            return;
        }

        String cmd = command.replace("{faction}", faction.getName())
                .replace("{faction-id}", faction.getId())
                .replace("{level}", Integer.toString(level));

        FactionRankup.getInstance().getLogger().info("Running command: " + cmd);

        if (allPlayers) {
            faction.getPlayers().forEach(p -> dispatchServerCommand(cmd.replace("{player}", p.getName())));
        } else {
            dispatchServerCommand(cmd);
        }
    }

    private static void dispatchServerCommand(String command) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}
