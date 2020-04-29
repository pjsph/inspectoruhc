package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Command(name = "feedall")
@RequiredArgsConstructor
public class FeedAllCommand extends AbstractCommand {

    private final InspectorUHC p;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        int foodLevel = 20;

        if(args.length > 0) { // /iu feedall <foodLevel>
            try {
                foodLevel = Integer.valueOf(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage("Le niveau de nourriture doit être un nombre !");
                return;
            }
        }

        for(IUPlayer player : p.getGameManager().getOnlineAlivePlayers()) {
            player.getPlayer().setFoodLevel(foodLevel);
            player.getPlayer().setSaturation(20f);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu feedall [foodPoints=20] §7pour feed tous les joueurs.");
    }
}
