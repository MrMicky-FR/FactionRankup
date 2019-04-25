package fr.mrmicky.factionrankup.compatibility.implementations.factionsone;

import com.factionsone.factions.FPlayers;
import com.factionsone.factions.Faction;
import com.factionsone.factions.Factions;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.IFactionManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrMicky
 */
public class FactionsOneManager implements IFactionManager {

    public FactionsOneManager() {
        new FactionsOneCommand();
        new FactionsOneListener();
    }

    @Override
    public IFaction getFactionByName(String name) {
        return of(Factions.i.getByTag(name));
    }

    @Override
    public IFaction getFactionById(String id) {
        return of(Factions.i.get(id));
    }

    @Override
    public IFaction getFactionByPlayer(Player player) {
        return of(FPlayers.i.get(player).getFaction());
    }

    @Override
    public boolean isInOwnTerritory(Player player) {
        return FPlayers.i.get(player).isInOwnTerritory();
    }

    @Override
    public boolean hasFaction(Player player) {
        return FPlayers.i.get(player).hasFaction();
    }

    @Override
    public List<IFaction> getAllFactions() {
        return Factions.i.get().stream().map(this::of).collect(Collectors.toList());
    }

    @Override
    public IFaction getSafezone() {
        return of(Factions.i.get("-1"));
    }

    @Override
    public IFaction getWarzone() {
        return of(Factions.i.get("-2"));
    }

    @Override
    public IFaction getWilderness() {
        return of(Factions.i.get("0"));
    }

    private IFaction of(Faction faction) {
        return faction != null ? new FactionsOneImpl(faction) : null;
    }
}
