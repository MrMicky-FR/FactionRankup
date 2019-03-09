package fr.mrmicky.factionrankup.commands;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.util.StringUtil;

import java.util.Collections;
import java.util.List;

public class CommandFactionrankup implements CommandExecutor, TabExecutor {

    private FactionRankup plugin;

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
            sender.sendMessage(ChatColor.YELLOW + "Config reloaded");
            return true;
        }

        sendUsage(sender);

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1 && sender.hasPermission("factionrankup.reload")) {
            if (StringUtil.startsWithIgnoreCase("reload", args[0])) {
                return Collections.singletonList("reload");
            }
        }

        return Collections.emptyList();
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(ChatUtils.color("&6FactionRankup v" + plugin.getDescription().getVersion() + " &7by &6MrMicky&7."));

        if (sender.hasPermission("factionrankup.reload")) {
            sender.sendMessage(ChatUtils.color("&7- &6/factionrankup reload"));
        }
    }
}
