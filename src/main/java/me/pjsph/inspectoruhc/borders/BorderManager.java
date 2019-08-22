package me.pjsph.inspectoruhc.borders;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.task.BorderWarningTask;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public class BorderManager {
    private final boolean BORDER_SHRINKING = true;
    private final long BORDER_SHRINKING_STARTS_AFTER = 30 * 60;
    private final long BORDER_SHRINKING_DURATION = 90 * 60;
    private final double BORDER_START_SIZE = 1000;
    private final double BORDER_SHRINKING_FINAL_SIZE = 200;

    private final InspectorUHC p;

    private WorldBorder border = null;

    private Integer warningSize = 0;
    private BukkitRunnable warningTask = null;

    private Boolean warningFinalTimeEnabled = false;
    private String warningTimerName = null;
    private CommandSender warningSender = null;

    private MapShape mapShape = null;

    public BorderManager(InspectorUHC plugin) {
        p = plugin;

        warningTimerName = "Bordure";

        mapShape = MapShape.SQUARED;

        World world = p.getServer().getWorlds().get(0);

        border = WorldBorder.getInstance(world);

        border.setShape(mapShape);
        border.setCenter(world.getSpawnLocation());
        border.setDiameter(BORDER_START_SIZE);

        border.init();

        p.getLogger().log(Level.INFO, "World border: " + border.getClass().getSimpleName());
    }

    public WorldBorder getBorder() {
        return border;
    }

    public boolean isInsideBorder(Location location, double diameter) {
        return !location.getWorld().getEnvironment().equals(World.Environment.NORMAL) || mapShape.getShape().isInsideBorder(location, diameter, location.getWorld().getSpawnLocation());
    }

    public boolean isInsideBorder(Location location) {
        return this.isInsideBorder(location, getCurrentBorderDiameter());
    }

    public double getDistanceToBorder(Location location, double diameter) {
        return mapShape.getShape().getDistanceToBorder(location, diameter, location.getWorld().getSpawnLocation());
    }

    public Set<Player> getPlayersOutside(int diameter) {
        HashSet<Player> playersOutside = new HashSet<Player>();

        for(final Player player : p.getGameManager().getAllPlayers()) {
            if(!isInsideBorder(player.getLocation(), diameter)) {
                playersOutside.add(player);
            }
        }

        return playersOutside;
    }

    public Integer getWarningSize() {
        return warningSize;
    }

    public Boolean getWarningFinalTimeEnabled() {
        return warningFinalTimeEnabled;
    }

    public CommandSender getWarningSender() {
        return warningSender;
    }

    public void setWarningSize(int diameter, int timeLeft, CommandSender sender) {
        // TODO

        this.warningSize = diameter;

        if(timeLeft != 0) {

        }

        if(sender != null) {
            this.warningSender = sender;
        }

        warningTask = new BorderWarningTask();
        warningTask.runTaskTimer(p, 20L, 20L * 90);
    }

    public void setWarningSize(int diameter) {
        setWarningSize(diameter, 0, null);
    }

    public void cancelWarning() {
        if(warningTask != null) {
            try {
                warningTask.cancel();
            } catch(IllegalStateException ignored) {}
        }
    }

    public int getCurrentBorderDiameter() {
        return (int) border.getDiameter();
    }

    public void setCurrentBorderDiameter(int diameter) {
        cancelWarning();

        border.setDiameter(diameter);
    }

    public void sendCheckMessage(CommandSender to, int diameter) {
        Set<Player> playersOutside = getPlayersOutside(diameter);

        if(playersOutside.size() == 0) {
            to.sendMessage("Tous les joueurs sont dans la barrière donnée.");
        } else {
            to.sendMessage("Il y a " + String.valueOf(playersOutside.size()) + " joueur(s) en dehors de cette barrière.");
            for(Player player : getPlayersOutside(diameter)) {
                double distance = getDistanceToBorder(player.getLocation(), diameter);
                if(distance > 150) {
                    to.sendMessage(ChatColor.GRAY + " - " + ChatColor.RED + player.getName() + " est loin de la barrière.");
                } else if(distance > 25) {
                    to.sendMessage(ChatColor.GRAY + " - " + ChatColor.YELLOW + player.getName() + " est plus ou moins proche de la barrière.");
                } else {
                    to.sendMessage(ChatColor.GRAY + " - " + ChatColor.GREEN + player.getName() + " est très proche de la barrière.");
                }
            }
        }
    }

    public void scheduleBorderReduction() {
        if(BORDER_SHRINKING) {
            Bukkit.getScheduler().runTaskLater(p, () -> {
                Integer secondsPerBlock = (int) Math.rint(BORDER_SHRINKING_DURATION / (border.getDiameter() - BORDER_SHRINKING_FINAL_SIZE)) * 2;

                border.setDiameter(BORDER_SHRINKING_FINAL_SIZE, BORDER_SHRINKING_DURATION);

                Bukkit.broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "La bordure commence à rétrécir...");
                Bukkit.broadcastMessage(ChatColor.RED + "Elle rétrécira d'un block toutes les " + secondsPerBlock + " secondes jusqu'à " + (int) BORDER_SHRINKING_FINAL_SIZE + " blocks de diamètre.");
            }, BORDER_SHRINKING_STARTS_AFTER * 20L);
        }
    }
}
