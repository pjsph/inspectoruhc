package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "heal")
@RequiredArgsConstructor
public class HealCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length < 1 || args.length > 2)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null || !player.isOnline()) {
            sender.sendMessage("§cLe joueur est hors-ligne.");
            return;
        }

        double health = 20D;

        if(args.length > 1) {
            try {
                health = Double.valueOf(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("La vie doit être un nombre !");
                return;
            }
        }

        if(health <= 0D) {
            sender.sendMessage("§cVous ne pouvez pas utiliser cette commande pour tuer un joueur.");
            return;
        } else if(health > 20D)
            health = 20D;

        player.setHealth(health);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            List<String> rawSuggestions = new ArrayList<>();
            rawSuggestions.addAll(plugin.getGameManager().getAlivePlayers()
                    .stream()
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
        return Arrays.asList("§e/iu heal <player> [hearts=20] §7pour heal un joueur.");
    }
}
