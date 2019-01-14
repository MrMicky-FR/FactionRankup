package fr.mrmicky.factionrankup.compatibility.implementations.factions;

import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class MFactionListener extends FactionsListener {

    public MFactionListener() {
        super(FactionRankup.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFactionPlayerJoin(EventFactionsMembershipChange e) {
        if (e.getReason() != EventFactionsMembershipChange.MembershipChangeReason.JOIN) {
            return;
        }

        handleJoin(e.getMPlayer().getPlayer(), new MFactionsImpl(e.getNewFaction()), e.getNewFaction().getMPlayers().size(), e);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(EventFactionsDisband e) {
        handleDisband(new MFactionsImpl(e.getFaction()));
    }
}
