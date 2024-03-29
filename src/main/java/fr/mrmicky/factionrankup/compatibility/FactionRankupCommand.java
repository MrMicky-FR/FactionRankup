package fr.mrmicky.factionrankup.compatibility;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.commands.CommandRankup;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public final class FactionRankupCommand {

    private FactionRankupCommand() {
        throw new UnsupportedOperationException();
    }

    public static void executeRankup(Player player) {
        CommandRankup.execute(FactionRankup.getInstance(), player);
    }

    public static String getRankupHelp() {
        return FactionRankup.getInstance().getMessage("f-rankup-help");
    }

    public static List<String> getRankupAliases() {
        List<String> aliases = FactionRankup.getInstance().getConfig().getStringList("f-rankup-aliases");
        return aliases.isEmpty() ? Collections.singletonList("rankup") : aliases;
    }
}
