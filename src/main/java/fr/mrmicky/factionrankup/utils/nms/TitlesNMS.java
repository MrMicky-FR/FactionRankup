package fr.mrmicky.factionrankup.utils.nms;

import fr.mrmicky.factionrankup.utils.FastReflection;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author MrMicky
 */
public final class TitlesNMS {

    // Packet sending
    private static final Field PLAYER_CONNECTION;
    private static final Method SEND_PACKET;
    private static final Method PLAYER_GET_HANDLE;

    // Utils
    private static final Class<?> CHAT_COMPONENT_CLASS;
    private static final Method MESSAGE_FROM_STRING;
    private static final Constructor<?> CHAT_COMPONENT_TEXT;

    // Packets
    private static final Constructor<?> PACKET_TITLE;
    private static final Constructor<?> PACKET_TITLE_TIME;
    private static final Object TITLE_ACTION_TITLE;
    private static final Object TITLE_ACTION_SUBTITLE;

    private static final Object CHAT_ACTION_ACTIONBAR;
    private static final Constructor<?> PACKET_CHAT;

    static {
        try {
            Class<?> craftChatMessageClass = FastReflection.obcClass("util.CraftChatMessage");

            Class<?> entityPlayerClass = FastReflection.nmsClass("EntityPlayer");
            Class<?> playerConnectionClass = FastReflection.nmsClass("PlayerConnection");
            Class<?> craftPlayerClass = FastReflection.obcClass("entity.CraftPlayer");

            Class<?> packetTitleClass = FastReflection.nmsClass("PacketPlayOutTitle");
            Class<?> packetTitleAction = FastReflection.nmsClass("PacketPlayOutTitle$EnumTitleAction");
            Class<?> packetChatClass = FastReflection.nmsClass("PacketPlayOutChat");
            Class<?> chatMessageType;

            CHAT_COMPONENT_TEXT = FastReflection.nmsClass("ChatComponentText").getConstructor(String.class);

            MESSAGE_FROM_STRING = craftChatMessageClass.getDeclaredMethod("fromString", String.class);
            CHAT_COMPONENT_CLASS = FastReflection.nmsClass("IChatBaseComponent");

            PLAYER_GET_HANDLE = craftPlayerClass.getDeclaredMethod("getHandle");
            PLAYER_CONNECTION = entityPlayerClass.getDeclaredField("playerConnection");
            SEND_PACKET = playerConnectionClass.getDeclaredMethod("sendPacket", FastReflection.nmsClass("Packet"));

            PACKET_TITLE = packetTitleClass.getConstructor(packetTitleAction, CHAT_COMPONENT_CLASS);
            PACKET_TITLE_TIME = packetTitleClass.getConstructor(int.class, int.class, int.class);
            TITLE_ACTION_TITLE = packetTitleAction.getEnumConstants()[0];
            TITLE_ACTION_SUBTITLE = packetTitleAction.getEnumConstants()[1];

            try {
                chatMessageType = FastReflection.nmsClass("ChatMessageType");
            } catch (ClassNotFoundException e) {
                chatMessageType = null;
            }

            if (chatMessageType != null) {
                CHAT_ACTION_ACTIONBAR = chatMessageType.getEnumConstants()[2];
                PACKET_CHAT = packetChatClass.getConstructor(CHAT_COMPONENT_CLASS, chatMessageType);
            } else {
                CHAT_ACTION_ACTIONBAR = null;
                PACKET_CHAT = packetChatClass.getConstructor(CHAT_COMPONENT_CLASS, byte.class);
            }
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void sendTitle(Player p, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        try {
            if (title != null) {
                Object packetTitle = PACKET_TITLE.newInstance(TITLE_ACTION_TITLE, getChatBaseComponent(title));
                sendPacket(p, packetTitle);
            }

            if (subtitle != null) {
                Object packetSubtitle = PACKET_TITLE.newInstance(TITLE_ACTION_SUBTITLE, getChatBaseComponent(subtitle));
                sendPacket(p, packetSubtitle);
            }

            sendPacket(p, PACKET_TITLE_TIME.newInstance(fadeIn, stay, fadeOut));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sendActionbar(Player p, String message) {
        if (message == null) {
            return;
        }

        try {
            Object component = CHAT_COMPONENT_TEXT.newInstance(message);
            Object packet = PACKET_CHAT.newInstance(component, CHAT_ACTION_ACTIONBAR == null ? (byte) 2 : CHAT_ACTION_ACTIONBAR);
            sendPacket(p, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Object getChatBaseComponent(String s) throws ReflectiveOperationException {
        return Array.get(MESSAGE_FROM_STRING.invoke(null, s), 0);
    }

    private static void sendPacket(Player p, Object packet) throws ReflectiveOperationException {
        Object entityPlayer = PLAYER_GET_HANDLE.invoke(p);
        Object playerConnection = PLAYER_CONNECTION.get(entityPlayer);
        SEND_PACKET.invoke(playerConnection, packet);
    }
}
