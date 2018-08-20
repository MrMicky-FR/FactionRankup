package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.IFactionManager;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class FactionsUUIDManager implements IFactionManager {

    public FactionsUUIDManager() {
        new FactionsUUIDCommand();
        new FactionsUUIDListener();
    }

    @Override
    public IFaction getFactionByName(String name) {
        return of(Factions.getInstance().getByTag(name));
    }

    @Override
    public IFaction getFactionByPlayer(Player player) {
        return of(FPlayers.getInstance().getByPlayer(player).getFaction());
    }

    @Override
    public boolean isInOwnTerritory(Player player) {
        return FPlayers.getInstance().getByPlayer(player).isInOwnTerritory();
    }

    @Override
    public boolean hasFaction(Player player) {
        return FPlayers.getInstance().getByPlayer(player).hasFaction();
    }

    @Override
    public List<IFaction> getAllFactions() {
        return Factions.getInstance().getAllFactions().stream().map(this::of).collect(Collectors.toList());
    }

    @Override
    public IFaction getSafezone() {
        return of(Factions.getInstance().getSafeZone());
    }

    @Override
    public IFaction getWarzone() {
        return of(Factions.getInstance().getWarZone());
    }

    @Override
    public IFaction getWilderness() {
        return of(Factions.getInstance().getWilderness());
    }

    private IFaction of(Faction faction) {
        return new FactionsUUIDImpl(faction);
    }
}
