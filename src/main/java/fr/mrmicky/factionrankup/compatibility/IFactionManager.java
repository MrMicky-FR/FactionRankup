package fr.mrmicky.factionrankup.compatibility;

import org.bukkit.entity.Player;

import java.util.List;

public interface IFactionManager {

    IFaction getFactionByName(String name);

    IFaction getFactionByPlayer(Player player);

    boolean isInOwnTerritory(Player player);

    boolean hasFaction(Player player);

    List<IFaction> getAllFactions();

    IFaction getSafezone();

    IFaction getWarzone();

    IFaction getWilderness();
}
