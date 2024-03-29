package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RankupListener implements Listener {

    private final FactionRankup plugin;

    public RankupListener(FactionRankup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (plugin.getConfig().getBoolean("level-in-chat")) {
            int level = plugin.getFactionLevel(e.getPlayer());

            if (level == 0 && !plugin.getConfig().getBoolean("prefix-no-level")) {
                return;
            }

            String format = ChatUtils.color(plugin.getConfig().getString("chat-prefix")).replace("%level%", Integer.toString(level));
            e.setFormat(format + e.getFormat());
        }
    }
}
