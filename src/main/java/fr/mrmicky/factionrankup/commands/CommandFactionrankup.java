package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ChatUtils;
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
            sender.sendMessage(ChatUtils.color("&bFactionRankup &7version &b" + main.getDescription().getVersion()));
            sender.sendMessage(ChatUtils.color("&7Download: &b" + main.getDescription().getWebsite()));
            sender.sendMessage(ChatUtils.color("&7ID: &b" + FactionRankup.USER_ID + "&7//&b" + FactionRankup.NONCE_ID));
            sender.sendMessage(ChatUtils.color("&8- &3/factionrankup reload"));
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("factionrankup.reload")) {
                    main.reloadAllConfigs();
                    sender.sendMessage(ChatColor.DARK_AQUA + "You have reloaded the configurations files.");
                } else {
                    sender.sendMessage(main.getMessage("no-permission"));
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /factionrankup <reload>");
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            if (sender.hasPermission("factionrankup.reload")) {
                completions.add("reload");
            }

            return StringUtil.copyPartialMatches(args[0], completions, new ArrayList<>());
        }

        return Collections.emptyList();
    }
}
