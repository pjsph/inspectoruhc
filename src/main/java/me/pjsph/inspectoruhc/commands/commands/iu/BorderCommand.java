package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.commands.commands.iu.border.CheckCommand;
import me.pjsph.inspectoruhc.commands.commands.iu.border.GetCommand;
import me.pjsph.inspectoruhc.commands.commands.iu.border.SetCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@Command(name = "border")
public class BorderCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    public BorderCommand(InspectorUHC plugin) {
        this.plugin = plugin;
        registerSubCommand(new CheckCommand(plugin));
        registerSubCommand(new GetCommand(plugin));
        registerSubCommand(new SetCommand(plugin));
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
