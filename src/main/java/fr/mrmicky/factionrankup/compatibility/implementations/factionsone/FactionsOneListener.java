package fr.mrmicky.factionrankup.compatibility.implementations.factionsone;

import com.factionsone.factions.event.FPlayerJoinEvent;
import com.factionsone.factions.event.FactionCreateEvent;
import com.factionsone.factions.event.FactionDisbandEvent;
import com.factionsone.factions.event.FactionRenameEvent;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

/**
 * @author MrMicky
 */
public class FactionsOneListener extends FactionsListener {

    public FactionsOneListener() {
        super(FactionRankup.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionCreate(FactionCreateEvent e) {
        handleCreate(e.getFactionTag());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFPlayerJoin(FPlayerJoinEvent e) {
        if (e.getReason() == FPlayerJoinEvent.PlayerJoinReason.CREATE) {
            return;
        }

        handleJoin(e.getFPlayer().getPlayer(), new FactionsOneImpl(e.getFaction()), e.getFaction().getFPlayers().size(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionRename(FactionRenameEvent e) {
        handleRename(e.getFaction().getTag(), e.getFactionTag());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(FactionDisbandEvent e) {
        handleDisband(new FactionsOneImpl(e.getFaction()));
    }
}