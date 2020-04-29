package me.pjsph.inspectoruhc.commands.commands.iu.scenarios;

import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.scenarios.Scenario;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Command(name = "toggle")
public class ToggleCommand extends AbstractCommand {

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);

        for(Scenario.Scenarios scenario : Scenario.Scenarios.values()) {
            if(args[0].equalsIgnoreCase(scenario.getName())) {
                try {
                    Class<? extends Scenario> scenarioClass = scenario.getClazz();
                    scenarioClass.newInstance().activeScenario();
                    sender.sendMessage("§6Le scénario §7§l"+scenario.getName()+" §6est maintenant "+(scenario.isEnabled() ? "§aactif" : "§cinactif")+"§6.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.stream(Scenario.Scenarios.values()).map(Scenario.Scenarios::getName).collect(Collectors.toList()))
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu scenarios toggle <scenario> §7pour activer/désactiver un scénario.");
    }
}
