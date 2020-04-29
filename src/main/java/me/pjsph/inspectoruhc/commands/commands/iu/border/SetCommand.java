package me.pjsph.inspectoruhc.commands.commands.iu.border;

import lombok.RequiredArgsConstructor;
import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import me.pjsph.inspectoruhc.tools.IUSound;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Command(name = "set")
@RequiredArgsConstructor
public class SetCommand extends AbstractCommand {

    private final InspectorUHC plugin;

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0)
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.BAD_USE, this);

        int diameter = 0;
        boolean force = false;
        int startsAfter = 0;

        try {
            diameter = Integer.valueOf(args[0]);
            if(diameter == 0) {
                sender.sendMessage("§cVous ne pouvez pas définir un diamètre de 0.");
                return;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("§c"+args[0]+" n'est pas un nombre.");
        }

        if(args.length >= 2 && args[1].equalsIgnoreCase("force"))
            force = true;
        else if(args.length >= 2) {
            try {
                startsAfter = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§c"+args[1]+" n'est pas un nombre.");
            }
        }

        if(!force && startsAfter == 0) {
            if(plugin.getBorderManager().getPlayersOutside(diameter).size() != 0) {
                sender.sendMessage("§cDes joueurs sont hors de la nouvelle bordure, la commande est annulée.");
                sender.sendMessage("§e/iu border set "+diameter+" force §7pour forcer la commande.");
                plugin.getBorderManager().sendCheckMessage(sender, diameter);
            } else {
                plugin.getBorderManager().setCurrentBorderDiameter(diameter);
                plugin.getGameManager().broadcastMessage("§dLa taille de la map est maintenant de "+diameter+"×"+diameter+".");
            }
        } else if(force && startsAfter == 0) {
            plugin.getBorderManager().setCurrentBorderDiameter(diameter);
            plugin.getGameManager().broadcastMessage("§dLa taille de la map est maintenant de "+diameter+"×"+diameter+".");
        } else if(!force && startsAfter != 0) {
            plugin.getBorderManager().setWarningSize(diameter);
            plugin.getGameManager().broadcastMessage("§dLa map rétrécira instantanément à "+diameter+"×"+diameter+" dans "+startsAfter+" secondes.");
            new BukkitRunnable() {
                int ticks = 8;
                @Override
                public void run() {
                    if(ticks-- % 2 == 0) {
                        if (ticks == -1) {
                            cancel();
                            return;
                        }
                        (new IUSound(Sound.NOTE_STICKS, 1f, 0.529732f)).broadcast();
                    }
                    else
                        (new IUSound(Sound.NOTE_STICKS, 1f, 0.667420f)).broadcast();
                }
            }.runTaskTimer(plugin, 0L, 7L);

            final int newDiameter = diameter;
            new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getBorderManager().cancelWarning();
                    plugin.getBorderManager().setCurrentBorderDiameter(newDiameter);
                    plugin.getGameManager().broadcastMessage("§dLa taille de la map est maintenant de "+newDiameter+"×"+newDiameter);
                }
            }.runTaskLater(plugin, startsAfter*20L);
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if(args.length == 2) {
            List<String> list = new ArrayList<>();
            for(String rawSuggestion : Arrays.asList("force", "300", "600"))
                if(rawSuggestion.toLowerCase().startsWith(args[1].toLowerCase()))
                    list.add(rawSuggestion);
            return list;
        }

        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu border set <diameter> [force|timeLeft] §7pour définir la taille de la map (§lforce§7 pour forcer la taille et §ltimeLeft§7 en secondes pour définir la taille après un certain temps).");
    }
}
