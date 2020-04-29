package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class PlayerDeathEvent extends Event {

    @Getter private IUPlayer player;
    @Getter private org.bukkit.event.entity.PlayerDeathEvent ev;

    public PlayerDeathEvent(IUPlayer player, org.bukkit.event.entity.PlayerDeathEvent ev) {
        this.player = player;
        this.ev = ev;
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
