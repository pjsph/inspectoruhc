package me.pjsph.inspectoruhc.commands.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.events.SpyEvent;
import me.pjsph.inspectoruhc.game.IUPlayer;
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

        if(!p.getGameManager().isRolesActivated()) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        IUPlayer executor = IUPlayer.thePlayer((Player) sender);
        if(Team.getTeamForPlayer(executor) == Team.INSPECTORS && (Kit.getKit(executor) == null || Kit.getKit(executor).getKitType() != Kit.KIT_TYPES.SPY_GLASSES || !p.getGameManager().isKitsActivated())) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);
        }

        if(args.length == 0) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        }

        Player targetP = Bukkit.getPlayer(args[0]);
        if(targetP != null && targetP.isOnline()) {
            IUPlayer target = IUPlayer.thePlayer(targetP);
            if(Math.round(target.getPlayer().getLocation().distance(executor.getPlayer().getLocation())) <= 15.0d)
                p.getServer().getPluginManager().callEvent(new SpyEvent(executor, target));

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
            for(IUPlayer players : p.getGameManager().getAlivePlayers())
                if(Bukkit.getOfflinePlayer(players.getUuid()).getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add((Bukkit.getOfflinePlayer(players.getUuid()).getName()));

            if(!(sender instanceof Player)) return list; // Should never happen

            Player player = (Player) sender;
            if(list.contains(player.getName())) list.remove(player.getName());

            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList(
                "§e/spy <joueur> §7pour connaître l'équipe d'un joueur."
        );
    }
}
