package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.commands.commands.iu.spawns.*;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "spawns")
public class SpawnsCommand extends AbstractCommand {

    public SpawnsCommand(InspectorUHC plugin) {
        registerSubCommand(new SpawnsAddCommand(plugin));
        registerSubCommand(new SpawnsGenerateCommand(plugin));
        registerSubCommand(new SpawnsListCommand(plugin));
        registerSubCommand(new SpawnsRemoveCommand(plugin));
        registerSubCommand(new SpawnsResetCommand(plugin));
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu spawns §7pour afficher l'aide de cette commande.");
    }
}
