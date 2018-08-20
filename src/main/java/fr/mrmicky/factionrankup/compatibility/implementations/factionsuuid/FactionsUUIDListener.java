package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.factions.event.FactionCreateEvent;
import com.massivecraft.factions.event.FactionDisbandEvent;
import com.massivecraft.factions.event.FactionRenameEvent;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class FactionsUUIDListener extends FactionsListener {

    public FactionsUUIDListener() {
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

        handleJoin(e.getfPlayer().getPlayer(), new FactionsUUIDImpl(e.getFaction()), e.getFaction().getFPlayers().size(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionRename(FactionRenameEvent e) {
        handleRename(e.getFaction().getTag(), e.getFactionTag());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(FactionDisbandEvent e) {
        handleDisband(new FactionsUUIDImpl(e.getFaction()));
    }
}
