package me.pjsph.inspectoruhc.commands.commands.iu.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.World;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

@Command(name = "add")
public class SpawnsAddCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpawnsAddCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        World world;
        if(sender instanceof Player) {
            world = ((Player) sender).getWorld();
        } else if(sender instanceof BlockCommandSender) {
            world = ((BlockCommandSender) sender).getBlock().getWorld();
        } else {
            world = p.getServer().getWorlds().get(0);
        }

        if(args.length == 0) {
            if(!(sender instanceof Player)) {
                throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.ONLY_AS_A_PLAYER);
            } else {
                Player pl = (Player) sender;
                try {
                    p.getSpawnsManager().addSpawnPoint(pl.getLocation());
                    sender.sendMessage("§aPoint de spawn ajouté dans le monde " + world.getName() + " : " + String.valueOf(pl.getLocation().getBlockX() + ";" + String.valueOf(pl.getLocation().getBlockZ())));
                } catch(IllegalArgumentException e) {
                    sender.sendMessage("§cVous ne pouvez pas ajouter de point de spawn hors de la bordure.");
                } catch(RuntimeException e) {
                    sender.sendMessage("§cImpossible d'ajouter ce point de spawn : il se situe dans le nether.");
                }

            }
        } else if(args.length == 1) {
            sender.sendMessage("§cVous devez spécifier deux coordonnées.");
        } else {
            try {
                p.getSpawnsManager().addSpawnPoint(world, Double.valueOf(args[0]), Double.valueOf(args[1]));
                sender.sendMessage("§aPoint de spawn ajouté dans le monde " + world.getName() + " : " + args[0] + ";" + args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage("§cCe n'est pas un nombre !");
            } catch (IllegalArgumentException e) {
                sender.sendMessage("§cVous ne pouvez pas ajouter de point de spawn hors de la bordure.");
            } catch (RuntimeException e) {
                sender.sendMessage("§cImpossible d'ajouter ce point de spawn : il se situe dans le nether.");
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
