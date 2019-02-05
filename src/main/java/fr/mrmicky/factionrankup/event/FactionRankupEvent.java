package fr.mrmicky.factionrankup.event;

import fr.mrmicky.factionrankup.compatibility.IFaction;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author MrMicky
 */
public class FactionRankupEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private IFaction faction;
    private int oldLevel;
    private int newLevel;
    private boolean cancelled = false;

    public FactionRankupEvent(IFaction faction, int oldLevel, int newLevel) {
        this.faction = faction;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public IFaction getFaction() {
        return faction;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
