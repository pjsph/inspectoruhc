package me.pjsph.inspectoruhc.commands.commands.iu;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Command(name = "spec")
public class SpectatorsCommand extends AbstractCommand {
    private InspectorUHC p;

    public SpectatorsCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        } else {
            String subCommand = args[0];

            if(subCommand.equalsIgnoreCase("add")) {
                if(args.length == 1) throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
                else {
                    Player player = Bukkit.getPlayer(args[1]);

                    if(player == null) {
                        sender.sendMessage("§cVous ne pouvez pas ajouter un joueur hors-ligne aux spectateurs.");
                        throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.UNKNOW, this);
                    }

                    p.getGameManager().addStartupSpectator(player);
                    sender.sendMessage("§aLe joueur " + player.getName() + " est maintenant un spectateur.");
                }
            } else if(subCommand.equalsIgnoreCase("remove")) {
                if(args.length == 1) throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);
                else {
                    Player player = Bukkit.getPlayer(args[1]);

                    if(!(p.getGameManager().getStartupSpectators().contains(player.getName()))) {
                        sender.sendMessage("§cVous ne pouvez pas supprimer un joueur qui n'est pas spectateur.");
                        throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.UNKNOW, this);
                    }

                    p.getGameManager().removeStartupSpectator(player);
                    sender.sendMessage("§aLe joueur " + player.getName() + " est maintenant dans la partie.");
                }
            } else if(subCommand.equalsIgnoreCase("list")) {
                HashSet<String> spectators = p.getGameManager().getStartupSpectators();
                if(spectators.size() == 0) {
                    sender.sendMessage("§aAucun joueur n'est spectateur.");
                } else {
                    sender.sendMessage("§a" + spectators.size() + " spectateur(s) enregistré(s)");

                    for(String spectator : spectators) {
                        sender.sendMessage(" §7- " + (Bukkit.getOnlinePlayers().stream().filter(player -> player.getName().equalsIgnoreCase(spectator)).findFirst().orElse(null) != null ? "§a" : "§c") + spectator);
                    }
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 1) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("add", "remove", "list")) {
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(rawSuggestion);
                }
            }

            return list;
        } else if(args.length == 2 && args[1].equalsIgnoreCase("remove")) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : p.getGameManager().getStartupSpectators()) {
                if(rawSuggestion.toLowerCase().startsWith(args[0].toLowerCase())) {
                    list.add(rawSuggestion);
                }
            }

            return list;
        } else {
            return null;
        }
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList(ChatColor.YELLOW + "----- Spectateurs -----",
                ChatColor.ITALIC + "/iu spec add <player>" + ChatColor.GRAY + " pour ajouter un spectateur.",
                ChatColor.ITALIC + "/iu spec remove <player>" + ChatColor.GRAY + " pour supprimer un spectateur.",
                ChatColor.ITALIC + "/iu spec list" + ChatColor.GRAY + " pour voir la liste des spectateurs."
        );
    }
}
