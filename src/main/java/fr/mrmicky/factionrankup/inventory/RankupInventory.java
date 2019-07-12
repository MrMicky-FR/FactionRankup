package fr.mrmicky.factionrankup.inventory;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.abilities.CommandAbility;
import fr.mrmicky.factionrankup.abilities.Level;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.event.FactionRankupEvent;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import fr.mrmicky.factionrankup.utils.FastReflection;
import fr.mrmicky.factionrankup.utils.Titles;
import fr.mrmicky.fastinv.FastInv;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.UnaryOperator;

/**
 * @author MrMicky
 */
public class RankupInventory extends FastInv {

    private static final Random RANDOM = new Random();

    private final FactionRankup plugin;
    private final IFaction faction;

    public RankupInventory(FactionRankup plugin, Player player) {
        super(54, ChatUtils.color(plugin.getConfig().getString("rankup-inventory.name")));
        this.plugin = plugin;
        this.faction = Compatibility.get().getFactionByPlayer(player);

        init();
    }

    private void init() {
        int level = plugin.getFactionLevel(faction);
        boolean maxLevel = level >= plugin.getLevelManager().getLevelCount() - 1;
        Level nextLevel = maxLevel ? null : plugin.getLevelManager().getLevel(level + 1);

        ConfigurationSection conf = plugin.getConfig().getConfigurationSection("rankup-inventory");
        ConfigurationSection item = conf.getConfigurationSection(maxLevel ? "rankup-item-max-level" : "rankup-item");

        UnaryOperator<String> replace = s -> s.replace("%faction_name%", faction.getName())
                .replace("%faction_level%", Integer.toString(level))
                .replace("%next_level_cost%", Double.toString(nextLevel == null ? 0 : nextLevel.getCost()))
                .replace("%money%", Double.toString(faction.getMoney()));

        setItem(13, getItem(Material.matchMaterial(item.getString("type")), item.getInt("data"), item.getString("name"),
                item.getStringList("lore"), false, replace), maxLevel ? null : e -> rankup((Player) e.getWhoClicked()));

        for (Level lvl : plugin.getLevelManager().getLevels()) {
            boolean unlocked = level >= lvl.getLevel();

            UnaryOperator<String> replaceItem = s -> s.replace("%ability%", lvl.getName())
                    .replace("%state%", plugin.getMessage(unlocked ? "unlocked" : "locked"))
                    .replace("%max_members%", Integer.toString(lvl.getMaxMembers()))
                    .replace("%level%", Integer.toString(lvl.getLevel()))
                    .replace("%money%", Double.toString(faction.getMoney()));

            List<String> lore = new ArrayList<>(conf.getStringList("levels-item.lore"));
            List<String> abilityInfos = new ArrayList<>(lvl.getDescription());

            int j = 0;
            for (String s : lore) {
                if (s.contains("%ability_info%")) {
                    if (!abilityInfos.isEmpty()) {
                        abilityInfos.set(0, s.replace("%ability_info%", abilityInfos.get(0)));
                        lore.remove(j);
                        lore.addAll(j, abilityInfos);
                    } else {
                        lore.remove(j);
                    }
                    break;
                }
                j++;
            }

            setItem(lvl.getLevel() + 26, getItem(lvl.getType(), lvl.getData(), conf.getString("levels-item.name"), lore, unlocked, replaceItem));
        }
    }

    private void rankup(Player player) {
        player.closeInventory();
        int lvl = plugin.getFactionLevel(faction);
        int nextLvl = lvl + 1;

        if (lvl >= plugin.getLevelManager().getLevelCount()) {
            player.sendMessage(plugin.getMessage("max-level"));
            player.closeInventory();
            return;
        }

        Level nextLevel = plugin.getLevelManager().getLevel(nextLvl);

        if (faction.hasMoney(nextLevel.getCost())) {
            player.closeInventory();

            FactionRankupEvent event = new FactionRankupEvent(faction, lvl, lvl + 1);

            Bukkit.getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }

            plugin.setFactionLevel(faction, nextLvl);
            faction.removeMoney(nextLevel.getCost());

            if (plugin.getConfig().getBoolean("rankup-fireworks")) {
                startFireworks(player);
            }

            String title = plugin.getMessage("rankup.title");
            String subtitle = plugin.getMessage("rankup.subtitle");
            Titles.sendTitle(player, title, subtitle, 5, 30, 5);

            String message = plugin.getMessage("rankup.chat");
            if (!message.isEmpty()) {
                String messageReplaced = replacePlaceholder(player, plugin.getMessage("rankup.chat"), faction.getName(), lvl);
                faction.getPlayers().forEach(ps -> ps.sendMessage(messageReplaced));
            }

            String bc = plugin.getMessage("rankup.broadcast");
            if (!bc.isEmpty()) {
                Bukkit.broadcastMessage(replacePlaceholder(player, bc, faction.getName(), lvl));
            }

            plugin.getLevelManager().getLevel(lvl + 1).getAbilities().stream()
                    .filter(ability -> ability.getClass() == CommandAbility.class)
                    .map(CommandAbility.class::cast)
                    .forEach(ability -> ability.dispatchCommand(faction, lvl + 1));
        } else {
            player.sendMessage(plugin.getMessage("no-money"));
            player.closeInventory();
        }
    }

    private ItemStack getItem(Material type, int data, String name, List<String> lore, boolean glow, UnaryOperator<String> replaces) {
        ItemStack item = new ItemStack(type == null ? Material.STONE : type, 1, (byte) data);
        ItemMeta meta = item.getItemMeta();

        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        if (FastReflection.optionalClass("org.bukkit.inventory.ItemFlag").isPresent()) {
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        }

        if (replaces != null) {
            name = replaces.apply(name);
            lore.replaceAll(replaces);
        }

        lore.replaceAll(ChatUtils::color);

        meta.setDisplayName(ChatColor.WHITE + ChatUtils.color(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private Color getRandomColor() {
        return Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255));
    }

    private String replacePlaceholder(Player player, String msg, String faction, int level) {
        return msg.replace("%new_level%", Integer.toString(level + 1))
                .replace("%old_level%", Integer.toString(level))
                .replace("%faction%", faction)
                .replace("%player%", player.getName());
    }

    private void startFireworks(Player p) {
        new BukkitRunnable() {

            int i = 8;

            @Override
            public void run() {
                Firework f = (Firework) p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                FireworkMeta fm = f.getFireworkMeta();
                fm.addEffects(FireworkEffect.builder().withColor(getRandomColor()).withFade(getRandomColor()).trail(true).build());
                fm.setPower(RANDOM.nextInt(3));
                f.setFireworkMeta(fm);

                if (i-- < 0) {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 10, 10);
    }
}
