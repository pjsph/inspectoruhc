package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class LoginListener implements Listener {
    private InspectorUHC plugin;

    public LoginListener(InspectorUHC plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent ev) {
        plugin.getGameManager().updatePlayer(ev.getPlayer());

        if(!plugin.getGameManager().hasStarted()) {
            ev.getPlayer().getInventory().clear();

            ev.getPlayer().setGameMode(ev.getPlayer().isOp() ? GameMode.CREATIVE : GameMode.ADVENTURE);
            ev.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation().add(0, 1, 0));
        } else {
            if(plugin.getTeamManager().getTeamOfPlayer(ev.getPlayer()) != null) {
                ev.getPlayer().loadData();
            } else {
                ev.getPlayer().setGameMode(GameMode.SPECTATOR);
                ev.getPlayer().teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
            }
        }

        plugin.getScoreboardManager().matchInfo();
    }

}
