package me.pjsph.inspectoruhc.teams;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;

public class TeamManager {
    private final InspectorUHC plugin;

    private HashMap<String, Team> teams = null;
    private HashMap<Player, Team> playersTeam = null;

    private int playersTeamInspectors = 0;
    private int playersTeamCrimes = 0;

    public TeamManager(InspectorUHC plugin) {
        this.plugin = plugin;

        this.teams = new HashMap<>();
        this.playersTeam = new HashMap<>();
    }

    public void initTeams() {
        this.addTeam("Inspecteurs", ChatColor.DARK_AQUA);
        this.addTeam("Criminels", ChatColor.RED);
    }

    private void addTeam(String teamName, ChatColor color) {
        Team team = new Team(teamName, color, plugin.getScoreboardManager().getScoreboard().registerNewTeam(teamName));

        teams.put(teamName, team);

        plugin.getLogger().log(Level.INFO, "" + teams.size());

        plugin.getScoreboardManager().matchInfo();
    }

    public Team getTeamOfPlayer(Player player) {
        return playersTeam.get(player);
    }

    public int getPlayersTeamInspectors() {
        return playersTeamInspectors;
    }

    public void setPlayersTeamInspectors(int playersTeamInspectors) {
        this.playersTeamInspectors = playersTeamInspectors;
    }

    public int getPlayersTeamCrimes() {
        return playersTeamCrimes;
    }

    public void setPlayersTeamCrimes(int playersTeamCrimes) {
        this.playersTeamCrimes = playersTeamCrimes;
    }

    public HashMap<String, Team> getTeams() {
        return teams;
    }

    public HashMap<Player, Team> getPlayersTeam() {
        return playersTeam;
    }
}
