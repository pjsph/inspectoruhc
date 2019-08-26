package me.pjsph.inspectoruhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class SpyEvent extends Event {

    private UUID spy;
    private UUID spied;

    public SpyEvent(UUID spy, UUID spied) {
        this.spy = spy;
        this.spied = spied;
    }

    public UUID getSpy() {
        return spy;
    }

    public UUID getSpied() {
        return spied;
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
