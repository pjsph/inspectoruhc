package me.pjsph.inspectoruhc.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class KitChosenEvent extends Event {

    private UUID uuid;

    public KitChosenEvent(UUID playerUUID) {
        this.uuid = playerUUID;
    }

    public UUID getPlayerUUID() {
        return uuid;
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
