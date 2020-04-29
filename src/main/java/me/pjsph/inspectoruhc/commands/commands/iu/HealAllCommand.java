package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Command(name = "healall")
@RequiredArgsConstructor
public class HealAllCommand extends AbstractCommand {

    private final InspectorUHC p;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        double health = 20D;

        if(args.length > 0) {
            try {
                health = Double.valueOf(args[0]);
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

        for(final IUPlayer player : p.getGameManager().getOnlineAlivePlayers()) {
            player.getPlayer().getPlayer().setHealth(health);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu healall [hearts=20] §7pour heal tous les joueurs.");
    }
}
