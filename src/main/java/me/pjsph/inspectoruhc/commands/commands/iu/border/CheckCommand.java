package me.pjsph.inspectoruhc.commands.commands.iu.border;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "check")
@RequiredArgsConstructor
public class CheckCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);

        try {
            plugin.getBorderManager().sendCheckMessage(sender, Integer.valueOf(args[0]));
        } catch (NumberFormatException e) {
            sender.sendMessage("§c"+args[0]+" n'est pas un nombre.");
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu border check <diameter> §7pour afficher la liste des joueurs en dehors du diamètre précisé.");
    }
}
