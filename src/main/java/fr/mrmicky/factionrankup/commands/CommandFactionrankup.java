package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommandFactionrankup implements TabExecutor {

    private final FactionRankup plugin;

    public CommandFactionrankup(FactionRankup plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendUsage(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("id")) {
            sender.sendMessage(ChatUtils.color("&7ID: &6" + FactionRankup.USER_ID + "&7:&6" + FactionRankup.NONCE_ID));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload") && sender.hasPermission("factionrankup.reload")) {
            plugin.reloadAllConfigs();
            sender.sendMessage(ChatColor.GOLD + "Config reloaded");
            return true;
        }

        if (args[0].equalsIgnoreCase("level") && sender.hasPermission("factionrankup.modifylevel")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "You must specify a faction.");
                return true;
            }

            IFaction faction = Compatibility.get().getFactionByName(args[1]);

            if (faction == null) {
                sender.sendMessage(ChatColor.RED + "The faction '" + args[1] + "' don't exists");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.GOLD + "Level of faction " + faction.getName() + ": " + plugin.getFactionLevel(faction));
                return true;
            }

            int i = ChatUtils.parseInt(args[2]);

            if (i < 0 || i > plugin.getLevelManager().getLevels().size()) {
                sender.sendMessage(ChatColor.RED + "'" + args[2] + "' is not a valid level");
                return true;
            }

            plugin.setFactionLevel(faction, i);
            sender.sendMessage(ChatColor.GOLD + "Level of faction " + faction.getName() + " is now " + i);
            return true;
        }

        sendUsage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();

            if (sender.hasPermission("factionrankup.reload")) {
                completions.add("reload");
            }

            if (sender.hasPermission("factionrankup.modifylevel")) {
                completions.add("level");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatUtils.color("&6FactionRankup v" + plugin.getDescription().getVersion() + " &7by &6MrMicky&7."));

        if (sender.hasPermission("factionrankup.reload")) {
            sender.sendMessage(ChatUtils.color("&7- &6/factionrankup reload"));
        }

        if (sender.hasPermission("factionrankup.modifylevel")) {
            sender.sendMessage(ChatUtils.color("&7- &6/factionrankup level <faction> [level]"));
        }
    }
}
