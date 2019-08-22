package me.pjsph.inspectoruhc.scoreboard;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.teams.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {
    private final InspectorUHC plugin;
    private final Scoreboard sb;

    private final String scoreboardTitle = "§3InspectorUHC";

    public ScoreboardManager(InspectorUHC plugin) {
        this.plugin = plugin;
        this.sb = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
    }

    public void matchInfo() {
        Scoreboard scoreboard = InspectorUHC.get().getServer().getScoreboardManager().getMainScoreboard();

        if(scoreboard != null && scoreboard.getObjective(scoreboardTitle) != null) {
            scoreboard.getObjective(scoreboardTitle).unregister();
        }

        Objective ob = scoreboard.registerNewObjective(scoreboardTitle, "IUObjective");
        ob.setDisplaySlot(DisplaySlot.SIDEBAR);
        String ml = displayTime(plugin.getTimerManager().getMinutesLeft());
        String sl = displayTime(plugin.getTimerManager().getSecondsLeft());

        String txt = "Episode ";

        ob.getScore("  ").setScore(-1);
        ob.getScore(ChatColor.GRAY + txt + ChatColor.RESET + plugin.getTimerManager().getEpisode()).setScore(-2);
        ob.getScore("" + plugin.getGameManager().getAlivePlayers().size() + " " + ChatColor.GRAY + "Joueurs").setScore(-3);
        ob.getScore("" + Team.getTeams().size() + ChatColor.GRAY + " Equipes").setScore(-4);
        ob.getScore("").setScore(-5);
        ob.getScore("" + ml + ChatColor.GRAY + ":" + ChatColor.RESET + sl).setScore(-6);

        if(!plugin.getGameManager().isRolesActivated()) {
            String tml = displayTime(plugin.getTimerManager().getMinutesRolesLeft());

            ob.getScore(" ").setScore(-7);
            ob.getScore(ChatColor.RED + "Equipes" + ChatColor.GRAY + " dans :").setScore(-8);
            ob.getScore("" + tml + ChatColor.GRAY + ":" + ChatColor.RESET + sl + " ").setScore(-9);
        } else if(plugin.getGameManager().isRolesActivated() && !plugin.getGameManager().isKitsActivated()) {
            String tml = displayTime(plugin.getTimerManager().getMinutesKitsLeft());

            ob.getScore(" ").setScore(-7);
            ob.getScore(ChatColor.RED + "Kits" + ChatColor.GRAY + " dans :").setScore(-8);
            ob.getScore("" + tml + ChatColor.GRAY + ":" + ChatColor.RESET + sl + " ").setScore(-9);
        }
    }

    private String displayTime(int n) {
        String txt = "" + n;
        if(n >= 0 && n <= 9) txt = "0" + n;

        return txt;
    }

    public Scoreboard getScoreboard() {
        return sb;
    }
}
