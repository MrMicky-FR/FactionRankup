package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FPlayerLeaveEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class FactionsUUIDListener extends FactionsListener {

    public FactionsUUIDListener(FactionRankup plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFPlayerJoin(FPlayerJoinEvent e) {
        if (e.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
            return;
        }

        handleJoin(e.getfPlayer().getPlayer(), new FactionsUUIDImpl(e.getFaction()), e.getFaction().getFPlayers().size(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionLeave(FPlayerLeaveEvent e) {
        handleLeave(e.getfPlayer().getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(FactionDisbandEvent e) {
        handleDisband(new FactionsUUIDImpl(e.getFaction()));
    }
}
