package me.pjsph.inspectoruhc.commands.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.events.SpyEvent;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "spy")
public class SpyCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpyCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!(sender instanceof Player)) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER, this);
        }

        if(!p.getGameManager().isKitsActivated()) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        Player executor = (Player) sender;
        if(Kit.getKit(executor.getUniqueId()) == null || Kit.getKit(executor.getUniqueId()).getKitType() != Kit.KIT_TYPES.SPY_GLASSES) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        if(args.length == 0) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target != null && target.isOnline()) {

            if(Math.round(target.getLocation().distance(executor.getLocation())) <= 15.0d)
                p.getServer().getPluginManager().callEvent(new SpyEvent(executor.getUniqueId(), target.getUniqueId()));

            else
                sender.sendMessage("§cCe joueur est trop loin de vous. Le joueur ciblé dans se trouver dans un rayon de 15 blocks autour de vous.");
        } else {
            sender.sendMessage("§cCe joueur est hors-ligne pour le moment. Réessayez plus tard.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(OfflinePlayer players : p.getGameManager().getAlivePlayers()) {
                if(players.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(players.getName());
                }
            }

            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList(
                "§o/spy <joueur> §7pour connaître l'équipe d'un joueur."
        );
    }
}
