package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.abilities.Ability;
import fr.mrmicky.factionrankup.abilities.ChanceAbility;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.utils.Titles;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class AbilitiesListener implements Listener {

    private final Set<UUID> flying = new HashSet<>();

    private final FactionRankup plugin;

    public AbilitiesListener(FactionRankup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getEntity().getKiller() == null) {
            return;
        }

        Player p = (Player) e.getEntity();
        Player killer = e.getEntity().getKiller();

        if (isChanceAbilityActive(p, "MoreDrops")) {
            sendActionbar(killer, "moredrops");
            e.getDrops().forEach(item -> p.getWorld().dropItemNaturally(p.getLocation(), item));
        }
    }

    @EventHandler
    public void onExp(PlayerExpChangeEvent e) {
        Player p = e.getPlayer();

        if (isChanceAbilityActive(p, "DoubleXP")) {
            sendActionbar(p, "double-xp");
            e.setAmount(e.getAmount() * 2);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getCause() != DamageCause.FALL) {
            return;
        }

        Player p = (Player) e.getEntity();
        if (isChanceAbilityActive(p, "ReduceFalls")) {
            sendActionbar(p, "reducefall");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        BlockState state = e.getBlockPlaced().getState();

        if (!(state.getData() instanceof Crops)) {
            return;
        }

        if (isChanceAbilityActive(p, "InstantCrops")) {
            Crops crops = (Crops) state.getData();
            crops.setState(CropState.RIPE);
            state.update();

            sendActionbar(p, "instantcrops");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        // noinspection deprecation
        ItemStack item = p.getItemInHand();

        if (item == null || item.getType() == Material.AIR || item.containsEnchantment(Enchantment.SILK_TOUCH)
                || !Enchantment.SILK_TOUCH.canEnchantItem(item) || p.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (isChanceAbilityActive(p, "SilkTouch")) {
            sendActionbar(p, "silktouch");

            item.addEnchantment(Enchantment.SILK_TOUCH, 1);
            // noinspection deprecation
            Bukkit.getScheduler().runTask(plugin, () -> p.getItemInHand().removeEnchantment(Enchantment.SILK_TOUCH));
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

        if (isAbilityActive(p, "Fly")) {
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
            Titles.sendActionBar(p, plugin.getMessage(s));
        }
    }

    private boolean isAbilityActive(Player p, String name) {
        int level = plugin.getFactionLevel(p);

        return plugin.getLevelManager().getAbilitiesForLevel(level, name).findAny().isPresent();
    }

    private boolean isChanceAbilityActive(Player p, String name) {
        int level = plugin.getFactionLevel(p);

        Optional<Ability> ability = plugin.getLevelManager().getAbilitiesForLevel(level, name)
                .filter(a -> a.getClass() == ChanceAbility.class)
                .findFirst();

        return ability.filter(ability1 -> ((ChanceAbility) ability1).isActive()).isPresent();

    }
}
