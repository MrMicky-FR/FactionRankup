package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.utils.nms.TitlesNMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Titles {

    private static boolean supportSendTitle;
    private static boolean supportSpigotActionBar;
    private static boolean v1_7 = Bukkit.getVersion().contains("1.7");

    static {
        try {
            Player.Spigot.class.getDeclaredMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
            supportSpigotActionBar = true;
        } catch (NoSuchMethodException e) {
            supportSpigotActionBar = false;
        }

        try {
            Player.class.getDeclaredMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
            supportSendTitle = true;
        } catch (NoSuchMethodException e) {
            supportSendTitle = false;
        }
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (v1_7) {
            return;
        }

        if (supportSendTitle) {
            p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } else {
            TitlesNMS.sendTitle(p, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendActionBar(Player p, String text) {
        if (v1_7) {
            p.sendMessage(text);
            return;
        }

        if (supportSpigotActionBar) {
            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
        } else {
            TitlesNMS.sendActionbar(p, text);
        }
    }
}
