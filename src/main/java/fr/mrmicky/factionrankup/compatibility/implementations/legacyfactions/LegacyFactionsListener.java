package fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import net.redstoneore.legacyfactions.event.EventFactionsChange;
import net.redstoneore.legacyfactions.event.EventFactionsDisband;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class LegacyFactionsListener extends FactionsListener {

    public LegacyFactionsListener(FactionRankup plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFactionPlayerJoin(EventFactionsChange e) {
        if (e.getReason() == EventFactionsChange.ChangeReason.CREATE) {
            return;
        }

        handleJoin(e.getFPlayer().getPlayer(), new LegacyFactionsImpl(e.getFactionNew()), e.getFactionNew().getFPlayers().size(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(EventFactionsDisband e) {
        handleDisband(new LegacyFactionsImpl(e.getFaction()));
    }
}
