package me.pjsph.inspectoruhc.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDeathEvent extends Event {

    private Player player;
    private org.bukkit.event.entity.PlayerDeathEvent ev;

    public PlayerDeathEvent(Player player, org.bukkit.event.entity.PlayerDeathEvent ev) {
        this.player = player;
        this.ev = ev;
    }

    public Player getPlayer() {
        return player;
    }

    public org.bukkit.event.entity.PlayerDeathEvent getPlayerDeathEvent() {
        return ev;
    }

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
