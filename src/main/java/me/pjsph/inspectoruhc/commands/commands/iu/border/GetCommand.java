package me.pjsph.inspectoruhc.commands.commands.iu.border;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "get")
@RequiredArgsConstructor
public class GetCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        int diameter = plugin.getBorderManager().getCurrentBorderDiameter();
        sender.sendMessage("§aLa taille actuelle de la map est de "+diameter+"×"+diameter);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu border get §7pour afficher la taille actuelle de la map.");
    }
}
