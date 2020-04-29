package me.pjsph.inspectoruhc.commands.commands.iu.scenarios;

import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.scenarios.Scenario;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "list")
public class ListCommand extends AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        sender.sendMessage("§6Liste des scénarios :");
        for(Scenario.Scenarios scenario : Scenario.Scenarios.values())
            sender.sendMessage("§7 - §6"+scenario.getName() +(scenario.isEnabled() ? " §aActif" : " §cInactif"));
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu scenarios list §7pour lister l'état des scénarios.");
    }
}
