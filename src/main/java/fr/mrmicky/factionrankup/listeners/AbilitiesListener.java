package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.abilities.Ability;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.utils.Titles;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AbilitiesListener implements Listener {

    private FactionRankup main;
    private List<UUID> flying = new ArrayList<>();

    public AbilitiesListener(FactionRankup main) {
        this.main = main;

        Bukkit.getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getEntity().getKiller() == null) {
            return;
        }

        Player p = (Player) e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (Ability.MORE_DROPS.isActive(killer)) {
            sendActionbar(killer, "moredrops");
            e.getDrops().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item));
        }
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();

        if (Ability.DOUBLE_XP.isActive(p)) {
            sendActionbar(p, "double-xp");
            e.setAmount(e.getAmount() * 2);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getCause() != DamageCause.FALL) {
            return;
        }

        Player p = (Player) e.getEntity();
        if (Ability.REDUCE_FALL.isActive(p)) {
            sendActionbar(p, "reducefall");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        // noinspection deprecation
        ItemStack item = p.getItemInHand();

        if (item == null || item.getType() == Material.AIR || item.containsEnchantment(Enchantment.SILK_TOUCH)
                || !Enchantment.SILK_TOUCH.canEnchantItem(item) || p.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (Ability.SILKTOUCH.isActive(p)) {
            sendActionbar(p, "silktouch");

            p.getItemInHand().addEnchantment(Enchantment.SILK_TOUCH, 1);
            Bukkit.getScheduler().runTask(main, () -> p.getItemInHand().removeEnchantment(Enchantment.SILK_TOUCH));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return;
        }

        if (Ability.FLY.isActive(p)) {
            if (Compatibility.get().isInOwnTerritory(p)) {
                if (!p.getAllowFlight()) {
                    if (!flying.contains(p.getUniqueId())) {
                        flying.add(p.getUniqueId());
                        p.setAllowFlight(true);
                        sendActionbar(p, "fly-enabled");
                    }
                }
            } else if (p.getAllowFlight()) {
                if (flying.contains(p.getUniqueId())) {
                    flying.remove(p.getUniqueId());
                    p.setAllowFlight(false);
                    sendActionbar(p, "fly-disabled");
                }
            }
        }
    }

    private void sendActionbar(Player p, String s) {
        if (!s.isEmpty()) {
            Titles.sendActionbar(p, main.messages.getString(s));
        }
    }
}
