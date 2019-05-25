package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.inventory.RankupInventory;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class CommandRankup implements TabExecutor {

    private final FactionRankup plugin;

    public CommandRankup(FactionRankup plugin) {
        this.plugin = plugin;
    }

    public static void execute(FactionRankup plugin, Player p) {
        if (!p.hasPermission("factionranking.openmenu") && !p.hasPermission("factionrankup.use")) {
            p.sendMessage(plugin.getMessage("no-permission"));
            return;
        }

        if (!Compatibility.get().hasFaction(p)) {
            p.sendMessage(plugin.getMessage("no-faction"));
            return;
        }

        new RankupInventory(plugin, p).open(p);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command");
            return true;
        }

        execute(plugin, (Player) sender);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return Collections.emptyList();
    }
}
