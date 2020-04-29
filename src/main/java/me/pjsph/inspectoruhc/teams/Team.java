package me.pjsph.inspectoruhc.teams;

import me.pjsph.inspectoruhc.game.IUPlayer;
import net.minecraft.server.v1_8_R3.IUpdatePlayerListBox;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Team {

    INSPECTORS("Inspecteurs", ChatColor.DARK_AQUA),
    THIEVES("Criminels", ChatColor.RED);

    private String name;
    private ChatColor color;

    private HashSet<IUPlayer> players = new HashSet<>();

    Team(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ChatColor getColor() {
        return color;
    }

    public Set<IUPlayer> getPlayers() {
        return players;
    }

    public Set<IUPlayer> getOnlinePlayers() {
        HashSet<IUPlayer> playersList = new HashSet<>();

        for(IUPlayer iup : players)
            if(iup.isOnline())
                playersList.add(iup);

        return playersList;
    }

    public int getSize() {
        return players.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void addPlayer(IUPlayer player) {
        Validate.notNull(player, "The player cannot be null.");

        Team.removePlayerFromTeam(player);

        players.add(player);
    }

    public void removePlayer(IUPlayer player) {
        Validate.notNull(player, "The player cannot be null.");

        players.remove(player);
    }

    public boolean containsPlayer(IUPlayer player) {
        Validate.notNull(player, "The player cannot be null.");

        return players.contains(player);
    }

    public void clear() {
        players.clear();
    }

    public static Team getTeamForPlayer(IUPlayer player) {
        return getTeams().stream().filter(t -> t.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public static boolean inSameTeam(IUPlayer player1, IUPlayer player2) {
        return (getTeamForPlayer(player1).equals(getTeamForPlayer(player2)));
    }

    public static Set<Team> getTeams() {
        return Stream.of(values()).collect(Collectors.toSet());
    }

    public static void removePlayerFromTeam(IUPlayer player) {
        Team team = Arrays.stream(values()).filter(t -> t.containsPlayer(player)).findFirst().orElse(null);

        if(team != null)
            team.removePlayer(player);
    }
}
