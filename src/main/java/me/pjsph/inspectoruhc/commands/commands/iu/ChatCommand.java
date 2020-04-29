package me.pjsph.inspectoruhc.commands.commands.iu;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "chat")
@RequiredArgsConstructor
public class ChatCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(!(sender instanceof Player))
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER, this);
        if(args.length != 1)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);

        Player player = (Player) sender;
        IUPlayer iup = IUPlayer.thePlayer(player);
        if(!player.isOp())
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NOT_ALLOWED, this);

        if(args[0].equalsIgnoreCase("spec")) {
            if(plugin.getGameManager().getSpectatorChat().getViewers().containsKey(iup)) {
                plugin.getGameManager().getSpectatorChat().leave(iup);
                iup.sendMessage("§aVous ne voyez plus le chat des morts.");
            } else {
                iup.joinChat(plugin.getGameManager().getSpectatorChat(), true);
                iup.sendMessage("§aVous voyez maintenant le chat des morts.");
            }
        } else
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("spec"))
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu chat spec §7pour voir/ne plus voir le chat des morts.");
    }
}
