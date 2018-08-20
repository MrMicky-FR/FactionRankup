package fr.mrmicky.factionrankup.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;

public class Titles {

    private static void sendPacket(Player player, Object packet) throws Exception {
        Object handle = player.getClass().getMethod("getHandle").invoke(player);
        Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
        playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
    }

    private static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendTitle(Player p, Integer fadeIn, Integer stay, Integer fadeOut, String title, String subtitle) {
        if (Version.V1_7_R4.isVersionOrLower()) {
            return;
        }

        try {
            Class<?> titleClass = getNMSClass("PacketPlayOutTitle");
            Class<?> chatComponentClass = getNMSClass("IChatBaseComponent");
            Class<?> enumTitleClass = titleClass.getDeclaredClasses()[0];

            if (title != null) {
                title = Messages.color(title);
                Object componnent = chatComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + title + "\"}");
                Object titlePacket = titleClass.getConstructor(enumTitleClass, chatComponentClass).newInstance(enumTitleClass.getField("TITLE").get(null), componnent);
                sendPacket(p, titlePacket);
            }

            if (subtitle != null) {
                subtitle = Messages.color(subtitle);
                Object componnent = chatComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null,
                        "{\"text\":\"" + subtitle + "\"}");

                Object subtitlePacket = titleClass.getConstructor(enumTitleClass, chatComponentClass)
                        .newInstance(enumTitleClass.getField("SUBTITLE").get(null), componnent);
                sendPacket(p, subtitlePacket);
            }

            Object titleLenghPacket = titleClass.getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(fadeIn, stay, fadeOut);
            sendPacket(p, titleLenghPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionbar(Player p, String message) {
        if (message == null || message.isEmpty()) {
            return;
        }

        message = Messages.getMessage(message);

        if (Version.V1_7_R4.isVersionOrLower()) {
            p.sendMessage(message);
            return;
        }

        try {
            boolean old = Version.V1_11_R1.isVersionOrLower();

            Class<?> chatComponentClass = getNMSClass("IChatBaseComponent");
            Class<?> chatMessageClass = old ? null : getNMSClass("ChatMessageType");
            Object componnent = chatComponentClass.getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + message + "\"}");
            Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(chatComponentClass, old ? Byte.TYPE : chatMessageClass);
            Object titlePacket = constructor.newInstance(componnent, old ? (byte) 2 : chatMessageClass.getEnumConstants()[2]);
            sendPacket(p, titlePacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
