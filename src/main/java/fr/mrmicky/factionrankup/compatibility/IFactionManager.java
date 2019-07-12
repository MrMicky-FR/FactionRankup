package fr.mrmicky.factionrankup.compatibility;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.List;

public interface IFactionManager {

    IFaction getFactionByName(String name);

    IFaction getFactionById(String id);

    IFaction getFactionByPlayer(Player player);

    default IFaction getFactionByBlock(Block block) {
        return getFactionByLocation(block.getLocation());
    }

    IFaction getFactionByLocation(Location location);

    boolean isInOwnTerritory(Player player);

    boolean hasFaction(Player player);

    List<IFaction> getAllFactions();

    IFaction getSafezone();

    IFaction getWarzone();

    IFaction getWilderness();
}
