package me.pjsph.inspectoruhc.commands.commands;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.commands.commands.iu.MeCommand;
import me.pjsph.inspectoruhc.commands.commands.iu.SpectatorsCommand;
import me.pjsph.inspectoruhc.commands.commands.iu.StartCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@Command(name = "iu")
public class IUCommand extends AbstractCommand {
    private InspectorUHC plugin;

    public IUCommand(InspectorUHC plugin) {
        this.plugin = plugin;

        registerSubCommand(new StartCommand(plugin));
        registerSubCommand(new MeCommand(plugin));
        registerSubCommand(new SpectatorsCommand(plugin));
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
        return null;
    }
}
