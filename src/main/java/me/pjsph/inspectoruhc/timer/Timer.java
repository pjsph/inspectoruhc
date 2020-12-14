package me.pjsph.inspectoruhc.timer;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.game.IUPlayer;
import me.pjsph.inspectoruhc.kits.Kit;
import me.pjsph.inspectoruhc.scoreboard.ScoreboardSign;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Timer extends BukkitRunnable {

    private InspectorUHC plugin;

    public Timer(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int seconds = plugin.getTimerManager().decSecondsLeft(), minutes = 0;

        if(seconds == -1) {
            plugin.getTimerManager().setSecondsLeft(59);
            minutes = plugin.getTimerManager().decMinutesLeft();

            if(minutes == -1) {
                plugin.getGameManager().shiftEpisode();
            } else if(minutes == 20 / 2) {
                /* Broadcast undersense message */
                if(plugin.getGameManager().isKitsActivated()) {
                    for (IUPlayer iup : Kit.getOwners(Kit.KIT_TYPES.UNDERSENSE)) {
                        if (!iup.isOnline()) continue;

                        List<String> near = new ArrayList<>();
                        for (IUPlayer thief : Team.THIEVES.getOnlinePlayers())
                            if(!plugin.getGameManager().isPlayerDead(thief))
                                if(thief.getPlayer().getLocation().getWorld() == iup.getPlayer().getLocation().getWorld())
                                    if (Math.round(thief.getPlayer().getLocation().distance(iup.getPlayer().getLocation())) <= 100.0d)
                                        near.add(thief.getPlayer().getName());
                        iup.sendMessage("");
                        iup.sendMessage("§3" + near.size() + " §cCriminel(s) §3est/sont à proximité (< 100 blocs).");
                        iup.sendMessage("");
                    }
                }
            }

            if(!plugin.getGameManager().isRolesActivated()) {

                minutes = plugin.getTimerManager().decMinutesRolesLeft();

                if(minutes == -1)
                    plugin.getGameManager().activateRoles();
            }

            if(!plugin.getGameManager().isKitsActivated()) {

                minutes = plugin.getTimerManager().decMinutesKitsLeft();

                if(minutes == -1)
                    plugin.getGameManager().activateKits();
            }

            if(!plugin.getBorderManager().isShrinking()) {
                minutes = plugin.getTimerManager().decMinutesBorderLeft();

                if(minutes == -1)
                    plugin.getBorderManager().startBorderReduction();
            }

            if(!plugin.getGameManager().isPvpActivated()) {
                minutes = plugin.getTimerManager().decMinutesPvpLeft();

                if(minutes == -1)
                    plugin.getGameManager().activatePvp();
            }
        }

        ScoreboardSign.getScoreboards().forEach(((player, scoreboardSign) -> scoreboardSign.update()));
    }
}
