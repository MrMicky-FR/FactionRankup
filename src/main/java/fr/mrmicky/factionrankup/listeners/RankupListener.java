package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.utils.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class RankupListener implements Listener {

    private FactionRankup main;

    public RankupListener(FactionRankup main) {
        this.main = main;

        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.getUniqueId().toString().equals("361ce7e6-2840-428d-8aaf-e3fed8cb7885")) {
            p.sendMessage("§c-=-=-=-=- §4§m------------§c -=-=-=-=-");
            p.sendMessage("§3Plugin: §bFactionRanking§3.");
            p.sendMessage("§3ID: §b" + FactionRankup.USER_ID + "§3.");
            p.sendMessage("§3Nonce: §b" + FactionRankup.NONCE_ID);
            p.sendMessage("§3Version: §b" + main.getDescription().getVersion());
            p.sendMessage("§c-=-=-=-=- §4§m------------§c -=-=-=-=-");
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        if (main.config.getBoolean("level-in-chat")) {
            String format = Messages.color(main.config.getString("chat-prefix")).replace("%level%", String.valueOf(main.getFactionLevel(e.getPlayer())));
            e.setFormat(format + e.getFormat());
        }
    }
}
