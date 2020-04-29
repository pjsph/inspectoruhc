package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "revive")
@RequiredArgsConstructor
public class ReviveCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length != 1)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
        if(!plugin.getGameManager().hasStarted()) {
            sender.sendMessage("§cLe jeu n'a pas encore démarré !");
            return;
        }

        boolean success = plugin.getGameManager().resurrect(args[0]);

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null || !player.isOnline()) {
            if(!success)
                sender.sendMessage("§cCe joueur ne joue pas ou n'est pas mort !");
            else
                sender.sendMessage("§aLe joueur sera ressuscité lorsque qu'il se reconnectera.");
        } else {
            if(!success)
                sender.sendMessage("§cCe joueur n'est pas mort ou n'est pas dans la partie !");
            else {
                sender.sendMessage("§aLe joueur a été ressuscité.");
                player.sendMessage("§aVous avez été ressuscité.");
                player.teleport(plugin.getGameManager().getDeathLocations().remove(IUPlayer.thePlayer(player)));
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            List<String> rawSuggestions = new ArrayList<>();
            rawSuggestions.addAll(plugin.getGameManager().getPlayers()
                    .stream()
                    .filter(iup -> !plugin.getGameManager().getAlivePlayers().contains(iup))
                    .map(iup -> Bukkit.getOfflinePlayer(iup.getUuid()).getName())
                    .collect(Collectors.toList()));
            for(String suggestion : rawSuggestions)
                if(suggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(suggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu revive <player> §7pour ressusciter un joueur (ou l'ajouter à la partie).");
    }
}
