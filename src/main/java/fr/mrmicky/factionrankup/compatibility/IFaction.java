package fr.mrmicky.factionrankup.compatibility;

import org.bukkit.entity.Player;

import java.util.List;

public interface IFaction {

    String getName();

    String getId();

    List<Player> getPlayers();

    boolean hasMoney(double money);

    double getMoney();

    boolean addMoney(double money);

    boolean removeMoney(double money);

    Object get();
}
