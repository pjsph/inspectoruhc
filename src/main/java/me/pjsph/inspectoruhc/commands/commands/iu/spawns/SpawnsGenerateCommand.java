package me.pjsph.inspectoruhc.commands.commands.iu.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.List;

@Command(name = "generate")
public class SpawnsGenerateCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpawnsGenerateCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        if(args.length != 0) {
            throw new CannotExecuteCommandException(CannotExecuteCommandException.Reason.NEED_DOC, this);
        }

        int size = p.getBorderManager().getCurrentBorderDiameter() - 25;
        int distanceBetweenToPoints = 250;
        World world = p.getServer().getWorlds().get(0);
        double xCenter = world.getSpawnLocation().getX();
        double zCenter = world.getSpawnLocation().getZ();

        int spawnsCount = p.getServer().getOnlinePlayers().size() - p.getGameManager().getStartupSpectators().size() - p.getSpawnsManager().getSpawnPoints().size();

        try {
            p.getSpawnsManager().generateSpawnPoints(world, spawnsCount, size, distanceBetweenToPoints, xCenter, zCenter);
        } catch(Exception e) {
            sender.sendMessage("§cIl y a trop de points de spawn répartis sur une trop petite surface. Réduisez le nombre de spawns.");
            return;
        }

        sender.sendMessage("§aLes points de spawn ont été correctement générés.");
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
