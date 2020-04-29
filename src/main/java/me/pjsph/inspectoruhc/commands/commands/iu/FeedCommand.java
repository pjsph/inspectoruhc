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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "feed")
@RequiredArgsConstructor
public class FeedCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length < 1)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);

        final Player p = Bukkit.getPlayer(args[0]);
        if(p == null || !p.isOnline()) {
            sender.sendMessage("§cLe joueur est hors-ligne.");
            return;
        }

        int foodLevel = 20;

        if(args.length > 1) { // /iu feed <player> <foodLevel>
            try {
                foodLevel = Integer.valueOf(args[1]);
            } catch(NumberFormatException e) {
                sender.sendMessage("Le niveau de nourriture doit être un nombre !");
                return;
            }
        }

        p.setFoodLevel(foodLevel);
        p.setSaturation(20f);
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
        return Arrays.asList("§e/iu feed <player> [foodPoints=20] §7pour feed un joueur.");
    }
}
