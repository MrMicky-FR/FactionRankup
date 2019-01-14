package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandFactionrankup implements CommandExecutor, TabExecutor {

    private FactionRankup main;

    public CommandFactionrankup(FactionRankup main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§7§m------------------------------");
            sender.sendMessage("§3/factionrankup about");
            sender.sendMessage("§3/factionrankup reload");
            sender.sendMessage("§7§m------------------------------");
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("factionranking.reload") || sender.hasPermission("factionrankup.reload")) {
                    ConfigHandler.loadAll();
                    sender.sendMessage("§3You have reloaded the configurations files.");
                } else {
                    sender.sendMessage(main.getMessage("no-permission"));
                }
            } else if (args[0].equalsIgnoreCase("about")) {
                sender.sendMessage("§7§m------------------------------");
                sender.sendMessage("§7Download: §3" + main.getDescription().getWebsite());
                sender.sendMessage("§7Author: §3" + main.getDescription().getAuthors().get(0));
                sender.sendMessage("§7Version: §3" + main.getDescription().getVersion());
                sender.sendMessage("§7ID: §3" + FactionRankup.USER_ID);
                sender.sendMessage("§7Nonce: §3" + FactionRankup.NONCE_ID);
                sender.sendMessage("§7§m------------------------------");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("about");
            if (sender.hasPermission("factionranking.reload") || sender.hasPermission("factionrankup.reload")) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
