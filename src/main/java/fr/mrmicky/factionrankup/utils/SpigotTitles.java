package fr.mrmicky.factionrankup.utils;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

public class SpigotTitles {

    private SpigotTitles() {
        throw new UnsupportedOperationException();
    }

    public static void sendSpigotActionBar(Player p, String text) {
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(text));
    }

    public static void verifySpigotCompat() throws NoSuchMethodException {
        Player.Spigot.class.getMethod("sendMessage", ChatMessageType.class, BaseComponent.class);
    }
}
