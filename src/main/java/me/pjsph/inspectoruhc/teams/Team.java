package me.pjsph.inspectoruhc.teams;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class Team {
    private String name = null;
    private ArrayList<Player> players = new ArrayList<>();
    private ChatColor color = null;
    private org.bukkit.scoreboard.Team scoreboardTeam = null;

    public Team(String name, ChatColor color, org.bukkit.scoreboard.Team scoreboardTeam) {
        this.name = name;
        this.color = color;
        this.scoreboardTeam= scoreboardTeam;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        this.scoreboardTeam.addEntry(player.getName());
        player.setPlayerListName(this.color + " " + player.getDisplayName());
        players.add(player);
        InspectorUHC.get().getTeamManager().getPlayersTeam().put(player, this);
    }

    public void removePlayer(Player player) {
        this.scoreboardTeam.removeEntry(player.getName());
        player.setPlayerListName(player.getDisplayName());
        players.remove(player);
        InspectorUHC.get().getTeamManager().getPlayersTeam().remove(player);
    }

    public int countPlayer() {
        return players.size();
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public org.bukkit.scoreboard.Team getScoreboardTeam() {
        return scoreboardTeam;
    }

    public void setScoreboardTeam(org.bukkit.scoreboard.Team scoreboardTeam) {
        this.scoreboardTeam = scoreboardTeam;
    }
}
