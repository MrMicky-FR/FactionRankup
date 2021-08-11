package fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;
import net.redstoneore.legacyfactions.cmd.CmdFactions;
import net.redstoneore.legacyfactions.cmd.CmdFactionsHelp;
import net.redstoneore.legacyfactions.cmd.FCommand;

import java.util.ArrayList;
import java.util.List;

public class LegacyFactionsCommand extends FCommand {

    public LegacyFactionsCommand(FactionRankup plugin) {
        aliases.addAll(FactionRankupCommand.getRankupAliases());
        setHelpShort(FactionRankupCommand.getRankupHelp());

        disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeColeader = false;
        senderMustBeAdmin = false;

        CmdFactions.get().addSubCommand(this);

        List<ArrayList<String>> commands = CmdFactionsHelp.get().helpPages;

        if (commands == null) {
            CmdFactionsHelp.get().updateHelp();
            commands = CmdFactionsHelp.get().helpPages;
        }

        int page = plugin.getConfig().getInt("f-rankup-page");

        if (page < 1 || page > commands.size()) {
            plugin.getLogger().warning("Help page set in config (" + page + ") is too big or too small ! Need to be between 1 and " + commands.size());
            page = 2;
        }

        commands.get(page - 1).add(getUseageTemplate(true));
    }

    @Override
    public String getUsageTranslation() {
        return FactionRankupCommand.getRankupHelp();
    }

    @Override
    public void perform() {
        FactionRankupCommand.executeRankup(me);
    }
}
