package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.integration.Econ;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import org.bukkit.entity.Player;

import java.util.List;

public class FactionsUUIDImpl implements IFaction {

    private Faction faction;

    public FactionsUUIDImpl(Faction faction) {
        this.faction = faction;
    }

    @Override
    public String getName() {
        return faction.getTag();
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
        return Econ.hasAtLeast(faction, money, null);
    }

    @Override
    public double getMoney() {
        return Econ.getBalance(faction.getAccountId());
    }

    @Override
    public boolean addMoney(double money) {
        return Econ.deposit(faction.getAccountId(), money);
    }

    @Override
    public boolean removeMoney(double money) {
        return Econ.withdraw(faction.getAccountId(), money);
    }

    @Override
    public Object get() {
        return faction;
    }
}
