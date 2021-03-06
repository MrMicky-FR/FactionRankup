package fr.mrmicky.factionrankup.listeners;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.abilities.Ability;
import fr.mrmicky.factionrankup.abilities.ChanceAbility;
import fr.mrmicky.factionrankup.abilities.MultiplierAbility;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.utils.Titles;
import fr.mrmicky.factionrankup.utils.crops.CropsData;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.SpawnerSpawnEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
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
    public void onBlockGrow(BlockGrowEvent e) {
        Block block = e.getBlock();

        IFaction faction = Compatibility.get().getFactionByLocation(block.getLocation());
        if (faction == null) {
            return;
        }

        int factionLevel = plugin.getFactionLevel(faction);
        Optional<ChanceAbility> ability = getActiveAbility(factionLevel, "FarmBoost", ChanceAbility.class);

        if (!ability.isPresent() || !ability.get().isActive()) {
            return;
        }

        BlockState state = e.getNewState();
        CropsData cropsData = CropsData.of(state);

        if (cropsData != null) {
            cropsData.setRipe();
            return;
        }

        if (state.getType() == Material.CACTUS || state.getType().toString().contains("SUGAR_CANE")) {
            Block topBlock = block.getRelative(BlockFace.UP);

            if (topBlock.getType() == Material.AIR) {
                topBlock.setType(state.getType());
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
    public void onPlayerJoin(PlayerJoinEvent e) {
        handleMove(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (flying.remove(player.getUniqueId())) {
            player.setAllowFlight(false);
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

        CropsData cropsData = CropsData.of(state);

        if (cropsData == null) {
            return;
        }

        if (isChanceAbilityActive(player, "InstantCrops")) {
            cropsData.setRipe();
            state.update();

            sendActionbar(player, "instantcrops");
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        Player player = e.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item.getType() == Material.AIR || player.getGameMode() != GameMode.SURVIVAL) {
            return;
        }

        if (!item.containsEnchantment(Enchantment.SILK_TOUCH)
                && Enchantment.SILK_TOUCH.canEnchantItem(item)
                && isChanceAbilityActive(player, "SilkTouch")) {
            sendActionbar(player, "silktouch");

            item.addEnchantment(Enchantment.SILK_TOUCH, 1);

            Bukkit.getScheduler().runTask(plugin, () -> player.getItemInHand().removeEnchantment(Enchantment.SILK_TOUCH));
        }

        if (block.getType().name().contains("_ORE") && isChanceAbilityActive(player, "DoubleOre")) {

            Collection<ItemStack> drops = block.getDrops(item);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (!e.isCancelled()) {
                    drops.forEach(i -> block.getWorld().dropItem(block.getLocation(), i));

                    System.out.println("GOODD");

                    sendActionbar(player, "double-ore");
                } else {
                    System.out.println("NOOOPP");
                }
            });
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

        handleMove(player);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent e) {
        Bukkit.getScheduler().runTask(plugin, () -> handleMove(e.getPlayer()));
    }

    private void handleMove(Player player) {
        if (player.getGameMode() != GameMode.SURVIVAL && player.getGameMode() != GameMode.ADVENTURE) {
            return;
        }

        if (isAbilityActive(player, "Fly")) {
            if (Compatibility.get().isInOwnTerritory(player)) {
                if (!player.getAllowFlight()) {
                    if (flying.add(player.getUniqueId())) {
                        player.setAllowFlight(true);
                        sendActionbar(player, "fly-enabled");
                    }
                }
            } else if (player.getAllowFlight()) {
                if (flying.remove(player.getUniqueId())) {
                    player.setAllowFlight(false);
                    player.setFallDistance(0);

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
