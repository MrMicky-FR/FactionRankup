package fr.mrmicky.factionrankup.compatibility.implementations.factions;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.ps.PS;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.IFactionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class MFactionsManager implements IFactionManager {

    private MFactionCommand command;

    public MFactionsManager() {
        command = new MFactionCommand();
        new MFactionListener();
    }

    @Override
    public IFaction getFactionByName(String name) {
        return of(FactionColl.get().getByName(name));
    }

    @Override
    public IFaction getFactionById(String id) {
        return of(FactionColl.get().getIdToEntityRaw().get(id));
    }

    @Override
    public IFaction getFactionByPlayer(Player player) {
        return of(MPlayer.get(player).getFaction());
    }

    @Override
    public IFaction getFactionByLocation(Location location) {
        return of(BoardColl.get().getFactionAt(PS.valueOf(location)));
    }

    @Override
    public boolean hasFaction(Player player) {
        return MPlayer.get(player).hasFaction();
    }

    @Override
    public boolean isInOwnTerritory(Player player) {
        return MPlayer.get(player).isInOwnTerritory();
    }

    @Override
    public List<IFaction> getAllFactions() {
        return FactionColl.get().getAll().stream().map(this::of).collect(Collectors.toList());
    }

    @Override
    public IFaction getSafezone() {
        return of(FactionColl.get().getSafezone());
    }

    @Override
    public IFaction getWarzone() {
        return of(FactionColl.get().getWarzone());
    }

    @Override
    public IFaction getWilderness() {
        return of(FactionColl.get().getNone());
    }

    private IFaction of(Faction faction) {
        return faction != null ? new MFactionsImpl(faction) : null;
    }

    @Override
    public void disable() {
        command.close();
    }
}
