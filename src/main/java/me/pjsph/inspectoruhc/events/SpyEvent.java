package me.pjsph.inspectoruhc.events;

import me.pjsph.inspectoruhc.game.IUPlayer;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;

public class SpyEvent extends Event {

    @Getter private IUPlayer spy;
    @Getter private IUPlayer spied;

    public SpyEvent(IUPlayer spy, IUPlayer spied) {
        this.spy = spy;
        this.spied = spied;
    }

    private static HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
