package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.abilities.Ability;
import fr.mrmicky.factionrankup.abilities.ChanceAbility;
import fr.mrmicky.factionrankup.abilities.MultiplierAbility;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.utils.Titles;
import org.bukkit.Bukkit;
import org.bukkit.CropState;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Crops;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.UnaryOperator;

public class AbilitiesListener implements Listener {

    private final Set<UUID> flying = new HashSet<>();

    private final FactionRankup plugin;

    public AbilitiesListener(FactionRankup plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSpawnerSpawn(SpawnerSpawnEvent e) {
        CreatureSpawner spawner = e.getSpawner();

        IFaction faction = Compatibility.get().getFactionByLocation(spawner.getLocation());
        if (faction == null) {
            return;
        }

        int factionLevel = plugin.getFactionLevel(faction);
        Optional<MultiplierAbility> ability = getActiveAbility(factionLevel, "SpawnerBoost", MultiplierAbility.class);

        if (ability.isPresent() && ability.get().isActive()) {
            int multiplier = ability.get().nextRandomMultiplier();

            if (multiplier <= 1) {
                return;
            }

            for (int i = 1; i < multiplier; i++) {
                spawner.getWorld().spawnEntity(spawner.getLocation().add(0, 0, 1), e.getEntityType());
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player || e.getEntity().getKiller() == null) {
            return;
        }

        LivingEntity entity = e.getEntity();
        Player killer = entity.getKiller();

        Optional<MultiplierAbility> ability = getActiveAbility(killer, "DropMultiplier", MultiplierAbility.class);
        if (ability.isPresent() && ability.get().isActive()) {
            int multiplier = ability.get().nextRandomMultiplier();

            if (multiplier <= 1) {
                return;
            }

            sendActionbar(killer, "drop-multiplier", s -> s.replace("%chances%", Integer.toString(multiplier)));

            for (int i = 1; i < multiplier; i++) {
                e.getDrops().forEach(item -> entity.getWorld().dropItemNaturally(entity.getLocation(), item));
            }
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent e) {
        Player player = e.getPlayer();

        if (isChanceAbilityActive(player, "DoubleXP")) {
            sendActionbar(player, "double-xp");
            e.setAmount(e.getAmount() * 2);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player) || e.getCause() != DamageCause.FALL) {
            return;
        }

        Player player = (Player) e.getEntity();
        if (isChanceAbilityActive(player, "ReduceFalls")) {
            sendActionbar(player, "reducefall");
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        BlockState state = e.getBlockPlaced().getState();

        if (!(state.getData() instanceof Crops)) {
            return;
        }

        if (isChanceAbilityActive(player, "InstantCrops")) {
            Crops crops = (Crops) state.getData();
            crops.setState(CropState.RIPE);
            state.update();

            sendActionbar(player, "instantcrops");
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        // noinspection deprecation
        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR || item.containsEnchantment(Enchantment.SILK_TOUCH)
                || !Enchantment.SILK_TOUCH.canEnchantItem(item) || player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (isChanceAbilityActive(player, "SilkTouch")) {
            sendActionbar(player, "silktouch");

            item.addEnchantment(Enchantment.SILK_TOUCH, 1);
            // noinspection deprecation
            Bukkit.getScheduler().runTask(plugin, () -> player.getItemInHand().removeEnchantment(Enchantment.SILK_TOUCH));
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) {
            return;
        }

        if (from.getX() == to.getX() && from.getZ() == to.getZ()) {
            return;
        }

        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
            return;
        }

        if (isAbilityActive(player, "Fly")) {
            if (Compatibility.get().isInOwnTerritory(player)) {
                if (!player.getAllowFlight()) {
                    if (!flying.contains(player.getUniqueId())) {
                        flying.add(player.getUniqueId());
                        player.setAllowFlight(true);
                        sendActionbar(player, "fly-enabled");
                    }
                }
            } else if (player.getAllowFlight()) {
                if (flying.contains(player.getUniqueId())) {
                    flying.remove(player.getUniqueId());
                    player.setAllowFlight(false);
                    sendActionbar(player, "fly-disabled");
                }
            }
        }
    }

    private void sendActionbar(Player player, String path) {
        sendActionbar(player, path, UnaryOperator.identity());
    }

    private void sendActionbar(Player player, String path, UnaryOperator<String> operator) {
        if (!path.isEmpty()) {
            Titles.sendActionBar(player, operator.apply(plugin.getMessage(path)));
        }
    }

    private boolean isAbilityActive(Player player, String name) {
        int level = plugin.getFactionLevel(player);

        return plugin.getLevelManager().getAbilitiesForLevel(level, name).findAny().isPresent();
    }

    private <T extends Ability> Optional<T> getActiveAbility(Player player, String name, Class<T> abilityClass) {
        return getActiveAbility(plugin.getFactionLevel(player), name, abilityClass);
    }

    private <T extends Ability> Optional<T> getActiveAbility(int factionLevel, String name, Class<T> abilityClass) {
        return plugin.getLevelManager().getAbilitiesForLevel(factionLevel, name)
                .filter(abilityClass::isInstance)
                .map(abilityClass::cast)
                .findAny();
    }

    private boolean isChanceAbilityActive(Player player, String name) {
        return getActiveAbility(player, name, ChanceAbility.class).filter(ChanceAbility::isActive).isPresent();
    }
}
