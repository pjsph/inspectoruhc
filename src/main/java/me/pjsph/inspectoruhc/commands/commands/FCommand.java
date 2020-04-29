package me.pjsph.inspectoruhc.commands.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.events.ActivateAuraEvent;
import me.pjsph.inspectoruhc.events.DesactivateAuraEvent;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.listeners.KitsListener;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Command(name = "f")
public class FCommand extends AbstractCommand {

    private InspectorUHC p;
    private HashMap<IUPlayer, Long> cooldown = new HashMap<>();

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

        IUPlayer executor = IUPlayer.thePlayer((Player) sender);
        if(Team.getTeamForPlayer(executor) != Team.THIEVES) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        if(executor.getCache().getBoolean("thief_aura")) {
            if(cooldown.get(executor) != null && cooldown.get(executor) < System.currentTimeMillis())
                p.getServer().getPluginManager().callEvent(new DesactivateAuraEvent(executor));
            else if(cooldown.get(executor) != null)
                executor.sendMessage("§cVous devez encore attendre "+Math.round((cooldown.get(executor) - System.currentTimeMillis()) / 1000)+" seconde(s) avant de pouvir désactiver votre Aura.");
        }
        else {
            cooldown.put(executor, System.currentTimeMillis() + 60000l);
            p.getServer().getPluginManager().callEvent(new ActivateAuraEvent(executor));
        }
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
