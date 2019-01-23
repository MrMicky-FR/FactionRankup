package fr.mrmicky.factionrankup.compatibility.implementations.factionsuuid;

import com.massivecraft.factions.P;
import com.massivecraft.factions.cmd.CmdHelp;
import com.massivecraft.factions.cmd.FCmdRoot;
import com.massivecraft.factions.cmd.FCommand;
import com.massivecraft.factions.zcore.util.TL;
import fr.mrmicky.factionrankup.FactionRankup;
import fr.mrmicky.factionrankup.compatibility.FactionRankupCommand;
import fr.mrmicky.factionrankup.compatibility.FactionType;
import fr.mrmicky.factionrankup.utils.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class FactionsUUIDCommand extends FCommand {

    public FactionsUUIDCommand() {
        aliases.addAll(FactionRankupCommand.getRankupAliases());
        setHelpShort(FactionRankupCommand.getRankupHelp());

        disableOnLock = true;

        senderMustBePlayer = true;
        senderMustBeMember = true;
        senderMustBeModerator = false;
        senderMustBeAdmin = false;

        FCmdRoot cmdBase;

        if (VersionUtils.getClass("com.massivecraft.factions.P").isPresent()) {
            cmdBase = P.p.cmdBase;
        } else {
            try {
                Plugin factionPlugin = Bukkit.getPluginManager().getPlugin(FactionType.FACTIONS_UUID.getPluginName());

                cmdBase = (FCmdRoot) factionPlugin.getClass().getDeclaredField("cmdBase").get(factionPlugin);
            } catch (Exception e) {
                throw new RuntimeException("Unsuported Factions plugin", e);
            }
        }

        cmdBase.addSubCommand(this);

        CmdHelp cmdHelp = cmdBase.cmdHelp;
        ArrayList<ArrayList<String>> commands = cmdHelp.helpPages;

        if (commands == null) {
            cmdHelp.updateHelp();
            commands = cmdHelp.helpPages;
        }

        int page = FactionRankup.getInstance().getConfig().getInt("f-rankup-page");

        if (page < 1 || page > commands.size()) {
            FactionRankup.getInstance().getLogger().warning("Help page set in config (" + page + ") is too big or too small ! Need to be between 1 and " + commands.size());
            page = 2;
        }

        commands.get(page - 1).add(getUseageTemplate(true));
    }

    @Override
    public TL getUsageTranslation() {
        return null;
    }

    @Override
    public void perform() {
        FactionRankupCommand.executeRankup(fme.getPlayer());
    }
}
