package fr.mrmicky.factionrankup.utils;

import fr.mrmicky.factionrankup.utils.nms.TitlesNMS;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class Titles {

    private static boolean supportSendTitle;
    private static boolean supportSpigotActionBar;

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
        if (Version.V1_7_R4.isVersionOrLower()) {
            return;
        }

        if (supportSendTitle) {
            p.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } else {
            TitlesNMS.sendTitle(p, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendActionBar(Player p, String text) {
        if (Version.V1_7_R4.isVersionOrLower()) {
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
