package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class RankupListener implements Listener {

    private FactionRankup main;

    public RankupListener(FactionRankup main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (main.getConfig().getBoolean("level-in-chat")) {
            int level = main.getFactionLevel(e.getPlayer());

            String format = ChatUtils.color(main.getConfig().getString("chat-prefix")).replace("%level%", String.valueOf(level));
            e.setFormat(format + e.getFormat());
        }
    }
}
