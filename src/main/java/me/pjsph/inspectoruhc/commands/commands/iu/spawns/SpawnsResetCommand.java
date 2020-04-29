package me.pjsph.inspectoruhc.commands.commands.iu.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "reset")
public class SpawnsResetCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpawnsResetCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        p.getSpawnsManager().reset();
        sender.sendMessage("§aTous les points de spawn ont été supprimés.");
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu spawns reset §7pour supprimer tous les points de spawn.");
    }
}
