package fr.mrmicky.factionrankup.economy;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class VaultAdapter {

    private Economy economy;

    public double getMoney(Player player) {
        return economy.getBalance(player);
    }

    public boolean hasMoney(Player player, double amount) {
        return economy.has(player, amount);
    }

    public boolean addMoney(Player player, double amount) {
        return economy.depositPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

    public boolean removeMoney(Player player, double amount) {
        return economy.withdrawPlayer(player, amount).type == EconomyResponse.ResponseType.SUCCESS;
    }

    public boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (serviceProvider == null) {
            return false;
        }
        economy = serviceProvider.getProvider();

        return true;
    }
}
