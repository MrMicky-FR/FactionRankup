package fr.mrmicky.factionrankup.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public final class Titles {

    private static boolean supportSendTitle;
    private static boolean supportSpigotActionBar;
    private static boolean v1_7 = Bukkit.getVersion().contains("1.7");

    static {
        if (v1_7) {
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

        if (!supportSpigotActionBar) {
            TitlesNMS.sendActionbar(p, text);
            return;
        }

        SpigotTitles.sendSpigotActionBar(p, text);
    }
}
