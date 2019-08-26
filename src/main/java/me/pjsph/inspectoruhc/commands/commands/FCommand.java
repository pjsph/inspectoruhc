package me.pjsph.inspectoruhc.commands.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.events.ActivateAuraEvent;
import me.pjsph.inspectoruhc.events.DesactivateAuraEvent;
import me.pjsph.inspectoruhc.listeners.KitsListener;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "f")
public class FCommand extends AbstractCommand {

    private InspectorUHC p;

    public FCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!(sender instanceof Player)) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER, this);
        }

        if(!p.getGameManager().isRolesActivated()) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        Player executor = (Player) sender;
        if(Team.getTeamForPlayer(executor) != Team.THIEVES) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        if(KitsListener.isAuraActivated(executor.getUniqueId()))
            p.getServer().getPluginManager().callEvent(new DesactivateAuraEvent(executor.getUniqueId()));
        else
            p.getServer().getPluginManager().callEvent(new ActivateAuraEvent(executor.getUniqueId()));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return null;
    }
}
