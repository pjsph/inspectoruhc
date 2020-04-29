package me.pjsph.inspectoruhc.commands.commands.iu.spawns;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.commands.AbstractCommand;
import me.pjsph.inspectoruhc.commands.CannotExecuteCommandException;
import me.pjsph.inspectoruhc.commands.annotations.Command;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.List;

@Command(name = "list")
public class SpawnsListCommand extends AbstractCommand {

    private InspectorUHC p;

    public SpawnsListCommand(InspectorUHC p) {
        this.p = p;
    }

    @Override
    public void run(CommandSender sender, String[] args) throws CannotExecuteCommandException {
        List<Location> spawnPoints = p.getSpawnsManager().getSpawnPoints();

        if(spawnPoints.size() == 0) {
            sender.sendMessage("§aAucun point de spawn n'a été enregistré.");
        } else {
            sender.sendMessage("§aIl y a " + spawnPoints.size() + " point(s) de spawn enregistré(s)");

            /* 5 spawn points per line */
            for(int j = 0; j < Math.ceil((double) spawnPoints.size() / 5); j++) {
                StringBuilder line = new StringBuilder();

                for(int k = 0; k < 5; k++) {
                    if(spawnPoints.size() > j * 5 + k) {
                        line.append("§a" + spawnPoints.get(j * 5 + k).getBlockX() + "§2;§a" + spawnPoints.get(j * 5 + k).getBlockZ()).append(" ");
                    }
                }

                sender.sendMessage(line.toString());
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        return null;
    }

    @Override
    public List<String> help(CommandSender sender) {
        return Arrays.asList("§e/iu spawns list §7pour lister les points de spawn enregistrés.");
    }
}
