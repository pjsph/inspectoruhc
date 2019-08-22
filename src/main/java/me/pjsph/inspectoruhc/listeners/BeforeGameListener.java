package me.pjsph.inspectoruhc.listeners;

import me.pjsph.inspectoruhc.InspectorUHC;
import me.pjsph.inspectoruhc.events.GameStartsEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

public class BeforeGameListener implements Listener {

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent ev) {
        if(InspectorUHC.get().getGameManager().hasStarted()) return;

        if(!ev.getPlayer().isOp()) {
            ev.getPlayer().getInventory().clear();
            ev.getPlayer().getInventory().setArmorContents(null);
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent ev) {
        if(InspectorUHC.get().getGameManager().hasStarted()) return;

        if(!ev.getPlayer().isOp()) {
            ev.setCancelled(true);
        }
    }

    @EventHandler
    public void onGameStarts(GameStartsEvent ev) {
        HandlerList.unregisterAll(this);
    }

}
