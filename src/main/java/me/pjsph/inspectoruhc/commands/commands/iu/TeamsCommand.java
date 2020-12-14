package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command(name = "teams")
@RequiredArgsConstructor
public class TeamsCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(plugin.getGameManager().hasStarted()) {
            sender.sendMessage("§cLe jeu a déjà démarré.");
            return;
        }

        if(args.length < 2)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE);

        if(args[0].equalsIgnoreCase("set")) {
            if(args.length < 3)
                throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE);

            Player player = Bukkit.getPlayer(args[1]);
            if(player == null || !player.isOnline()) {
                sender.sendMessage("§cCe joueur est hors-ligne.");
                return;
            }

            Team team = Arrays.stream(Team.values()).filter(t -> t.getName().equalsIgnoreCase(args[2])).findFirst().orElse(null);
            if(team == null) {
                sender.sendMessage("§cCette équipe n'existe pas.");
                return;
            }

            if(Team.getTeamForPlayer(IUPlayer.thePlayer(player)) != null) {
                sender.sendMessage("§cCe joueur est déjà dans l'équipe "+Team.getTeamForPlayer(IUPlayer.thePlayer(player)).getColor()+Team.getTeamForPlayer(IUPlayer.thePlayer(player)).getName());
                return;
            }

            team.addPlayer(IUPlayer.thePlayer(player));
            sender.sendMessage("§a"+player.getName()+" a été ajouté à l'équipe "+team.getColor()+team.getName());
        } else if(args[0].equalsIgnoreCase("unset")) {
            Player player = Bukkit.getPlayer(args[1]);
            if (player == null || !player.isOnline()) {
                sender.sendMessage("§cCe joueur est hors-ligne.");
                return;
            }

            Team team = Team.getTeamForPlayer(IUPlayer.thePlayer(player));
            if(team == null) {
                sender.sendMessage("§cCe joueur n'était pas affecté à une équipe.");
                return;
            }

            team.removePlayer(IUPlayer.thePlayer(player));
            sender.sendMessage("§a"+player.getName()+" a été supprimé de l'équipe "+team.getColor()+team.getName());
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            ArrayList<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("set", "unset"))
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        } else if(args.length == 2) {
            ArrayList<String> list = new ArrayList<>();
            List<String> rawSuggestions = new ArrayList<>();
            rawSuggestions.addAll(Bukkit.getOnlinePlayers()
                    .stream()
                    .map(p -> p.getName())
                    .collect(Collectors.toList()));
            System.out.println(rawSuggestions);
            for(String suggestion : rawSuggestions)
                if(suggestion.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(suggestion);
            return list;
        } else if(args.length == 3 && args[0].equalsIgnoreCase("set")) {
            ArrayList<String> list = new ArrayList<>();
            List<String> rawSuggestions = new ArrayList<>();
            rawSuggestions.addAll(Stream.of(Team.values())
                    .map(t -> t.getName())
                    .collect(Collectors.toList()));
            System.out.println(rawSuggestions);
            for(String suggestion : rawSuggestions)
                if(suggestion.toLowerCase().startsWith(args[2].toLowerCase()))
                    list.add(suggestion);
            return list;
        }
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu teams set <player> <team> §7pour forcer l'ajout d'un joueur dans une équipe.",
                            "§e/iu teams unset <player> §7pour supprimer un joueur de son équipe.");
    }
}
