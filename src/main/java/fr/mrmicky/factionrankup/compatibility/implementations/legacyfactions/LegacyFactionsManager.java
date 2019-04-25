package fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions;

import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.compatibility.IFactionManager;
import net.redstoneore.legacyfactions.entity.FPlayerColl;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.FactionColl;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class LegacyFactionsManager implements IFactionManager {

    public LegacyFactionsManager() {
        new LegacyFactionsCommand();
        new LegacyFactionsListener();
    }

    @Override
    public IFaction getFactionByName(String name) {
        return of(FactionColl.get().getByTag(name));
    }

    @Override
    public IFaction getFactionById(String id) {
        return of(FactionColl.get().getFactionById(id));
    }

    @Override
    public IFaction getFactionByPlayer(Player player) {
        return of(FPlayerColl.get(player).getFaction());
    }

    @Override
    public boolean isInOwnTerritory(Player player) {
        return FPlayerColl.get(player).isInOwnTerritory();
    }

    @Override
    public boolean hasFaction(Player player) {
        return FPlayerColl.get(player).hasFaction();
    }

    @Override
    public List<IFaction> getAllFactions() {
        return FactionColl.get().getAllFactions().stream().map(this::of).collect(Collectors.toList());
    }

    @Override
    public IFaction getSafezone() {
        return of(FactionColl.get().getSafeZone());
    }

    @Override
    public IFaction getWarzone() {
        return of(FactionColl.get().getWarZone());
    }

    @Override
    public IFaction getWilderness() {
        return of(FactionColl.get().getWilderness());
    }

    private IFaction of(Faction faction) {
        return faction != null ? new LegacyFactionsImpl(faction) : null;
    }
}
