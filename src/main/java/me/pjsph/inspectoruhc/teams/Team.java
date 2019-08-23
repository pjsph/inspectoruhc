package me.pjsph.inspectoruhc.teams;

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

    private HashSet<UUID> players = new HashSet<>();

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

    public Set<OfflinePlayer> getPlayers() {
        final Set<OfflinePlayer> playersList = new HashSet<>();

        for(UUID id : players) {
            final Player player = Bukkit.getPlayer(id);
            if(player != null)
                playersList.add(player);

            else
                playersList.add(Bukkit.getOfflinePlayer(id));
        }

        return playersList;
    }

    public Set<Player> getOnlinePlayers() {
        HashSet<Player> playersList = new HashSet<>();

        for(UUID id : players) {
            Player player = Bukkit.getPlayer(id);
            if(player != null && player.isOnline()) {
                playersList.add(player);
            }
        }

        return playersList;
    }

    public Set<UUID> getPlayersUUID() {
        return Collections.unmodifiableSet(players);
    }

    public Set<UUID> getOnlinePlayersUUID() {
        HashSet<UUID> playersList = new HashSet<>();

        for(UUID id : players) {
            Player player = Bukkit.getPlayer(id);
            if(player != null && player.isOnline()) {
                playersList.add(id);
            }
        }

        return playersList;
    }

    public int getSize() {
        return players.size();
    }

    public boolean isEmpty() {
        return getSize() == 0;
    }

    public void addPlayer(OfflinePlayer player) {
        Validate.notNull(player, "The player cannot be null.");

        Team.removePlayerFromTeam(player);

        players.add(player.getUniqueId());
    }

    public void removePlayer(OfflinePlayer player) {
        Validate.notNull(player, "The player cannot be null.");

        players.remove(player.getUniqueId());
    }

    public boolean containsPlayer(Player player) {
        Validate.notNull(player, "The player cannot be null.");

        return players.contains(player.getUniqueId());
    }

    public boolean containsPlayer(UUID id) {
        Validate.notNull(id, "The player cannot be null.");

        return players.contains(id);
    }

    public static Team getTeamForPlayer(OfflinePlayer player) {
        return getTeams().stream().filter(t -> t.getPlayers().contains(player)).findFirst().orElse(null);
    }

    public static boolean inSameTeam(Player player1, Player player2) {
        return (getTeamForPlayer(player1).equals(getTeamForPlayer(player2)));
    }

    public static Set<Team> getTeams() {
        return Stream.of(values()).collect(Collectors.toSet());
    }

    public static void removePlayerFromTeam(OfflinePlayer player) {
        Team team = Arrays.stream(values()).filter(t -> t.containsPlayer(player.getUniqueId())).findFirst().orElse(null);

        if(team != null) {
            team.removePlayer(player);
        }
    }
}
