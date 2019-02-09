package fr.mrmicky.factionrankup.abilities;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

/**
 * @author MrMicky
 */
public class CommandAbility extends Ability {

    private String command;

    public CommandAbility(String name, ConfigurationSection config) {
        this(name, config.getString("command", ""));
    }

    public CommandAbility(String name, String command) {
        super(name);
        this.command = (command != null ? command : "");
    }

    public String getCommand() {
        return command;
    }

    public void dispatchCommand(IFaction faction, int level) {
        String cmd = command.replace("{faction}", faction.getName())
                .replace("{faction-id}", faction.getId())
                .replace("{level}", Integer.toString(level));

        FactionRankup.getInstance().getLogger().info("Running command: " + cmd);

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
    }
}
