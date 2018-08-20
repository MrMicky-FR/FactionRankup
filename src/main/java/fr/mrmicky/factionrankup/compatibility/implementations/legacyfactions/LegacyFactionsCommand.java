package fr.mrmicky.factionrankup.compatibility.implementations.legacyfactions;

import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;
import net.redstoneore.legacyfactions.cmd.CmdFactionsHelp;
import net.redstoneore.legacyfactions.cmd.FCommand;

import java.util.ArrayList;

public class LegacyFactionsCommand extends FCommand {

    public LegacyFactionsCommand() {
        aliases.addAll(FactionRankupCommand.getRankupAliases());
        setHelpShort(FactionRankupCommand.getRankupHelp());

        this.disableOnLock = true;

        this.senderMustBePlayer = true;
        this.senderMustBeMember = true;
        this.senderMustBeModerator = false;
        this.senderMustBeColeader = false;
        this.senderMustBeAdmin = false;

        net.redstoneore.legacyfactions.cmd.CmdFactions.get().addSubCommand(this);

        ArrayList<ArrayList<String>> commands = CmdFactionsHelp.get().helpPages;

        if (commands == null) {
            CmdFactionsHelp.get().updateHelp();
            commands = CmdFactionsHelp.get().helpPages;
        }

        int page = FactionRankup.getInstance().config.getInt("f-rankup-page");

        if (page < 1 || page > commands.size()) {
            FactionRankup.getInstance().getLogger().warning("Help page set in config (" + page + ") is too big or too small ! Need to be between 1 and " + commands.size());
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
