package fr.mrmicky.factionrankup.compatibility.implementations.factions;

import com.massivecraft.factions.cmd.CmdFactions;
import com.massivecraft.factions.cmd.FactionsCommand;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;

public class MFactionCommand extends FactionsCommand implements AutoCloseable {

    public MFactionCommand() {
        addAliases(FactionRankupCommand.getRankupAliases());
        setDesc(FactionRankupCommand.getRankupHelp());

        addRequirements(ReqHasFaction.get());

        CmdFactions.get().addChild(this);
    }

    @Override
    public void perform() {
        FactionRankupCommand.executeRankup(me);
    }

    @Override
    public void close() {
        CmdFactions.get().removeChild(this);
    }
}
