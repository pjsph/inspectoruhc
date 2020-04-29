package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "skip")
@RequiredArgsConstructor
public class SkipCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length != 1)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
        if(!plugin.getGameManager().hasStarted()) {
            sender.sendMessage("§cLa partie n'a pas encore démarré.");
            return;
        }

        if(args[0].equalsIgnoreCase("pvp")) {
            if(plugin.getGameManager().isPvpActivated()) {
                sender.sendMessage("§cLe pvp est déjà activé.");
                return;
            }
            plugin.getGameManager().activatePvp();
        } else if(args[0].equalsIgnoreCase("roles")) {
            if(plugin.getGameManager().isRolesActivated()) {
                sender.sendMessage("§cLes équipes ont déjà été annoncées.");
                return;
            }
            plugin.getGameManager().activateRoles();
        } else if(args[0].equalsIgnoreCase("kits")) {
            if(plugin.getGameManager().isKitsActivated()) {
                sender.sendMessage("§cLes kits ont déjà été annoncés.");
                return;
            }
            if(!plugin.getGameManager().isRolesActivated())
                plugin.getGameManager().activateRoles();
            plugin.getGameManager().activateKits();
        } else if(args[0].equalsIgnoreCase("border")) {
            if(plugin.getBorderManager().isShrinking()) {
                sender.sendMessage("§cLa bordure rétrécit déjà.");
                return;
            }
            plugin.getBorderManager().startBorderReduction();
        } else
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("pvp", "roles", "kits", "border"))
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu skip <roles|kits|border> §7pour passer un stade du jeu (équipes, bordure, etc.).");
    }
}
