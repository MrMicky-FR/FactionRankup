package fr.mrmicky.factionrankup.compatibility.implementations.factions;

import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionsListener;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class MFactionListener extends FactionsListener {

    public MFactionListener() {
        super(FactionRankup.getInstance());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onFactionPlayerJoin(EventFactionsMembershipChange e) {
        switch (e.getReason()) {
            case JOIN:
                Player player = e.getMPlayer().getPlayer();
                int size = e.getNewFaction().getMPlayers().size();

                handleJoin(player, new MFactionsImpl(e.getNewFaction()), size, e);

                break;
            case LEAVE:
            case KICK:
                handleLeave(e.getMPlayer().getPlayer());
                break;
            default:
                break;
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFactionDisband(EventFactionsDisband e) {
        handleDisband(new MFactionsImpl(e.getFaction()));
    }
}
