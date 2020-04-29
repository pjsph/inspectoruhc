package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(name = "shift")
public class ShiftCommand extends AbstractCommand {

    private InspectorUHC p;

    public ShiftCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(p.getGameManager().hasStarted()) {
            if(sender instanceof Player) {
                p.getGameManager().shiftEpisode(sender.getName());
            } else {
                p.getGameManager().shiftEpisode("la console");
            }
        } else {
            sender.sendMessage("§cImpossible de passer l'épisode courant car le jeu n'a pas commencé.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu shift §7pour démarrer l'épisode suivant.");
    }
}
