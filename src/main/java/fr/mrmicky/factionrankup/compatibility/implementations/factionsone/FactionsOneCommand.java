package fr.mrmicky.factionrankup.compatibility.implementations.factionsone;

import com.factionsone.factions.P;
import com.factionsone.factions.cmd.FCommand;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;

/**
 * @author MrMicky
 */
public class FactionsOneCommand extends FCommand {

    public FactionsOneCommand() {
        aliases.addAll(FactionRankupCommand.getRankupAliases());
        setHelpShort(FactionRankupCommand.getRankupHelp());

        disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeOfficer = false;
        senderMustBeLeader = false;

        P.p.cmdBase.addSubCommand(this);
    }

    @Override
    public void perform() {
        FactionRankupCommand.executeRankup(fme.getPlayer());
    }
}
