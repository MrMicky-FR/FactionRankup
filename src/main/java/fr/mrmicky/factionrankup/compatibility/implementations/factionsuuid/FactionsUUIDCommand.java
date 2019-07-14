package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.CommandContext;
import com.massivecraft.factions.cmd.CommandRequirements;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.zcore.util.TL;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;

public class FactionsUUIDCommand extends FCommand {

    public FactionsUUIDCommand() {
        aliases.addAll(FactionRankupCommand.getRankupAliases());
        setHelpShort(FactionRankupCommand.getRankupHelp());

        this.requirements = new CommandRequirements.Builder(Permission.HELP)
                .memberOnly()
                .build();

        FCmdRoot cmdBase = P.p.cmdBase;

        cmdBase.addSubCommand(this);
    }

    @Override
    public void perform(CommandContext commandContext) {
        FactionRankupCommand.executeRankup(commandContext.player);
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }
}
