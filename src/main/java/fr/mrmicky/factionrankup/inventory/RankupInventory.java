package fr.mrmicky.factionrankup.inventory;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.Compatibility;
import fr.mrmicky.factionrankup.compatibility.IFaction;
import fr.mrmicky.factionrankup.utils.FastInv;
import fr.mrmicky.factionrankup.utils.Messages;
import fr.mrmicky.factionrankup.utils.Titles;
import fr.mrmicky.factionrankup.utils.Version;
import org.bukkit.Bukkit;
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

public class RankupInventory extends FastInv {

    private FactionRankup main;
    private IFaction faction;
    private static final Random RANDOM = new Random();

    public RankupInventory(FactionRankup main, Player p) {
        super(54, Messages.color(main.config.getString("rankup-inventory.name")));
        this.main = main;
        this.faction = Compatibility.get().getFactionByPlayer(p);

        init(p);
    }

    private void init(Player p) {
        int level = main.getFactionLevel(faction);
        int nextLevelCost = getNextRankPrice(level);

        ConfigurationSection conf = main.config.getConfigurationSection("rankup-inventory");
        ConfigurationSection item = conf.getConfigurationSection(nextLevelCost > 0 ? "rankup-item" : "rankup-item-max-level");

        UnaryOperator<String> replace = s -> {
            s = s.replace("%faction_name%", faction.getName());
            s = s.replace("%faction_level%", String.valueOf(level));
            s = s.replace("%next_level_cost%", String.valueOf(nextLevelCost));
            return s.replace("%money%", String.valueOf(faction.getMoney()));
        };

        addItem(13, getItem(item.getString("type"), item.getInt("data"), item.getString("name"),
                item.getStringList("lore"), false, replace), e -> rankup(e.getPlayer()));

        ConfigurationSection levels = main.levels.getConfigurationSection("levels");
        for (int i = 1; i < 100; i++) {
            String levelString = String.valueOf(i);
            ConfigurationSection levelItem = levels.getConfigurationSection(String.valueOf(i));
            if (levelItem != null) {
                boolean unlocked = level >= i;

                UnaryOperator<String> replaceItem = s -> {
                    s = s.replace("%ability%", levelItem.getString("name"));
                    s = s.replace("%state%", main.messages.getString(unlocked ? "unlocked" : "locked"));
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
            p.sendMessage(Messages.getMessage(main.messages.getString("max-level")));
            p.closeInventory();
            return;
        }

        if (faction.hasMoney(price)) {
            main.setFactionLevel(faction, level + 1);
            faction.removeMoney(price);
            p.closeInventory();

            if (main.config.getBoolean("rankup-fireworks")) {
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

            Titles.sendTitle(p, 5, 30, 5, main.messages.getString("rankup.title"),
                    main.messages.getString("rankup.subtitle"));

            String message = main.messages.getString("rankup.chat");
            if (!message.isEmpty()) {
                faction.getPlayers().forEach(
                        ps -> ps.sendMessage(Messages.getMessage(replacePlaceholder(p, message, faction.getName(), level))));
            }

            String bc = main.messages.getString("rankup.broadcast");
            if (!bc.isEmpty()) {
                Bukkit.broadcastMessage(Messages.getMessage(replacePlaceholder(p, bc, faction.getName(), level)));
            }
        } else {
            p.sendMessage(Messages.getMessage(main.messages.getString("no-money")));
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

        lore.replaceAll(Messages::color);

        meta.setDisplayName(Messages.color("&f" + name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private int getNextRankPrice(int level) {
        ConfigurationSection section = main.levels.getConfigurationSection("levels." + (level + 1));

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
