package me.pjsph.inspectoruhc.task;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BorderWarningTask extends BukkitRunnable {

    private final InspectorUHC p;

    public BorderWarningTask() {
        this.p = InspectorUHC.get();
    }

    @Override
    public void run() {
        for(Player player : p.getBorderManager().getPlayersOutside(p.getBorderManager().getWarningSize())) {
            double distance = p.getBorderManager().getDistanceToBorder(player.getLocation(), p.getBorderManager().getWarningSize());

            player.sendMessage(ChatColor.RED + "Vous êtes présentement hors de la prochaine bordure de " + p.getBorderManager().getWarningSize() + "×" + p.getBorderManager().getWarningSize() + " blocks.");
            player.sendMessage(ChatColor.RED + "Vous avez " + (int) distance + " block(s) à parcourir avant d'être dedans.");
        }
    }
}
