package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.commands.commands.iu.scenarios.ListCommand;
import me.pjsph.inspectoruhc.commands.commands.iu.scenarios.ToggleCommand;
import org.bukkit.command.CommandSender;

import java.util.List;

@Command(name = "scenarios")
public class ScenariosCommand extends AbstractCommand {

    public ScenariosCommand() {
        registerSubCommand(new ListCommand());
        registerSubCommand(new ToggleCommand());
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
