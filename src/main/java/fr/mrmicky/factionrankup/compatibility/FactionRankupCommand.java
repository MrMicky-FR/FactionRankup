package fr.mrmicky.factionrankup.compatibility;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.commands.CommandRankup;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class FactionRankupCommand {

    public static void executeRankup(Player p) {
        CommandRankup.execute(FactionRankup.getInstance(), p);
    }

    public static String getRankupHelp() {
        return FactionRankup.getInstance().messages.getString("f-rankup-help");
    }

    public static List<String> getRankupAliases() {
        List<String> aliases = FactionRankup.getInstance().config.getStringList("f-rankup-aliases");
        return aliases.isEmpty() ? Collections.singletonList("rankup") : aliases;
    }
}
