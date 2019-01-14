package fr.mrmicky.factionrankup.compatibility.implementations.factions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.money.Money;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import org.bukkit.entity.Player;

import java.util.List;

public class MFactionsImpl implements IFaction {

    private Faction faction;

    public MFactionsImpl(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getName() {
        return faction.getName();
    }

    @Override
    public String getId() {
        return faction.getId();
    }

    @Override
    public List<Player> getPlayers() {
        return faction.getOnlinePlayers();
    }

    @Override
    public boolean hasMoney(double money) {
        return Money.has(faction, money);
    }

    @Override
    public double getMoney() {
        return Money.get(faction);
    }

    @Override
    public boolean addMoney(double money) {
        return Money.move(null, faction, null, money);
    }

    @Override
    public boolean removeMoney(double money) {
        return Money.move(faction, null, null, money);
    }

    @Override
    public Object get() {
        return faction;
    }
}
