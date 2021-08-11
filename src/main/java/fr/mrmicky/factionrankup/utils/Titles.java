package fr.mrmicky.factionrankup.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Titles {

    private static final boolean LEGACY = Bukkit.getVersion().contains("1.7");
    private static boolean supportSendTitle;
    private static boolean supportSpigotActionBar;

    static {
        if (LEGACY) {
            supportSendTitle = false;
            supportSpigotActionBar = false;
        } else {
            try {
                Class.forName("net.md_5.bungee.api.ChatMessageType");
                Player.class.getMethod("spigot");
                SpigotTitles.verifySpigotCompat();

                supportSpigotActionBar = true;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                supportSpigotActionBar = false;
            }

            try {
                Player.class.getMethod("sendTitle", String.class, String.class, int.class, int.class, int.class);
                supportSendTitle = true;
            } catch (NoSuchMethodException e) {
                supportSendTitle = false;
            }
        }
    }

    private Titles() {
        throw new UnsupportedOperationException();
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        if (LEGACY) {
            return;
        }

        if (supportSendTitle) {
            player.sendTitle(title, subtitle, fadeIn, stay, fadeOut);
        } else {
            TitlesNMS.sendTitle(player, title, subtitle, fadeIn, stay, fadeOut);
        }
    }

    public static void sendActionBar(Player player, String text) {
        if (LEGACY) {
            player.sendMessage(text);
            return;
        }

        if (!supportSpigotActionBar) {
            TitlesNMS.sendActionbar(player, text);
            return;
        }

        SpigotTitles.sendSpigotActionBar(player, text);
    }
}
