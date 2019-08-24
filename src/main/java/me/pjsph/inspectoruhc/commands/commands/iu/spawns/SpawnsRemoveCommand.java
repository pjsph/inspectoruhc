package me.pjsph.inspectoruhc.commands.commands.iu.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "remove")
public class SpawnsRemoveCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpawnsRemoveCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER);
            } else {
                Player pl = (Player) sender;
                p.getSpawnsManager().removeSpawnPoint(pl.getLocation(), false);
                sender.sendMessage("§aLe point de spawn " + String.valueOf(pl.getLocation().getBlockX()) + ";" + String.valueOf(pl.getLocation().getBlockZ() + " a été supprimé."));
            }
        } else if(args.length == 1) {
            sender.sendMessage("§cVous devez spécifier deux coordonnées.");
        } else {
            try {
                World world;
                if(sender instanceof Player) {
                    world = ((Player) sender).getWorld();
                } else {
                    world = p.getServer().getWorlds().get(0);
                }

                p.getSpawnsManager().removeSpawnPoint(new Location(world, Double.valueOf(args[0]), 0, Double.valueOf(args[1])), false);
                sender.sendMessage("§aLe point de spawn " + args[0] + ";" + args[1] + " dans le monde " + world.getName() + " a été supprimé.");
            } catch (NumberFormatException e) {
                sender.sendMessage("§cCe n'est pas un nombre !");
            }
        }
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
