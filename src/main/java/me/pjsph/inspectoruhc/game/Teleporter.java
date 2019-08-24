package me.pjsph.inspectoruhc.game;

import me.pjsph.inspectoruhc.tools.Callback;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;

public class Teleporter {
    private final Map<UUID, Location> spawnPoints = new HashMap<>();
    private final Map<UUID, Cage> cages = new HashMap<>();

    private Callback<UUID> onTp = null;
    private Callback<UUID> onTpSuccessful = null;
    private Callback<UUID> onTpFailed = null;
    private Callback<Set<UUID>> onTpProcessFinished = null;

    public void setSpawnForPlayer(UUID playerUUID, final Location spawn) {
        spawnPoints.put(playerUUID, spawn);
    }

    public boolean hasSpawnForPlayer(UUID playerUUID) {
        return spawnPoints.containsKey(playerUUID);
    }

    public Location getSpawnForPlayer(UUID playerUUID) {
        return spawnPoints.get(playerUUID);
    }

    public void setCageForPlayer(UUID player, Cage cage) {
        cages.put(player, cage);
    }

    public boolean hasCageForPlayer(UUID player) {
        return cages.containsKey(player);
    }

    public Cage getCageForPlayer(UUID player) {
        return cages.get(player);
    }

    public boolean teleportPlayer(UUID playerUUID) {
        Player player = Bukkit.getPlayer(playerUUID);
        if(player == null)
            return false;

        Location spawn = spawnPoints.get(playerUUID);

        if(spawn == null)
            return false;

        final Cage cage = cages.get(playerUUID);
        if(cage != null) cage.build();

        player.teleport(spawn);
        return true;
    }

    public Teleporter whenTeleportationOccurs(Callback<UUID> callback) {
        onTp = callback;
        return this;
    }

    public Teleporter whenTeleportationSuccesses(Callback<UUID> callback) {
        onTpSuccessful = callback;
        return this;
    }

    public Teleporter whenTeleportationFails(Callback<UUID> callback) {
        onTpFailed = callback;
        return this;
    }

    public Teleporter whenTeleportationEnds(Callback<Set<UUID>> callback) {
        onTpProcessFinished = callback;
        return this;
    }

    public void startTeleportationProcess() {
        Set<UUID> fails = new HashSet<>();

        for(UUID playerUUID : spawnPoints.keySet()) {
            if(onTp != null) onTp.call(playerUUID);

            if(teleportPlayer(playerUUID)) {
                if(onTpSuccessful != null) onTpSuccessful.call(playerUUID);
            } else {
                if(onTpFailed != null) onTpFailed.call(playerUUID);
                fails.add(playerUUID);
            }
        }

        if(onTpProcessFinished != null) onTpProcessFinished.call(fails);
    }

    public void cleanup() {
        for(final Cage cage : cages.values()) {
            cage.destroy();
        }
    }

}
