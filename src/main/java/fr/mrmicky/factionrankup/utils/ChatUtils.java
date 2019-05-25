package fr.mrmicky.factionrankup.utils;

import org.bukkit.ChatColor;

/**
 * @author MrMicky
 */
public final class ChatUtils {

    private ChatUtils() {
        throw new UnsupportedOperationException();
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static int parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
