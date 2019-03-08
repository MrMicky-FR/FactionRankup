package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.inventory.RankupInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandRankup implements CommandExecutor {

    private FactionRankup main;

    public CommandRankup(FactionRankup main) {
        this.main = main;
    }

    public static void execute(FactionRankup main, Player p) {
        if (!p.hasPermission("factionranking.openmenu") && !p.hasPermission("factionrankup.use")) {
            p.sendMessage(main.getMessage("no-permission"));
            return;
        }

        if (!Compatibility.get().hasFaction(p)) {
            p.sendMessage(main.getMessage("no-faction"));
            return;
        }

        new RankupInventory(main, p).open(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        execute(main, (Player) sender);
        return true;
    }
}
