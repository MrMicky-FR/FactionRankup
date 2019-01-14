package fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions;

import fr.mrmicky.factionrankup.compatibility.IFaction;
import net.redstoneore.legacyfactions.entity.Faction;
import net.redstoneore.legacyfactions.entity.VaultAccount;
import org.bukkit.entity.Player;

import java.util.List;

public class LegacyFactionsImpl implements IFaction {

    private Faction faction;

    public LegacyFactionsImpl(Faction faction) {
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
        return VaultAccount.get(faction).has(money);
    }

    @Override
    public double getMoney() {
        return VaultAccount.get(faction).getBalance();
    }

    @Override
    public boolean addMoney(double money) {
        return VaultAccount.get(faction).deposit(money);
    }

    @Override
    public boolean removeMoney(double money) {
        return VaultAccount.get(faction).withdraw(money);
    }

    @Override
    public Object get() {
        return faction;
    }
}
