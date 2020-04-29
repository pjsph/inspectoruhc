package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.events.GameEndsEvent;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "end")
@RequiredArgsConstructor
public class EndCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!plugin.getGameManager().hasStarted()) {
            sender.sendMessage("§cLa partie n'a pas encore démarré.");
            return;
        }

        if(args.length != 1) { // soft
            List<Team> aliveAndOnline = plugin.getGameManager().getAliveTeams()
                    .stream()
                    .filter(t -> {
                        for(IUPlayer player : t.getOnlinePlayers())
                            if(!plugin.getGameManager().isPlayerDead(player))
                                return true;
                        return false;
                    }).collect(Collectors.toList());
            if(aliveAndOnline.size() == 1) { // If many teams are alive but only one is online
                Team winner = aliveAndOnline.iterator().next();
                sender.sendMessage("§aVous arrêtez la partie, l'équipe avec au moins un joueur en ligne gagne.");
                plugin.getServer().getPluginManager().callEvent(new GameEndsEvent(winner, true));
                return;
            } else if(plugin.getGameManager().getAliveTeams().size() == 1) { // If one team is alive and not online (should not be called because end of the game is automatic)
                Team winner = plugin.getGameManager().getAliveTeams().iterator().next();
                sender.sendMessage("§aVous arrêtez la partie, la seule équipe en vie gagne.");
                plugin.getServer().getPluginManager().callEvent(new GameEndsEvent(winner));
                return;
            }
            sender.sendMessage("§cPlusieurs (ou aucune) équipes sont encore en vie !");
            sender.sendMessage("§e/iu end force §7pour forcer la fin de la partie.");
        } else if(args[0].equalsIgnoreCase("force")) { // force
            if(plugin.getGameManager().getAliveTeams().size() == 0) {
                plugin.getServer().getPluginManager().callEvent(new GameEndsEvent(Team.INSPECTORS, true));
            } else {
                Team chosen = plugin.getGameManager().getAliveTeams().iterator().next();
                plugin.getServer().getPluginManager().callEvent(new GameEndsEvent(chosen, true));
            }
            plugin.getGameManager().broadcastMessage("§aL'arrêt de la partie a été forcé.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("force"))
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu end [force] §7pour arrêter la partie.");
    }
}
