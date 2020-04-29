package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

@Command(name = "start")
public class StartCommand extends AbstractCommand {
    private InspectorUHC plugin;

    public StartCommand(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!(sender instanceof Player)) throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER, this);
        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        } else {
            sender.sendMessage(ChatColor.GREEN + "Vous avez lancé le jeu !");
            plugin.getGameManager().start((Player) sender);
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu start §7pour démarrer la partie.");
    }
}
