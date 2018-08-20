package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.FactionRankup;
import org.bukkit.ChatColor;

public class Messages {

    public static String getMessage(String message) {
        return color(message.replace("%prefix%", FactionRankup.getInstance().messages.getString("prefix")));
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }
}
