package fr.mrmicky.factionrankup.inventory;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.utils.ChatUtils;
import fr.mrmicky.factionrankup.utils.FastInv;
import fr.mrmicky.factionrankup.utils.Titles;
import fr.mrmicky.factionrankup.utils.Version;
import org.bukkit.*;
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

public class RankupInventory extends FastInv {

    private FactionRankup main;
    private IFaction faction;
    private static final Random RANDOM = new Random();

    public RankupInventory(FactionRankup main, Player p) {
        super(54, ChatUtils.color(main.getConfig().getString("rankup-inventory.name")));
        this.main = main;
        this.faction = Compatibility.get().getFactionByPlayer(p);

        init();
    }

    private void init() {
        int level = main.getFactionLevel(faction);
        int nextLevelCost = getNextRankPrice(level);

        ConfigurationSection conf = main.getConfig().getConfigurationSection("rankup-inventory");
        ConfigurationSection item = conf.getConfigurationSection(nextLevelCost > 0 ? "rankup-item" : "rankup-item-max-level");

        UnaryOperator<String> replace = s -> {
            s = s.replace("%faction_name%", faction.getName());
            s = s.replace("%faction_level%", String.valueOf(level));
            s = s.replace("%next_level_cost%", String.valueOf(nextLevelCost));
            return s.replace("%money%", String.valueOf(faction.getMoney()));
        };

        addItem(13, getItem(item.getString("type"), item.getInt("data"), item.getString("name"),
                item.getStringList("lore"), false, replace), e -> rankup(e.getPlayer()));

        ConfigurationSection levels = main.getLevelsConfig().getConfigurationSection("levels");
        for (int i = 1; i < 100; i++) {
            String levelString = String.valueOf(i);
            ConfigurationSection levelItem = levels.getConfigurationSection(String.valueOf(i));
            if (levelItem != null) {
                boolean unlocked = level >= i;

                UnaryOperator<String> replaceItem = s -> {
                    s = s.replace("%ability%", levelItem.getString("name"));
                    s = s.replace("%state%", main.getMessage(unlocked ? "unlocked" : "locked"));
                    s = s.replace("%max_members%", String.valueOf(levelItem.getInt("max-members")));
                    s = s.replace("%ability%", levelItem.getString("name"));
                    s = s.replace("%level%", levelString);
                    return s.replace("%money%", String.valueOf(faction.getMoney()));
                };

                List<String> lore = new ArrayList<>(conf.getStringList("levels-item.lore"));
                List<String> abilityInfos = levelItem.getStringList("item.description");

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

                addItem(i + 26, getItem(levelItem.getString("item.type"), levelItem.getInt("item.data"),
                        conf.getString("levels-item.name"), lore, unlocked, replaceItem));
            }
        }
    }

    private void rankup(Player p) {
        p.closeInventory();
        int level = main.getFactionLevel(faction);
        int price = getNextRankPrice(level);

        if (price == -1) {
            p.sendMessage(main.getMessage("max-level"));
            p.closeInventory();
            return;
        }

        if (faction.hasMoney(price)) {
            main.setFactionLevel(faction, level + 1);
            faction.removeMoney(price);
            p.closeInventory();

            if (main.getConfig().getBoolean("rankup-fireworks")) {
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
                }.runTaskTimer(main, 10, 10);
            }

            String title = main.getMessage("rankup.title");
            String subtitle = main.getMessage("rankup.subtitle");
            Titles.sendTitle(p, title, subtitle, 5, 30, 5);

            String message = main.getMessage("rankup.chat");
            if (!message.isEmpty()) {
                String messageReplaced = replacePlaceholder(p, main.getMessage("rankup.chat"), faction.getName(), level);
                faction.getPlayers().forEach(ps -> ps.sendMessage(messageReplaced));
            }

            String bc = main.getMessage("rankup.broadcast");
            if (!bc.isEmpty()) {
                Bukkit.broadcastMessage(replacePlaceholder(p, bc, faction.getName(), level));
            }
        } else {
            p.sendMessage(main.getMessage("no-money"));
            p.closeInventory();
        }
    }

    private ItemStack getItem(String mat, int data, String name, List<String> lore, boolean glow, UnaryOperator<String> replaces) {
        Material type = Material.matchMaterial(mat);
        ItemStack item = new ItemStack(type == null ? Material.STONE : type, 1, (byte) data);
        ItemMeta meta = item.getItemMeta();

        if (glow) {
            meta.addEnchant(Enchantment.DURABILITY, 1, true);
        }

        if (Version.V1_8_R1.isVersionOrHigher()) {
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

    private int getNextRankPrice(int level) {
        ConfigurationSection section = main.getLevelsConfig().getConfigurationSection("levels." + (level + 1));

        return section != null ? section.getInt("cost") : -1;
    }

    private Color getRandomColor() {
        return Color.fromRGB(RANDOM.nextInt(255), RANDOM.nextInt(255), RANDOM.nextInt(255));
    }

    private String replacePlaceholder(Player player, String msg, String faction, int level) {
        msg = msg.replace("%new_level%", String.valueOf(level + 1));
        msg = msg.replace("%old_level%", String.valueOf(level));
        msg = msg.replace("%faction%", faction);
        msg = msg.replace("%player%", player.getName());
        return msg;
    }
}
