package me.pjsph.inspectoruhc.spectators;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SpectatorsManager {

    private Map<UUID, GameMode> oldGameModes = new HashMap<>();

    public void setSpectating(final Player player, final boolean spectating) {
        if(player == null) return;

        if(spectating) {
            if(player.getGameMode() != GameMode.SPECTATOR) {
                oldGameModes.put(player.getUniqueId(), player.getGameMode());
                player.setGameMode(GameMode.SPECTATOR);
            }
        } else {
            GameMode previousGameMode = oldGameModes.get(player.getUniqueId());
            player.setGameMode(previousGameMode != null ? previousGameMode : Bukkit.getDefaultGameMode());

            oldGameModes.remove(player.getUniqueId());
        }
    }

    public boolean isSpectating(Player player) {
        return player != null && player.getGameMode() == GameMode.SPECTATOR;
    }

}
