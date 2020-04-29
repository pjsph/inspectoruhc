package me.pjsph.inspectoruhc.task;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.game.IUPlayer;
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

            player.sendMessage(ChatColor.RED + "Vous êtes présentement hors de la prochaine bordure de " + Math.round(p.getBorderManager().getWarningSize() / 2) + "×" + Math.round(p.getBorderManager().getWarningSize() / 2) + " blocs.");
            player.sendMessage(ChatColor.RED + "Vous avez " + (int) distance + " bloc(s) à parcourir avant d'être dedans.");
            IUPlayer iup = IUPlayer.thePlayer(player);
            iup.sendActionBarMessage("§c§lRapprochez vous du centre !");
        }
    }
}
